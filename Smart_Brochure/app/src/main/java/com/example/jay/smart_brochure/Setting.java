package com.example.jay.smart_brochure;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class Setting extends Activity {

    public Database data;
    private Button btn_onoff;
    private Boolean checkOnOff;
    private TextView auto_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btn_onoff = (Button) findViewById(R.id.btn_onoff);
        auto_search = (TextView) findViewById(R.id.auto_search);
        data = new Database(this);

        // db에서 onoff 설정 상태 불러와서 설정
        data.open();
        String check = data.getOnoff();
        Log.d("Setting", "check : : "+ check);
        if (check.equals("1")){
            btn_onoff.setText("끄기");
            auto_search.setText("자동 검색 : 켜져있음");
            checkOnOff = true;
        }
        else {
            btn_onoff.setText("켜기");
            auto_search.setText("자동 검색 : 꺼져있음");
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
                    btn_onoff.setText("끄기");
                    auto_search.setText("자동 검색 : 켜져있음");

                    data.open();
                    data.editOnoff("1");
                    Log.d("세팅", "checkonoff == false 일떄" + data.getOnoff());
                    data.close();
                }
                else{
                    checkOnOff = false;
                    btn_onoff.setText("켜기");
                    auto_search.setText("자동 검색 : 꺼져있음");
                    data.open();
                    data.editOnoff("0");
                    Log.d("세팅", "checkonoff == true 일떄" + data.getOnoff());
                    data.close();
                }
                break;
        }
    }
}
