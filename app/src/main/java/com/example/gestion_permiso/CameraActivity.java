package com.example.gestion_permiso;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button btnTakePhoto;
    private Button btnDeletePhoto;
    private Button btnBack;
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        beginObject();
        btnTakePhoto.setOnClickListener(this::takePhoto);
        btnDeletePhoto.setOnClickListener(this::deletePhoto);
        btnBack.setOnClickListener(v -> finish());
        requestCameraPermission();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void takePhoto(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePhoto(View view) {
        imgPreview.setImageDrawable(null);
        btnDeletePhoto.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(photo);
            btnDeletePhoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Alerta de Permisos")
                        .setMessage("No ha otorgado los permisos a la cámara. Configure el permiso en ajustes.")
                        .setPositiveButton("Ajustes", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Salida", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            finish();
                        })
                        .create().show();
            }
        }
    }

    private void beginObject() {
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnDeletePhoto = findViewById(R.id.btnDeletePhoto);
        btnBack = findViewById(R.id.btnBack);
        imgPreview = findViewById(R.id.imgPreview);
        btnDeletePhoto.setVisibility(View.GONE);
    }
}