package com.drafens.dranacger.Tools;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.OkHttpClient;

public class ImageManager {
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/drafens/cache/";
    private static final String imgPath = "img";
    private static final String iconPath = "icon";
    private static final long iconSize = 7*1000*1000;
    private static final long imgSize = 43*1000*1000;

    public static void getIcon(Context context,String url,ImageView imageView){
        File file = new File(PATH+iconPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new okhttp3.Cache(file, iconSize))
                .build();
        new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build()
                .load(url)
                .priority(Picasso.Priority.HIGH)
                .into(imageView);
    }

    public static void getImage(Context context,String url,ImageView imageView){
        if (!url.isEmpty()) {
            Picasso.with(context).load(url).into(imageView);
        }
    }

    public static void loadImage(Context context,List<String> urlList){
        for (int i=0;i<urlList.size();i++){
            if (!urlList.get(i).equals("0")){
                Picasso.with(context).load(urlList.get(i)).fetch();
            }
        }
    }

    public static void init(Context context) {
        final String path = PATH + imgPath;
        Picasso.Builder builder = new Picasso.Builder(context);
        Picasso picasso = builder
                .downloader(new OkHttp3Downloader(new File(path),imgSize))
                .build();
        try {
            Picasso.setSingletonInstance(picasso);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*public static void getImageOnlyFromNet(final Context context, String url, ImageView imageView, final SwipeRefreshLayout refreshLayout) {
        Picasso.with(context).load(url).networkPolicy(NetworkPolicy.NO_CACHE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                refreshLayout.setRefreshing(false);
                Toast.makeText(context, R.string.refresh_success,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError() {
                Toast.makeText(context, R.string.refresh_error,Toast.LENGTH_LONG).show();
            }
        });
    }*/
}
