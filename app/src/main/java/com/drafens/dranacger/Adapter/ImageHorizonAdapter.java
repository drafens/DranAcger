package com.drafens.dranacger.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.drafens.dranacger.Tools.ImageManager;
import com.drafens.dranacger.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageHorizonAdapter extends PagerAdapter {
    private Context context;
    private List<String> images;
    private SparseArray<View> cacheView;

    public ImageHorizonAdapter(Context context, List<String> images){
        this.images = images;
        this.context = context;
        cacheView = new SparseArray<>(images.size());
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = cacheView.get(position);

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_image_horizon,container,false);
            view.setTag(position);
            final ImageView image = view.findViewById(R.id.iv_comic);
            final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.fresh);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ImageManager.getImageOnlyFromNet(context,images.get(position),image,refreshLayout);
                    /*Picasso.with(context).load(images.get(position)).networkPolicy(NetworkPolicy.NO_CACHE).into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, R.string.refresh_success,Toast.LENGTH_LONG).show();
                            refreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, R.string.refresh_error,Toast.LENGTH_LONG).show();
                            refreshLayout.setRefreshing(false);
                        }
                    });*/
                }
            });

            if(images.get(position).equals("0")){
                image.setImageBitmap(null);
            }
            ImageManager.getImage(context,images.get(position),image);
            cacheView.put(position,view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
