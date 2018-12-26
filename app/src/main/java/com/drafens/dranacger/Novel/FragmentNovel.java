package com.drafens.dranacger.Novel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.drafens.dranacger.Error.ErrorActivity;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Tools.ApplicationUpdate;

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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ApplicationUpdate update = new ApplicationUpdate();
                        try {
                            int result = update.getResult(getContext());
                            if (result==1){
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
                        } catch (MyNetWorkException e) {
                            ErrorActivity.show(getContext(),"网络错误");
                        }
                    }
                }).start();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return view;
    }
}
