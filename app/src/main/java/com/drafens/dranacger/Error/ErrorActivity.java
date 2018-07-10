package com.drafens.dranacger.Error;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drafens.dranacger.R;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bt_reconnect;
    private TextView textView;
    private String detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        Intent intent = getIntent();
        detail = intent.getStringExtra("detail");
        initView();
    }

    private void initView() {
        bt_reconnect = findViewById(R.id.bt_reconnect);
        textView = findViewById(R.id.text_view);
        textView.setText(detail);
        bt_reconnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_reconnect:
                finish();
                break;
        }
    }

    public static void show(Context context,String detail){
        Intent intent = new Intent(context,ErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("detail", detail);
        context.startActivity(intent);
    }
}
