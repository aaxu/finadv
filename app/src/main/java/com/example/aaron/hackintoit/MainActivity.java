package com.example.aaron.hackintoit;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity {

    Button close;
    Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_main);

        //Set the toolbar to show on the screen
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        Button button = (Button) findViewById(R.id.add_cost);

    }

    protected void addCost(View v) {
        showPopup();
    }

    private PopupWindow pw;
    private void showPopup() {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) findViewById(R.id.popup_1));
            pw = new PopupWindow(layout, 300, 370, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            close = (Button) layout.findViewById(R.id.close_popup);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pw.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
