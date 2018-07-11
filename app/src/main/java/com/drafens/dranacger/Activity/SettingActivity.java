package com.drafens.dranacger.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.drafens.dranacger.Error.ErrorActivity;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Tools.ApplicationUpdate;

public class SettingActivity extends AppCompatActivity {

    private RadioButton radioHorizon;
    private RadioButton radioVertical;
    private RadioGroup radioReadPattern;
    private Button buttonUpdate;
    private SharedPreferences.Editor editor;
    private String read_patterns;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        read_patterns = preferences.getString("read_patterns","");


        initView();
    }

    private void initView() {
        buttonUpdate = findViewById(R.id.btn_update);
        radioHorizon = findViewById(R.id.radio_horizon);
        radioVertical = findViewById(R.id.radio_vertical);
        radioReadPattern = findViewById(R.id.radio_read_patterns);
        if(read_patterns.equals("vertical")) {
            radioVertical.setChecked(true);
        }else {
            radioHorizon.setChecked(true);
        }
        radioReadPattern.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                editor = preferences.edit();
                switch (i){
                    case R.id.radio_horizon:
                        editor.putString("read_patterns", "horizon");
                        break;
                    case R.id.radio_vertical:
                        editor.putString("read_patterns", "vertical");
                        break;
                }
                editor.apply();
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ApplicationUpdate update = new ApplicationUpdate();
                        try {
                            int result = update.getResult(SettingActivity.this);
                            if (result==1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(SettingActivity.this)
                                                .setTitle("是否更新")
                                                .setMessage("")
                                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                update.getUpdate(SettingActivity.this);
                                                            }
                                                        }).start();
                                                    }
                                                })
                                                .setNegativeButton("否",null)
                                                .show();
                                    }
                                });
                            }
                        } catch (MyNetWorkException e) {
                            ErrorActivity.show(SettingActivity.this,"网络错误");
                        }
                    }
                }).start();
            }
        });
    }
}
