package com.drafens.dranacger.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drafens.dranacger.Adapter.BookAdapter;
import com.drafens.dranacger.Book;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Error.ErrorActivity;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Sites;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textView;
    private int searchItem;
    private String siteItem;
    private String searchContent;
    private List<Book> bookList;
    private boolean isEmpty = false;
    private boolean isError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        searchItem = bundle.getInt("search_item");
        siteItem = bundle.getString("site_item");

        searchContent = bundle.getString("search_content");
        getSearchResult();
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        textView = findViewById(R.id.text_view);
        LinearLayoutManager manager = new LinearLayoutManager(SearchResult.this);
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    private void getSearchResult() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Sites sites = Sites.getSites(siteItem);
                try {
                    bookList = sites.getSearch(searchContent);
                }catch (MyNetWorkException e){
                    bookList = new ArrayList<>();
                    isError = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorActivity.show(SearchResult.this,"网络错误");
                            finish();
                        }
                    });
                }
                if(!isError) {
                    if (bookList.size() <= 0) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isEmpty) {
                                BookAdapter adapter = new BookAdapter(SearchResult.this, bookList, searchItem);
                                recyclerView.setAdapter(adapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(SearchResult.this, DividerItemDecoration.VERTICAL));
                            } else {
                                textView.setText("没有搜索到结果");
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
