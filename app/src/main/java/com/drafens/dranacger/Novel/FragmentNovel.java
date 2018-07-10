package com.drafens.dranacger.Novel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.drafens.dranacger.Activity.SettingActivity;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Tools.ApplicationUpdate;

import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public class FragmentNovel extends Fragment{
    private Button button1;
    private Button button2;

    @Nullable
    @Override
    public View onCreateView(@Nullable final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel,container,false);
        button1 = view.findViewById(R.id.bt1);
        button2 = view.findViewById(R.id.bt2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), SettingActivity.class);
                //startActivity(intent);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ApplicationUpdate update = new ApplicationUpdate();
                        if (update.getResult(getContext())==1){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("是否更新")
                                            .setMessage("")
                                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            update.getUpdate(getContext());
                                                        }
                                                    }).start();
                                                }
                                            })
                                            .setNegativeButton("否",null)
                                            .show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
