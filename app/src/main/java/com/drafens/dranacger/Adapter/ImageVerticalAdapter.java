package com.drafens.dranacger.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.drafens.dranacger.Tools.ImageManager;
import com.drafens.dranacger.R;

import java.util.List;

public class ImageVerticalAdapter extends RecyclerView.Adapter<ImageVerticalAdapter.ViewHolder> {
    private Context context;
    private List<String> images;

    public ImageVerticalAdapter(Context context, List<String> images){
        this.images = images;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_vertical, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ImageManager.getImage(context, images.get(position), holder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public ViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.iv_comic);
        }
    }

    public void updateList(List<String> newData,boolean flag,int count) {
        if (newData != null) {
            if(flag) {
                for (int i=0;i<count;i++) {
                    images.remove(i);
                }
                images.addAll(newData);
                notifyItemRangeRemoved(0,count);
            }else {
                images.addAll(0,newData);
                for (int i=0;i<count;i++) {
                    images.remove(images.size()-1);
                }
                notifyItemRangeInserted(0,count);
            }
        }
    }
}
