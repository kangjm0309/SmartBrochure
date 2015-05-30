package com.example.jay.smart_brochure;
//Info 탭에서 리스트 중 하나 클릭 했을 떄 페이지
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class Info_Clicked extends Activity {

    ArrayList<String> explanation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__clicked);

        explanation = new ArrayList<String>();
        explanation.add("위치설명");
        explanation.add("상세설명");
        TextView textView = (TextView) findViewById(R.id.ex_text);
        textView.setText(explanation.get(0) +"\n"+ explanation.get(1));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info__clicked, menu);
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
}
