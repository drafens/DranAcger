package com.drafens.dranacger.Novel;

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

import java.util.ArrayList;
import java.util.List;

public class FragmentNovel extends Fragment{
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@Nullable final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel,container,false);
        button = view.findViewById(R.id.bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationUpdate update = new ApplicationUpdate();
                        if (update.getResult(getContext())==1){
                            update.getUpdate(getContext());
                        }
                    }
                }).start();
            }
        });
        return view;
    }
}
