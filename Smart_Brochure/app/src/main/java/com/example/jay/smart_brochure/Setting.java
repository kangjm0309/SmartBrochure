package com.example.jay.smart_brochure;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


public class Setting extends Activity {

    public Database data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        data = new Database(this);

/*        // db에서 onoff 설정 상태 불러와서 설정
        data.open();
        int check = data.getOnoff();
        if (check == 1){
            onOff.setText("On");
            checkOnOff = true;
        }
        else {
            onOff.setText("Off");
            checkOnOff = false;
        }
        data.close();*/
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
/*            case R.id.onOff:
                if (checkOnOff == false){
                    checkOnOff = true;
                    onOff.setText("On");
                    data.open();
                    data.editOnoff("1");
                    data.close();
                }
                else{
                    checkOnOff = false;
                    onOff.setText("Off");
                    data.open();
                    data.editOnoff("0");
                    data.close();
                }
                break;*/
        }
    }
}
