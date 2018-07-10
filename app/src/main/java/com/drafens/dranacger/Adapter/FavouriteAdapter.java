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
import com.drafens.dranacger.Tools.ImageManager;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Book;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private List<Book> bookList;
    private Context context;
    private int updateSize;
    private int searchItem;

    public FavouriteAdapter(Context context,int searchItem,List<Book> bookList,int updateSize){
        this.bookList = bookList;
        this.context = context;
        this.updateSize = updateSize;
        this.searchItem = searchItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
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
        holder.lastReadChapter.setText(book.getLastReadChapter());
        holder.lastReadTime.setText(book.getLastReadTime());
        ImageManager.getIcon(context,book.getIcon(),holder.icon);
        if (position<updateSize){
            holder.iv_update.setVisibility(View.VISIBLE);
        }
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
        TextView lastReadChapter;
        TextView lastReadTime;
        ImageView iv_update;

        public ViewHolder(View view){
            super(view);
            bookView = view;
            name = view.findViewById(R.id.tv_name);
            updateChapter = view.findViewById(R.id.tv_update_chapter);
            icon = view.findViewById(R.id.iv_icon);
            author = view.findViewById(R.id.tv_author);
            type = view.findViewById(R.id.tv_type);
            updateTime = view.findViewById(R.id.tv_update_time);
            iv_update = view.findViewById(R.id.iv_update);
            lastReadChapter = view.findViewById(R.id.tv_last_read_chapter);
            lastReadTime = view.findViewById(R.id.tv_last_read_time);
        }
    }
}