package com.drafens.dranacger.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.drafens.dranacger.Animation.FragmentAnimation;
import com.drafens.dranacger.Book;
import com.drafens.dranacger.Comic.FragmentComic;
import com.drafens.dranacger.Adapter.MyFragmentPagerAdapter;
import com.drafens.dranacger.Sites;
import com.drafens.dranacger.Tools.ImageManager;
import com.drafens.dranacger.Novel.FragmentNovel;
import com.drafens.dranacger.R;
import com.drafens.dranacger.Tools.DynamicLine;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, TextView.OnEditorActionListener ,RadioGroup.OnCheckedChangeListener{

    private Button btn_comic;
    private Button btn_animation;
    private Button btn_novel;
    private List<Button> buttonList;
    private Button btn_search;
    private Button btn_setting;
    private RadioGroup radioGroup;
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private EditText et_search;
    private DynamicLine cursor;
    private float startX,stopX;
    private int lineWidth;
    private String siteItem="";
    private String[] SiteGroup;
    private int searchItem;
    private static final String[] hintArray={"动漫","漫画","小说"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
    }

    private void initView(){
        ImageManager.init(this);
        btn_comic=findViewById(R.id.btn_comic);
        btn_animation=findViewById(R.id.btn_animation);
        btn_novel=findViewById(R.id.btn_novel);
        buttonList=new ArrayList<>();
        buttonList.add(btn_animation);
        buttonList.add(btn_comic);
        buttonList.add(btn_novel);
        btn_search=findViewById(R.id.btn_search);
        btn_setting=findViewById(R.id.btn_setting);
        radioGroup = findViewById(R.id.radio_group);
        et_search=findViewById(R.id.et_search);
        viewPager = findViewById(R.id.view_pager);
        cursor = findViewById(R.id.cursor);
        radioGroup.setOnCheckedChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        btn_comic.setOnClickListener(this);
        btn_animation.setOnClickListener(this);
        btn_novel.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        et_search.setOnEditorActionListener(this);
        fragments=new ArrayList<>();
        FragmentAnimation fragmentAnimation = new FragmentAnimation();
        fragments.add(fragmentAnimation);
        FragmentComic fragmentComic = new FragmentComic();
        fragments.add(fragmentComic);
        FragmentNovel fragmentNovel = new FragmentNovel();
        fragments.add(fragmentNovel);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        initCursor();
        for (int i = 0; i< Sites.ANIMATION_GROUP.length; i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(Sites.ANIMATION_GROUP[i]);
            radioGroup.addView(radioButton);
            if(i==0&&!radioButton.isChecked()){
                radioButton.setChecked(true);
            }
        }
    }

    private void initCursor(){
        lineWidth = DynamicLine.getScreenWidth(this)/3;
        startX = 0;
        stopX = startX + lineWidth;
        cursor.updateView(startX,stopX);
        updateCursor(0);
    }

    private void updateCursor(int item){
        for (int i=0;i<buttonList.size();i++){
            if(i==item){
                buttonList.get(i).setTextColor(Color.parseColor("#1155ff"));
            }else {
                buttonList.get(i).setTextColor(Color.parseColor("#000000"));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_comic:
                viewPager.setCurrentItem(Book.COMIC);
                updateCursor(Book.COMIC);
                break;
            case R.id.btn_animation:
                viewPager.setCurrentItem(Book.ANIMATION);
                updateCursor(Book.ANIMATION);
                break;
            case R.id.btn_novel:
                viewPager.setCurrentItem(Book.NOVEL);
                updateCursor(Book.NOVEL);
                break;
            case R.id.btn_search:
                if (et_search.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"请输入内容",Toast.LENGTH_LONG).show();
                }else {
                    searchAcg(et_search.getText().toString());
                }
                break;
            case R.id.btn_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
        searchItem = position;
        radioGroup.removeAllViews();
        switch (searchItem){
            case Book.ANIMATION:
                SiteGroup = Sites.ANIMATION_GROUP;
                break;
            case Book.COMIC:
                SiteGroup = Sites.COMIC_GROUP;
                break;
            case Book.NOVEL:
                SiteGroup = Sites.NOVEL_GROUP;
                break;
        }
        for (int i = 0; i< SiteGroup.length; i++){
            RadioButton radioButton = new RadioButton(MainActivity.this);
            radioButton.setText(SiteGroup[i]);
            radioGroup.addView(radioButton);
            if(i==0&&!radioButton.isChecked()){
                radioButton.setChecked(true);
            }
        }
        updateCursor(position);
        startX=position*lineWidth;
        stopX=startX+lineWidth;
        cursor.updateView(startX,stopX);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void searchAcg(String searchContent){
        Intent intent = new Intent(this,SearchResult.class);
        intent.putExtra("search_content", searchContent);
        intent.putExtra("search_item", searchItem);
        intent.putExtra("site_item", siteItem);
        if(siteItem.isEmpty()){
            Toast.makeText(this,"请选择搜索源",Toast.LENGTH_LONG).show();
        }else {
            startActivity(intent);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i==EditorInfo.IME_ACTION_SEARCH){
            if (textView.getText().toString().isEmpty()){
                Toast.makeText(MainActivity.this,"请输入内容",Toast.LENGTH_LONG).show();
            }else {
                searchAcg(textView.getText().toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        RadioButton radioButton = radioGroup.findViewById(i);
        siteItem = radioButton.getText().toString();
    }
}
