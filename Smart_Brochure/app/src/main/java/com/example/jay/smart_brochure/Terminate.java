package com.example.jay.smart_brochure;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jay on 2015-06-02.
 */
public class Terminate extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Terminate", "꺼져라좀");
        stopService(new Intent(this, SearchBLE.class));
        stopSelf();
        Log.d("Terminate", "꺼졌나?");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "꺼진다꺼진다", Toast.LENGTH_SHORT).show();
    }
}
