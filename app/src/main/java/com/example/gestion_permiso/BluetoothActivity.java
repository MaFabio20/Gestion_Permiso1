package com.example.gestion_permiso;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_BT = 2;
    private BluetoothAdapter bluetoothAdapter;
    private TextView tvBluetoothStatus;
    private TextView tvPairedDevices;
    private Button btnEnableBT;
    private Button btnDisableBT;
    private Button btnListDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);


        beginObject();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnEnableBT.setOnClickListener(v -> enableBluetooth());
        btnDisableBT.setOnClickListener(v -> disableBluetooth());
        btnListDevices.setOnClickListener(v -> listPairedDevices());
    }

    private void enableBluetooth() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSION_BT);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                tvBluetoothStatus.setText("Bluetooth ya está activado");
            }
        }
    }

    private void disableBluetooth() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSION_BT);
        } else {

            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
                tvBluetoothStatus.setText("Desactivando Bluetooth...");
                Toast.makeText(this, "Bluetooth desactivado", Toast.LENGTH_SHORT).show();


                new Handler().postDelayed(() -> {
                    if (!bluetoothAdapter.isEnabled()) {
                        tvBluetoothStatus.setText("Bluetooth desactivado correctamente");
                    } else {
                        tvBluetoothStatus.setText("No se pudo desactivar el Bluetooth");
                    }
                }, 2000);
            } else {
                tvBluetoothStatus.setText("Bluetooth ya está desactivado");
                Toast.makeText(this, "Bluetooth ya está desactivado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void listPairedDevices() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSION_BT);
        } else {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            StringBuilder devicesList = new StringBuilder("Dispositivos emparejados:\n");

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    devicesList.append(device.getName()).append(" - ").append(device.getAddress()).append("\n");
                }
            } else {
                devicesList.append("No hay dispositivos emparejados");
            }

            tvPairedDevices.setText(devicesList.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Se necesita el permiso de Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void beginObject() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        tvBluetoothStatus = findViewById(R.id.tvBluetoothStatus);
        tvPairedDevices = findViewById(R.id.tvPairedDevices);
        btnEnableBT = findViewById(R.id.btnEnableBT);
        btnDisableBT = findViewById(R.id.btnDisableBT);
        btnListDevices = findViewById(R.id.btnListDevices);
    }
}
