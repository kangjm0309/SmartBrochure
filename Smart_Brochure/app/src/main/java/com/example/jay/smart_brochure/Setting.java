package com.example.jay.smart_brochure;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class Setting extends Activity {

    public Database data;
    private ImageView btn_onoff;
    private Boolean checkOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btn_onoff = (ImageView) findViewById(R.id.btn_onoff);
        data = new Database(this);
        TextView bSet = (TextView)findViewById(R.id.beacon_set);
        TextView bEx = (TextView)findViewById(R.id.beacon_ex);
        TextView bTitle = (TextView)findViewById(R.id.setting_title);
        Typeface rbm = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface rbr = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Regular.ttf");
        Typeface rbb = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Bold.ttf");
        bTitle.setTypeface(rbb);
        bSet.setTypeface(rbm);
        bEx.setTypeface(rbr);

        // db에서 onoff 설정 상태 불러와서 설정
        data.open();
        String check = data.getOnoff();
        Log.d("Setting", "check : : "+ check);
        if (check.equals("1")){
            btn_onoff.setImageResource(R.drawable.on_button);
            checkOnOff = true;
        }
        else {
            btn_onoff.setImageResource(R.drawable.off_button);
            checkOnOff = false;
        }
        data.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btn_onoff:
                if (!checkOnOff){
                    checkOnOff = true;
                    btn_onoff.setImageResource(R.drawable.on_button);

                    data.open();
                    data.editOnoff("1");
                    Log.d("세팅", "checkonoff == false 일떄" + data.getOnoff());
                    data.close();
                }
                else{
                    checkOnOff = false;
                    btn_onoff.setImageResource(R.drawable.off_button);
                    data.open();
                    data.editOnoff("0");
                    Log.d("세팅", "checkonoff == true 일떄" + data.getOnoff());
                    data.close();
                }
                break;
        }
    }
}
