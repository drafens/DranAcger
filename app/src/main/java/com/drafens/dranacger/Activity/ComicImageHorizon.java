package com.drafens.dranacger.Activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.drafens.dranacger.Adapter.ImageHorizonAdapter;
import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Error.ErrorActivity;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Tools.FavouriteTool;
import com.drafens.dranacger.Tools.ImageManager;
import com.drafens.dranacger.Sites;
import com.drafens.dranacger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComicImageHorizon extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private static final int PRE_CACHE_PAGES = 5+3;
    private TextView tv_comic_detail;
    private ViewPager viewPager;
    private List<String> image_url;
    private List<String> pre_image_url;
    private int now_page=1;
    private int now_chapter=0;
    private List<Episode> episodeList;
    private Book book;
    private boolean clickable=true;
    private boolean preCacheable=true;
    private boolean isError=false;
    private int searchItem = Book.COMIC;
    private ImageHorizonAdapter adapter;

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
        setContentView(R.layout.activity_comic_image_horizon);

        Intent intent = getIntent();
        episodeList = (List<Episode>) intent.getSerializableExtra("episode");
        book = (Book) getIntent().getSerializableExtra("book");
        now_chapter = intent.getIntExtra("position",0);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        tv_comic_detail=findViewById(R.id.tv_detail);
        viewPager.addOnPageChangeListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Sites sites = Sites.getSites(book.getWebsite());
                try {
                    image_url = Sites.getUrlsList(sites.getImage(episodeList.get(now_chapter).getId()));
                } catch (MyNetWorkException e) {
                    List<String> list = new ArrayList<>();
                    list.add("0");
                    image_url = Sites.getUrlsList(list);
                    ErrorActivity.show(ComicImageHorizon.this,"网络错误");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        now_page = 1;
                        adapter = new ImageHorizonAdapter(ComicImageHorizon.this,image_url);
                        viewPager.setAdapter(adapter);
                        viewPager.setCurrentItem(now_page);
                        setDetailText();
                        clickable=true;
                    }
                });
                List <String> stringList = new ArrayList<>();
                for (int i=0;i<image_url.size()-1;i++){
                    if (i==1||i==2){
                        stringList.add("0");
                    }else{
                        stringList.add(image_url.get(i));
                    }
                }
                ImageManager.loadImage(ComicImageHorizon.this,stringList);
            }
        }).start();
    }

    private void preCache() {
        if (now_chapter<episodeList.size()-1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Sites sites = Sites.getSites(book.getWebsite());
                    try {
                        if (now_chapter<episodeList.size()-1) {
                            pre_image_url = Sites.getUrlsList(sites.getImage(episodeList.get(now_chapter + 1).getId()));
                            isError = false;
                        }
                    } catch (MyNetWorkException e) {
                        isError = true;
                        preCacheable = true;
                    }
                    if(!isError) {
                        ImageManager.loadImage(ComicImageHorizon.this, pre_image_url);
                    }
                }
            }).start();
        }
    }

    private void setDetailText(){
        if (now_page>0&&now_page<image_url.size()-1) {
            String string = " "+book.getName()+" "+episodeList.get(now_chapter).getName()+" "+(now_page) + "/" + (image_url.size()-2)+" ";
            tv_comic_detail.setText(string);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
        now_page=position;
        setDetailText();
        if(image_url.size()-now_page<PRE_CACHE_PAGES&&preCacheable){
            //预加载
            preCacheable = false;
            preCache();
        }
        if (clickable){
            if (now_page == image_url.size() - 1) {
                //下一章
                if (now_chapter<episodeList.size()-1) {
                    preCacheable = true;
                    clickable = false;
                    now_chapter += 1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!isError) {
                                image_url = pre_image_url;
                            }else {
                                Sites sites = Sites.getSites(book.getWebsite());
                                try {
                                    image_url = Sites.getUrlsList(sites.getImage(episodeList.get(now_chapter).getId()));
                                } catch (MyNetWorkException e) {
                                    List<String> list = new ArrayList<>();
                                    list.add("0");
                                    image_url = Sites.getUrlsList(list);
                                    ErrorActivity.show(ComicImageHorizon.this,"网络错误");
                                }
                            }
                            now_page = 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageHorizonAdapter adapter = new ImageHorizonAdapter(ComicImageHorizon.this,image_url);
                                    viewPager.setAdapter(adapter);
                                    viewPager.setCurrentItem(now_page);
                                    setDetailText();
                                    clickable=true;
                                }
                            });
                        }
                    }).start();
                }else{
                    viewPager.setCurrentItem(image_url.size()-2);
                    Toast.makeText(ComicImageHorizon.this,"没有更多了",Toast.LENGTH_LONG).show();
                }
            } else if (now_page == 0) {
                //上一章
                if (now_chapter>0) {
                    clickable = false;
                    now_chapter -= 1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Sites sites = Sites.getSites(book.getWebsite());
                            try {
                                image_url = Sites.getUrlsList(sites.getImage(episodeList.get(now_chapter).getId()));
                            } catch (MyNetWorkException e) {
                                List<String> list = new ArrayList<>();
                                list.add("0");
                                image_url = Sites.getUrlsList(list);
                                ErrorActivity.show(ComicImageHorizon.this,"网络错误");
                            }
                            now_page = image_url.size() - 2;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageHorizonAdapter adapter = new ImageHorizonAdapter(ComicImageHorizon.this,image_url);
                                    viewPager.setAdapter(adapter);
                                    viewPager.setCurrentItem(now_page);
                                    setDetailText();
                                    clickable=true;
                                }
                            });
                        }
                    }).start();
                }else {
                    viewPager.setCurrentItem(1);
                    Toast.makeText(ComicImageHorizon.this,"没有更多了",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
