package com.example.jay.smart_brochure;

import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 메인엑티비티. 탭 뷰 구성.
 */


public class MainActivity extends TabActivity {
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler;
    private Boolean mScanning;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public ArrayList<String[]> devices = new ArrayList<String[]>();

    Database data = new Database(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);

        startActivity(new Intent(this, Splash.class)); // Splash
        setContentView(R.layout.activity_main);

        // Use this check to determine whether BLE is supported on the device. Then
// you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "This app requires device supporting BLE.", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This app requires a bluetooth capable phone", Toast.LENGTH_SHORT).show();
        }
        else{
            // When bluetooth is off.
            if(!mBluetoothAdapter.isEnabled()){
                turnOnBT();
            }
        }

        Resources res = getResources();
            TabHost tabHost = getTabHost();
            TabHost.TabSpec spec;
            Intent intent;

        intent = new Intent(this, My.class);
        spec = tabHost.newTabSpec("My")
                .setIndicator("My", res.getDrawable(android.R.drawable.sym_action_chat))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(this, Info.class);
        spec = tabHost.newTabSpec("Info")
                .setIndicator("Info", res.getDrawable(android.R.drawable.sym_action_email))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(this, Around.class);
        spec = tabHost.newTabSpec("Around")
                .setIndicator("Around", res.getDrawable(android.R.drawable.sym_action_call))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(this, Setting.class);
        spec = tabHost.newTabSpec("Settings")
                .setIndicator("Settings", res.getDrawable(android.R.drawable.sym_action_call))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

    // Method to request the bluetooth to be on.
    private void turnOnBT() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "This app requires the bluetooth to be on", Toast.LENGTH_SHORT).show();
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            Log.i("ttt", "onLeScan");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 스캔해서 device에서 주소랑 이름 불러와서 리스트에 뿌려줌
                    String[] s = { device.getAddress(), device.getName() };
                    devices.add(s);
                }
            });
        }
    };

    // 스캔 시작 및 중단
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.i("ttt", " mScanning = false;");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Log.i("ttt", "mBluetoothAdapter.stopLeScan(mLeScanCallback);");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.i("ttt", " startLeScan");
        } else {
            Log.i("ttt", " scanLeDevice, else");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}