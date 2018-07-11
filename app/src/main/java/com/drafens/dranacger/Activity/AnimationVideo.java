package com.drafens.dranacger.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Error.ErrorActivity;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Tools.FavouriteTool;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Sites;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnimationVideo extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private VideoView videoView;
    private TextView textView;
    private RadioGroup radioGroup;
    private Button btnLock;
    private List<Episode> episodeList;
    private Book book;
    private int now_chapter;
    private int searchItem = Book.ANIMATION;
    private String animation_detail;
    private List<String> url;
    private int serveItem=0;
    private boolean isError = false;
    private boolean isLocked = false;
    private boolean btnLockHasChanged = false;
    private MyMediaController myMediaController;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(FavouriteTool.isFavourite(book.getId(),searchItem)!=-1){
            book.setLastReadChapter(episodeList.get(now_chapter).getName());
            book.setLastReadChapter_id(episodeList.get(now_chapter).getId());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date = new Date(System.currentTimeMillis());
            book.setLastReadTime("阅读于："+format.format(date));
            FavouriteTool.update_favourite(FavouriteTool.isFavourite(book.getId(),searchItem),book,searchItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_video);

        Intent intent = getIntent();
        episodeList = (List<Episode>) intent.getSerializableExtra("episode");
        book = (Book) getIntent().getSerializableExtra("book");
        now_chapter = intent.getIntExtra("position",0);
        initView();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        videoView = findViewById(R.id.video_view);
        textView = findViewById(R.id.text_view);
        radioGroup = findViewById(R.id.radio_group);
        btnLock = findViewById(R.id.btn_lock);
        btnLock.setText("一");
        radioGroup.setOnCheckedChangeListener(AnimationVideo.this);
        myMediaController = new MyMediaController(AnimationVideo.this);
        videoView.setMediaController(myMediaController);
        videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
        btnLock.setOnClickListener(this);
        animation_detail=" "+book.getName()+" "+episodeList.get(now_chapter).getName();
        textView.setText(animation_detail);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                isError = true;
                radioGroup.setVisibility(View.VISIBLE);
                Toast.makeText(AnimationVideo.this,"无法播放，请换源",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        final String string = episodeList.get(now_chapter).getId();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Sites sites = Sites.getSites(book.getWebsite());
                try {
                    url = sites.getVideo(string);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i=0;i<url.size();i++){
                                RadioButton radioButton = new RadioButton(AnimationVideo.this);
                                radioButton.setTextColor(Color.parseColor("#ffffff"));
                                radioButton.setText("源"+i);
                                radioGroup.addView(radioButton);
                                if(i==0){
                                    radioButton.setChecked(true);
                                }
                            }
                            playVideo();
                        }
                    });
                } catch (MyNetWorkException e) {
                    url=new ArrayList<>();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorActivity.show(AnimationVideo.this,"网络错误");
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        myMediaController.show();
        isError=false;
        RadioButton radioButton = radioGroup.findViewById(i);
        serveItem = Integer.parseInt(radioButton.getText().toString().substring(1));
        playVideo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_lock:
                if (isLocked){
                    isLocked=false;
                    btnLock.setText("一");
                    btnLockHasChanged = true;
                    myMediaController.show();
                }else {
                    isLocked=true;
                    btnLock.setText("〇");
                    myMediaController.hide();
                }
                break;
        }
    }

    private class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (now_chapter<episodeList.size()-1) {
                now_chapter += 1;
                final String string = episodeList.get(now_chapter).getId();
                Toast.makeText(AnimationVideo.this, "自动播放下一话", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Sites sites = Sites.getSites(book.getWebsite());
                        try {
                            url = sites.getVideo(string);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playVideo();
                                }
                            });
                        } catch (MyNetWorkException e) {
                            url = new ArrayList<>();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ErrorActivity.show(AnimationVideo.this,"网络错误");
                                }
                            });
                        }
                    }
                }).start();
            }else {
                Toast.makeText(AnimationVideo.this, "没有更多了", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void playVideo(){
        videoView.setVideoPath(url.get(serveItem));
        videoView.start();
    }

    private class MyMediaController extends MediaController{

        public MyMediaController(Context context) {
            super(context);
        }

        @Override
        public void show(final int timeout) {
            btnLockHasChanged = true;
            if (!isLocked) {
                super.show(timeout);
                animation_detail = " " + book.getName() + " " + episodeList.get(now_chapter).getName();
                textView.setText(animation_detail);
                btnLock.setVisibility(VISIBLE);
                textView.setVisibility(VISIBLE);
                radioGroup.setVisibility(VISIBLE);
            }else {
                if (btnLock.getVisibility()==VISIBLE){
                    btnLock.setVisibility(GONE);
                }else {
                    btnLock.setVisibility(VISIBLE);
                    if (btnLock.getText()=="〇") {
                        btnLockHasChanged = false;
                        btnLock.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //MyMediaController.super.hide();
                                if (!btnLockHasChanged) {
                                    btnLock.setVisibility(GONE);
                                }
                            }
                        }, timeout);
                    }
                }
            }
        }

        @Override
        public void hide() {
            super.hide();
            btnLock.setVisibility(GONE);
            if (!isError) {
                textView.setVisibility(GONE);
                radioGroup.setVisibility(GONE);
            }
        }
    }
}
