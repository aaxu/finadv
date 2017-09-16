package com.example.aaron.hackintoit;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button close;
    EditText editText;
    TextView nonNumberError;
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
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
    }

    private PopupWindow pw;
    protected void addCost(View v) {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) findViewById(R.id.popup_1));
            editText = layout.findViewById(R.id.money_input);
            nonNumberError = layout.findViewById(R.id.number_format_error_message);
            pw = new PopupWindow(layout, 1250, 750, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            close = layout.findViewById(R.id.update_money);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMoney(view);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMoney(View view) {
        try {
            TextView textView = findViewById(R.id.money_left);
            Float moneyRemaining = Float.parseFloat(textView.getText().toString().replaceAll("\\$", ""));
            moneyRemaining -= Float.parseFloat(editText.getText().toString().replaceAll("\\$", ""));
            if (moneyRemaining < 0) {
                textView.setText("-$" + String.format("%.2f", Math.abs(moneyRemaining)));
            } else {
                textView.setText("$" + String.format("%.2f", moneyRemaining));
            }
            nonNumberError.setText("That is not a dollar amount...");
            pw.dismiss();
        } catch (NumberFormatException e) {
            nonNumberError.setText("That is not a dollar amount...");
        }

    }

}
