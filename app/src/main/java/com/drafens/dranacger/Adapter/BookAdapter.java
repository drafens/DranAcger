package com.drafens.dranacger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drafens.dranacger.Activity.EpisodeResult;
import com.drafens.dranacger.Book;
import com.drafens.dranacger.Tools.ImageManager;
import com.drafens.dranacger.R;

import java.util.List;

public class BookAdapter  extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> bookList;
    private Context context;
    private int searchItem;
    public BookAdapter(Context context,List<Book> bookList,int searchItem){
        this.bookList = bookList;
        this.context = context;
        this.searchItem = searchItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Book book = bookList.get(position);
                Intent intent = new Intent(context,EpisodeResult.class);
                intent.putExtra("book",book);
                intent.putExtra("search_item",searchItem);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Book book = bookList.get(position);
        holder.name.setText(book.getName());
        holder.updateChapter.setText(book.getUpdateChapter());
        holder.updateTime.setText(book.getUpdateTime());
        holder.author.setText(book.getAuthor());
        holder.type.setText(book.getType());
        ImageManager.getIcon(context,book.getIcon(),holder.icon);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View bookView;
        TextView name;
        TextView updateChapter;
        ImageView icon;
        TextView updateTime;
        TextView author;
        TextView type;

        public ViewHolder(View view){
            super(view);
            bookView = view;
            name = view.findViewById(R.id.tv_name);
            updateChapter = view.findViewById(R.id.tv_update_chapter);
            icon = view.findViewById(R.id.iv_icon);
            author = view.findViewById(R.id.tv_author);
            type = view.findViewById(R.id.tv_type);
            updateTime = view.findViewById(R.id.tv_update_time);
        }
    }
}
