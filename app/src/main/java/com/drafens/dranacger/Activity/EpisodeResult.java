package com.drafens.dranacger.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Adapter.EpisodeAdapter;
import com.drafens.dranacger.Error.ErrorActivity;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Tools.FavouriteTool;
import com.drafens.dranacger.Tools.ImageManager;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Sites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EpisodeResult extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private RecyclerView recyclerView;
    private List<Episode> episodeList;
    private TextView name;
    private TextView update_chapter;
    private ImageView icon;
    private TextView update_time;
    private TextView author;
    private TextView type;
    private Button bt_read;
    private Switch switch_order;
    private CheckBox check_favourite;
    private Book book;
    private LinearLayoutManager manager;
    private int episodePosition;
    private int searchItem;
    private boolean isError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_result);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        searchItem = bundle.getInt("search_item");
        book = (Book) getIntent().getSerializableExtra("book");
        initView();
    }

    private void initView() {
        name = findViewById(R.id.tv_name);
        update_chapter = findViewById(R.id.tv_update_chapter);
        icon = findViewById(R.id.iv_icon);
        author = findViewById(R.id.tv_author);
        type = findViewById(R.id.tv_type);
        update_time = findViewById(R.id.tv_update_time);
        bt_read = findViewById(R.id.bt_read);
        switch_order = findViewById(R.id.switch_order);
        check_favourite = findViewById(R.id.check_favourite);
        recyclerView = findViewById(R.id.recycler_episode);

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        switch_order.setOnCheckedChangeListener(this);
        check_favourite.setOnCheckedChangeListener(this);

        if (FavouriteTool.isFavourite(book.getId(),searchItem)!=-1) {
            check_favourite.setChecked(true);
            try {
                book=FavouriteTool.getBookList(searchItem).get(FavouriteTool.isFavourite(book.getId(),searchItem));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        name.setText(book.getName());
        update_chapter.setText(book.getUpdateChapter());
        update_time.setText(book.getUpdateTime());
        author.setText(book.getAuthor());
        type.setText(book.getType());
        ImageManager.getIcon(EpisodeResult.this,book.getIcon(),icon);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Sites sites = Sites.getSites(book.getWebsite());
                try {
                    episodeList = sites.getEpisode(book.getId());
                } catch (MyNetWorkException e) {
                    episodeList = new ArrayList<>();
                    isError = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorActivity.show(EpisodeResult.this,"网络错误");
                            finish();
                        }
                    });
                }
                if(!isError) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (FavouriteTool.isFavourite(book.getId(), searchItem) != -1) {
                                episodePosition = FavouriteTool.getEpisodeId(book.getLastReadChapter_id(), episodeList);
                            } else {
                                episodePosition = 0;
                            }
                            bt_read.setText(episodeList.get(episodePosition).getName());
                            EpisodeAdapter adapter = new EpisodeAdapter(EpisodeResult.this, episodeList, book, searchItem);
                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(EpisodeResult.this, DividerItemDecoration.VERTICAL));
                            bt_read.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent;
                                    switch (searchItem) {
                                        case Book.COMIC:
                                            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                                            String read_patterns = pref.getString("read_patterns","");
                                            if(read_patterns.equals("vertical")) {
                                                intent = new Intent(EpisodeResult.this, ComicImageVertical.class);
                                            }else {
                                                intent = new Intent(EpisodeResult.this, ComicImageHorizon.class);
                                            }
                                            break;
                                        case Book.ANIMATION:
                                            intent = new Intent(EpisodeResult.this, AnimationVideo.class);
                                            break;
                                        default:
                                            intent = new Intent(EpisodeResult.this, MainActivity.class);
                                            break;
                                    }
                                    intent.putExtra("position", episodePosition);
                                    intent.putExtra("episode", (Serializable) episodeList);
                                    intent.putExtra("book", book);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()){
            case R.id.switch_order:
                if (b){
                    manager.setStackFromEnd(false);
                    manager.setReverseLayout(false);
                }else {
                    manager.setStackFromEnd(true);
                    manager.setReverseLayout(true);
                }
                break;
            case R.id.check_favourite:
                if (b){
                    if (FavouriteTool.isFavourite(book.getId(),searchItem)==-1) {
                        FavouriteTool favouriteTool = new FavouriteTool();
                        favouriteTool.write_favourite(book,searchItem);
                    }
                }else {
                    int i = FavouriteTool.isFavourite(book.getId(),searchItem);
                    if (i!=-1) {
                        FavouriteTool.delete_favourite(i,searchItem);
                    }
                }
                break;
        }
    }
}
