package com.example.presensiguru;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class QrCodeAdapter extends RecyclerView.Adapter<QrCodeAdapter.QrCodeViewHolder> {
    private List<QrCode> qrCodeList;
    private Context context;

    public QrCodeAdapter(List<QrCode> qrCodeList, Context context) {
        this.qrCodeList = qrCodeList != null ? qrCodeList : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public QrCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_code, parent, false);
        return new QrCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QrCodeViewHolder holder, int position) {
        QrCode qrCode = qrCodeList.get(position);
        holder.textViewQrCodeData.setText(qrCode.getData());

        // Menghasilkan QR code dan memuatnya ke dalam ImageView
        Bitmap qrCodeBitmap = generateQRCode(qrCode.getData());
        if (qrCodeBitmap != null) {
            holder.imageViewQrCode.setImageBitmap(qrCodeBitmap);
        } else {
            holder.imageViewQrCode.setImageResource(R.drawable.placeholder_image); // Gambar placeholder jika gagal
        }

        holder.imageViewQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "QR Code: " + qrCode.getData(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.imageViewQrCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((QrCodeGuruActivity) context).deleteQRCode(qrCode.getId());
                return true;
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((QrCodeGuruActivity) context).deleteQRCode(qrCode.getId());
                qrCodeList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, qrCodeList.size());
                Toast.makeText(context, "QR Code dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengunduh QR code sebagai gambar dengan nama file berdasarkan data QR code
                downloadQrCodeImage(holder, qrCode);
            }
        });
    }

    // Metode untuk menghasilkan QR code
    private Bitmap generateQRCode(String data) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null; // Kembalikan null jika gagal
        }
    }

    private void downloadQrCodeImage(QrCodeAdapter.QrCodeViewHolder holder, QrCode qrCode) {
        Bitmap bitmap = ((BitmapDrawable) holder.imageViewQrCode.getDrawable()).getBitmap();

        try {
            // Gunakan data dari QR code sebagai nama file
            String fileName = qrCode.getData() + ".png";

            // Path untuk menyimpan di direktori Download
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadDir, fileName);

            // Simpan file
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Memberitahukan pengguna dan membuat file tersedia di sistem
            Toast.makeText(context, "QR Code diunduh: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Kirim broadcast agar file dapat diakses di galeri/download manager
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Gagal mengunduh QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return qrCodeList.size();
    }

    public void updateQRCodeList(List<QrCode> newQrCodeList) {
        qrCodeList.clear();
        qrCodeList.addAll(newQrCodeList);
        notifyDataSetChanged();
    }

    static class QrCodeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewQrCodeData;
        ImageView imageViewQrCode;
        ImageButton btnDelete;
        ImageButton btnDownload;

        public QrCodeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQrCodeData = itemView.findViewById(R.id.tvQrCodeData);
            imageViewQrCode = itemView.findViewById(R.id.imageViewQrCode);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}
