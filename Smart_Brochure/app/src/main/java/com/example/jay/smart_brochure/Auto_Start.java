package com.example.jay.smart_brochure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jay on 2015-05-27.
 */
public class Auto_Start extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("BOOT", "Start");
        Database data = new Database(context);
        data.open();
        String onoff = data.getOnoff();
        data.close();
        Intent i = new Intent(context, SearchBLE.class);
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            //Toast.makeText(context, "부팅완료", Toast.LENGTH_SHORT).show();
            if (onoff.equals("1")) {
            //Toast.makeText(context, "체크 온, 자동 시작!", Toast.LENGTH_SHORT).show();
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(i);
            }
        }
    }
}
