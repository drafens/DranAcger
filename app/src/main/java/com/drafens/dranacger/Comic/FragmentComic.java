package com.drafens.dranacger.Comic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drafens.dranacger.Book;
import com.drafens.dranacger.Error.MyJsonFormatException;
import com.drafens.dranacger.Error.MyJsonObjectEmptyException;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Tools.FavouriteTool;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Adapter.FavouriteAdapter;
import com.drafens.dranacger.Sites;

import java.util.ArrayList;
import java.util.List;

public class FragmentComic extends Fragment {
    private RecyclerView recyclerView;
    private TextView textView;
    private List<Book> bookList;
    private List<Book> noUpdateList;
    private List<Book> bookListAfterUpdate;
    private boolean updateable=false;
    private FavouriteAdapter adapter;
    private int searchItem = Book.COMIC;
    private boolean isError = false;//网络错误

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        textView = view.findViewById(R.id.text_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        try {
            bookList = FavouriteTool.getBookList(searchItem);
        } catch (MyJsonObjectEmptyException e) {
            textView.setVisibility(View.VISIBLE);
        } catch (MyJsonFormatException e) {
            e.printStackTrace();
        }
        setAdapter();
        setUpdateAdapter();
    }

    private void setAdapter(){
        getUpdateList(bookList);
        int updateSize = bookListAfterUpdate.size()-noUpdateList.size();
        adapter = new FavouriteAdapter(getContext(),searchItem,bookListAfterUpdate,updateSize);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }

    private void setUpdateAdapter(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bookList != null) {
                    for (int i = 0; i < bookList.size(); i++) {
                        Book book = bookList.get(i);
                        Sites sites = Sites.getSites(book.getWebsite());
                        try {
                            book = sites.getBook(book, book.getLastReadChapter(), book.getLastReadChapter_id(), book.getLastReadTime());
                        } catch (MyNetWorkException e) {
                            isError = true;
                        }
                        if (!isError) {
                            if (book.getUpdateTime() != bookList.get(i).getUpdateTime()) {
                                updateable = true;
                                bookList.set(i, book);
                            }
                        }
                    }
                    if (updateable) {
                        getUpdateList(bookList);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int updateSize = bookListAfterUpdate.size() - noUpdateList.size();
                                adapter = new FavouriteAdapter(getContext(), searchItem, bookListAfterUpdate, updateSize);
                                adapter.notifyDataSetChanged();

                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void getUpdateList(List<Book> books){
        bookListAfterUpdate = new ArrayList<>();
        noUpdateList = new ArrayList<>();
        if (books!=null){
            for (int i=0;i<books.size();i++){
                Book book = books.get(i);
                if(FavouriteTool.isUpdate(book.getLastReadChapter_id(),book.getUpdateChapter_id())){
                    FavouriteTool.update_favourite(i,book,searchItem);
                    bookListAfterUpdate.add(book);
                }else{
                    noUpdateList.add(book);
                }
            }
            for (int i=0;i<noUpdateList.size();i++){
                bookListAfterUpdate.add(noUpdateList.get(i));
            }
        }
    }
}