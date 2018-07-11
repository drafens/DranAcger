package com.drafens.dranacger.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drafens.dranacger.Adapter.ImageVerticalAdapter;
import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Error.ErrorActivity;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Sites;
import com.drafens.dranacger.Tools.FavouriteTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComicImageVertical extends AppCompatActivity{

    private static final int CONTAINER_SIZE = 20;
    private static final int CONTAINER_THRESHOLD = 5;
    private List<String> urlList;
    private List<String> tagList;
    private int nextChapter;
    private int nextPage;
    private int lastChapter;
    private int lastPage;
    private boolean nextable = true;
    private static final int PRE_CACHE_PAGES = 5+3;
    private SwipeRefreshLayout refreshLayout;
    private TextView tv_comic_detail;
    private RecyclerView recyclerView;
    private List<String> image_url;
    private List<Episode> episodeList;
    private Book book;
    private boolean preCacheable=true;
    private boolean isError=true;
    private int searchItem = Book.COMIC;
    private LinearLayoutManager manager;
    private ImageVerticalAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(FavouriteTool.isFavourite(book.getId(),searchItem)!=-1){
            int i=manager.findFirstVisibleItemPosition();
            if (i>=0&&i<CONTAINER_SIZE) {
                String[] arr = tagList.get(i).split("#");
                book.setLastReadChapter(episodeList.get(Integer.parseInt(arr[0])).getName());
                book.setLastReadChapter_id(episodeList.get(Integer.parseInt(arr[0])).getId());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date date = new Date(System.currentTimeMillis());
                book.setLastReadTime("阅读于：" + format.format(date));
                FavouriteTool.update_favourite(FavouriteTool.isFavourite(book.getId(), searchItem), book, searchItem);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_image_vertical);

        Intent intent = getIntent();
        episodeList = (List<Episode>) intent.getSerializableExtra("episode");
        book = (Book) getIntent().getSerializableExtra("book");
        nextChapter = intent.getIntExtra("position",0);
        lastChapter = nextChapter;
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        tv_comic_detail=findViewById(R.id.tv_detail);
        refreshLayout = findViewById(R.id.refresh_layout);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        initData();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = manager.findFirstVisibleItemPosition();
                int j = manager.findLastVisibleItemPosition();
                if(i>=0&&i<CONTAINER_SIZE) {
                    setDetailText(i);
                }
                if (CONTAINER_SIZE-j<=CONTAINER_THRESHOLD&&nextable){
                    nextable = false;
                    getNextData();
                }
            }
        });

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                Sites sites = Sites.getSites(book.getWebsite());
                try {
                    image_url = sites.getImage(episodeList.get(now_chapter).getId());
                } catch (MyNetWorkException e) {
                    image_url = new ArrayList<>();
                    ErrorActivity.show(ComicImageVertical.this,"网络错误");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ImageVerticalAdapter(ComicImageVertical.this,image_url);
                        recyclerView.setAdapter(adapter);
                        setDetailText();
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
                ImageManager.loadImage(ComicImageVertical.this,stringList);
            }
        }).start();*/
    }

    private void initData() {
        tagList = new ArrayList<>();
        urlList = new ArrayList<>();
        nextPage = 0;
        lastPage = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Sites sites = Sites.getSites(book.getWebsite());
                try {
                    int i=0;
                    label:
                    while (true){
                        image_url = sites.getImage(episodeList.get(nextChapter).getId());
                        for (int j = 0; j < image_url.size(); j++,i++) {
                            tagList.add(nextChapter + "#" + j +"#"+ image_url.size());
                            urlList.add(image_url.get(j));
                            if (i>=CONTAINER_SIZE-1){
                                nextPage = j;
                                break label;
                            }
                        }
                        nextChapter+=1;
                    }
                } catch (MyNetWorkException e) {
                    image_url = new ArrayList<>();
                    ErrorActivity.show(ComicImageVertical.this,"网络错误");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ImageVerticalAdapter(ComicImageVertical.this,urlList);
                        recyclerView.setAdapter(adapter);
                        setDetailText(0);
                    }
                });
            }
        }).start();
    }

    private void getNextData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                int i=0;
                nextPage += 1;
                final List<String> newList = new ArrayList<>();
                for (int j=0;j<CONTAINER_THRESHOLD;j++) {
                    tagList.remove(0);
                }
                for (int j=nextPage;j<image_url.size();j++,i++){
                    newList.add(image_url.get(j));
                    tagList.add(nextChapter + "#" + j +"#"+ image_url.size());
                    if (i >= CONTAINER_THRESHOLD-1){
                        nextPage = j;
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    Sites sites = Sites.getSites(book.getWebsite());
                    nextChapter += 1;
                    label:
                    while (true) {
                        try {
                            image_url = sites.getImage(episodeList.get(nextChapter).getId());
                            for (int j = 0; j < image_url.size(); j++, i++) {
                                tagList.add(nextChapter + "#" + j +"#"+ image_url.size());
                                newList.add(image_url.get(j));
                                if (i >= CONTAINER_THRESHOLD - 1) {
                                    nextPage = j;
                                    break label;
                                }
                            }
                            nextChapter += 1;
                        } catch (MyNetWorkException e) {
                            e.printStackTrace();
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateList(newList,true,CONTAINER_THRESHOLD);
                        String[] arr = tagList.get(0).split("#");
                        lastChapter = Integer.parseInt(arr[0]);
                        lastPage = Integer.parseInt(arr[1]);
                        nextable = true;
                    }
                });
            }
        }).start();
    }

    private void getLastData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                int i = 0;
                lastPage -= 1;
                final List<String> newList = new ArrayList<>();
                for (int j = 0; j < CONTAINER_THRESHOLD; j++) {
                    tagList.remove(tagList.size() - 1);
                }
                for (int j = lastPage; j >= 0; j--, i++) {
                    newList.add(0, image_url.get(j));
                    tagList.add(0,lastChapter + "#" + j +"#"+ image_url.size());
                    if (i >= CONTAINER_THRESHOLD - 1) {
                        lastPage = j;
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Sites sites = Sites.getSites(book.getWebsite());
                    lastChapter -= 1;
                    label:
                    while (true) {
                        try {
                            image_url = sites.getImage(episodeList.get(lastChapter).getId());
                            for (int j = image_url.size() - 1; j >= 0; j--, i++) {
                                tagList.add(0,lastChapter + "#" + j +"#"+ image_url.size());
                                newList.add(image_url.get(j));
                                if (i >= CONTAINER_THRESHOLD - 1) {
                                    lastPage = j;
                                    break label;
                                }
                            }
                            lastChapter -= 1;
                        } catch (MyNetWorkException e) {
                            image_url = new ArrayList<>();
                            ErrorActivity.show(ComicImageVertical.this,"网络错误");
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateList(newList, false, CONTAINER_THRESHOLD);
                        String[] arr = tagList.get(tagList.size() - 1).split("#");
                        nextChapter = Integer.parseInt(arr[0]);
                        nextPage = Integer.parseInt(arr[1]);
                        manager.scrollToPositionWithOffset(CONTAINER_THRESHOLD,1);
                        if (refreshLayout.isRefreshing()){
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        }).start();
    }

    /*private void preCache() {
        if (now_chapter<episodeList.size()-1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Sites sites = Sites.getSites(book.getWebsite());
                    try {
                        if (now_chapter<episodeList.size()-1) {
                            pre_image_url = sites.getImage(episodeList.get(now_chapter + 1).getId());
                            isError = false;
                        }
                    } catch (MyNetWorkException e) {
                        isError = true;
                        preCacheable = true;
                    }
                    if(!isError) {
                        ImageManager.loadImage(ComicImageVertical.this, pre_image_url);
                    }
                }
            }).start();
        }
    }*/

    private void setDetailText(int position){
        if (position>=0&&position<tagList.size()) {
            String[] arr = tagList.get(position).split("#");
            String string = " " + book.getName() + " " + episodeList.get(Integer.parseInt(arr[0])).getName() + " " + (Integer.parseInt(arr[1]) + 1) + "/" + Integer.parseInt(arr[2]) + " ";
            tv_comic_detail.setText(string);
        }
    }
}
