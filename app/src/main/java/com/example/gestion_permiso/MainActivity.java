package com.example.gestion_permiso;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_PERMISSIONS = 1;
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_BLUETOOTH = 3;
    public static final int REQUEST_CODE_CONTACTS = 4;
    public static final int REQUEST_CODE_STORAGE = 5;
    public static final int REQUEST_CODE_READ_STORAGE = 6;
    public static final int REQUEST_CODE_BIOMETRIC = 7;
    public static final int REQUEST_CODE_MANAGE_STORAGE = 8;
    public static final int  REQUEST_CODE_IN = 9 ;

    private Button btnCheckPermission;
    private Button btnRequestPermission;
    private Button btnOpenBluetooth;
    private Button btnOpenCamera;
    private TextView tvFingerPrint;
    private TextView tvCamera;
    private TextView tvBT;
    private TextView tvEws;
    private TextView tvRS;
    private TextView tvResponse;
    private TextView tvInternet;
    private TextView tvContacto;
    private Button btnSaveFile;
    private EditText etFileName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        beginObjects();
        btnCheckPermission.setOnClickListener(this::voidCheckPermission);
        btnRequestPermission.setOnClickListener(this::voidRequestPermission);
        btnOpenBluetooth.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BluetoothActivity.class)));
        btnOpenCamera.setOnClickListener(this::btnOpenCamera);
        btnSaveFile.setOnClickListener(this::saveFile);
    }

    private void btnOpenCamera(View view) {
        startActivity(new Intent(MainActivity.this, CameraActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;

                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (!allPermissionsGranted) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Alerta de Permisos")
                                    .setMessage("No ha otorgado los permisos necesarios. Configure el permiso en ajustes.")
                                    .setPositiveButton("Ajustes", (dialogInterface, which) -> {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("Salir", (dialogInterface, which) -> {
                                        dialogInterface.dismiss();
                                        finish();
                                    })
                                    .create().show();
                        } else {
                            Toast.makeText(this, "Permiso denegado. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    updatePermissionStatus(REQUEST_CODE_PERMISSIONS);
                }
            }
        }
    }

    private void voidRequestPermission(View view) {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.USE_BIOMETRIC);
        }


        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        } else {
            Toast.makeText(this, "Todos los permisos ya están concedidos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void voidCheckPermission(View view) {
        int fingerPrint = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC);
        int camera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int bluet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH);
        int contacts = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        int internet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        int Ews = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int Rs = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        tvFingerPrint.setText("Status Permission fingerprint: " + fingerPrint);
        tvCamera.setText("Status Permission camera: " + camera);
        tvBT.setText("Status Permission BLUETOOTH: " + bluet);
        tvEws.setText("Status Permission EWS: " + Ews);
        tvRS.setText("Status Permission RS: " + Rs);
        tvInternet.setText("Status Permission internet: " + internet);
        tvContacto.setText("Status Permission Contacto: " + contacts);
        btnRequestPermission.setEnabled(true);
    }

    private void saveFile(View view) {
        String fileName = etFileName.getText().toString().trim();
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un nombre de archivo.", Toast.LENGTH_SHORT).show();
            return;
        }

        String batteryLevel = String.valueOf(getBatteryLevel());
        String androidVersion = android.os.Build.VERSION.RELEASE;

        String content = "Nombre del estudiante: " + fileName + "\n" +
                "Nivel de batería: " + batteryLevel + "%\n" +
                "Versión de Android: " + androidVersion;

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri collection = MediaStore.Files.getContentUri("external");
        Uri fileUri = getContentResolver().insert(collection, values);

        if (fileUri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri)) {
                if (outputStream != null) {
                    outputStream.write(content.getBytes());
                    Toast.makeText(this, "Archivo guardado correctamente en Descargas.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se pudo abrir el flujo de salida.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar el archivo.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error al crear el archivo.", Toast.LENGTH_SHORT).show();
        }
    }




    private int getBatteryLevel() {

        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (batteryManager != null) {

            int level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            return level;
        }
        return -1;
    }


    private void updatePermissionStatus(int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                tvCamera.setText("Status Permission camera: " + ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA));
                break;
            case REQUEST_CODE_BLUETOOTH:
                tvBT.setText("Status Permission BLUETOOTH: " + ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH));
                break;
            case REQUEST_CODE_CONTACTS:
                tvContacto.setText("Status Permission Contacto: " + ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS));
                break;
            case REQUEST_CODE_STORAGE:
                tvEws.setText("Status Permission EWS: " + ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE));
                break;
            case REQUEST_CODE_READ_STORAGE:
                tvRS.setText("Status Permission RS: " + ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE));
                break;
            case REQUEST_CODE_BIOMETRIC:
                tvFingerPrint.setText("Status Permission fingerprint: " + ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC));
                break;
            case REQUEST_CODE_IN:
                tvInternet.setText("Status Permission fingerprint: " + ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET));
                break;
        }
    }
    private void beginObjects() {
        btnCheckPermission = findViewById(R.id.btnCheckPermission);
        btnRequestPermission = findViewById(R.id.btnRequestPermission);
        tvBT = findViewById(R.id.tvBT);
        tvCamera = findViewById(R.id.tvCamera);
        tvEws = findViewById(R.id.tvEws);
        tvRS = findViewById(R.id.tvRS);
        tvInternet = findViewById(R.id.tvInternet);
        tvResponse = findViewById(R.id.tvResponse);
        tvContacto = findViewById(R.id.tvContacto);
        tvFingerPrint = findViewById(R.id.tvDactilar);
        btnRequestPermission.setEnabled(false);
        btnSaveFile = findViewById(R.id.btnSaveFile);
        etFileName = findViewById(R.id.etFileName);
        btnOpenBluetooth = findViewById(R.id.btnOpenBluetooth);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
    }
}