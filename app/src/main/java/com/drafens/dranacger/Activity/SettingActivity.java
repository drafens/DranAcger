package com.drafens.dranacger.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.drafens.dranacger.R;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch switch_read_patterns;
    private String read_patterns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        read_patterns = pref.getString("read_patterns","");

        initView();
    }

    private void initView() {
        switch_read_patterns = findViewById(R.id.switch_read_patterns);
        switch_read_patterns.setOnCheckedChangeListener(this);
        if(read_patterns.equals("vertical")) {
            switch_read_patterns.setChecked(true);
        }else {
            switch_read_patterns.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.switch_read_patterns:
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                if (b){
                    editor.putString("read_patterns", "vertical");
                }else {
                    editor.putString("read_patterns", "horizon");
                }
                editor.apply();
                break;
        }
    }
}
