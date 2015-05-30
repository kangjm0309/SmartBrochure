package com.example.jay.smart_brochure;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Jay on 2015-05-27.
 */
public class BackPressed {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressed(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }


    private void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.",
                Toast.LENGTH_SHORT);
        toast.show();
    }

}