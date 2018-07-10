package com.drafens.dranacger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drafens.dranacger.Activity.AnimationVideo;
import com.drafens.dranacger.Activity.ComicImageHorizon;
import com.drafens.dranacger.Activity.ComicImageVertical;
import com.drafens.dranacger.Activity.EpisodeResult;
import com.drafens.dranacger.Activity.MainActivity;
import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.R;

import java.io.Serializable;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private List<Episode> episodeList;
    private Book book;
    private Context context;
    private int searchItem;

    public EpisodeAdapter(Context context,List<Episode> episodeList,Book book,int searchItem){
        this.episodeList = episodeList;
        this.book = book;
        this.context = context;
        this.searchItem = searchItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.episodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent;
                switch (searchItem){
                    case Book.COMIC:
                        SharedPreferences pref = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                        String read_patterns = pref.getString("read_patterns","");
                        if(read_patterns.equals("vertical")) {
                            intent = new Intent(context, ComicImageVertical.class);
                        }else {
                            intent = new Intent(context, ComicImageHorizon.class);
                        }
                        break;
                    case Book.ANIMATION:
                        intent = new Intent(context,AnimationVideo.class);
                        break;
                    default:
                        intent = new Intent(context,MainActivity.class);
                        break;
                }
                intent.putExtra("position", position);
                intent.putExtra("episode",(Serializable) episodeList);
                intent.putExtra("book",book);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        holder.name.setText(episode.getName());
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View episodeView;
        TextView name;

        public ViewHolder(View view){
            super(view);
            episodeView = view;
            name = view.findViewById(R.id.tv_name);
        }
    }
}

