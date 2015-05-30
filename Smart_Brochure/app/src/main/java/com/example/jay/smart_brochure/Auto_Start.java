package com.example.jay.smart_brochure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jay on 2015-05-27.
 */
public class Auto_Start extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("BOOT", "Start");
        if ( action.equals(Intent.ACTION_BOOT_COMPLETED) ) {
            Toast.makeText(context, "자동으로 시작!", Toast.LENGTH_SHORT).show();
/*            ComponentName cn = new ComponentName(context.getPackageName(), MainActivity.class.getName());
            ComponentName maName = context.startService(new Intent().setComponent(cn));
            if(maName == null) {
                Log.i("ttt", "실행 못함" + cn.toString());
            }*/
            Intent i = new Intent(context, SearchBLE.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startService(i);
        }
/*
        else if ( action.equals(Intent.ACTION_SCREEN_OFF ) ) {
            Toast.makeText(context, "스크린오프", Toast.LENGTH_LONG).show();
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else
            Toast.makeText(context, "몰라 씨발!", Toast.LENGTH_SHORT).show();
*/

    }
}
