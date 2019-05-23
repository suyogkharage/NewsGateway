package com.assignment5.Suyog.NewsGateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static final String SERVICE_DATA = "SERVICE_DATA";
    static final String SOURCE_ID = "SOURCE_ID";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";

    private DrawerLayout sDrawerLayout;
    private ListView sDrawerList;
    private ActionBarDrawerToggle sDrawerToggle;

    public List<NewsList> newsList = new ArrayList<>();
    public ArrayList<String> items= new ArrayList<>();
    private ArrayList<String> catList = new ArrayList<>();
    public List<ArticleList> artList = new ArrayList<>();
    private ArrayList<String> items1 = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    private Menu menu;
    private Boolean isRotate = false;
    private NewsReceiver newsReceiver;
    private ArrayAdapter myAdapter;
    private int position = -1;
    private int pIndex = -1;
    List<Fragment> fList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        sDrawerList = (ListView) findViewById(R.id.left_drawer);
        myAdapter = new ArrayAdapter<>(this,
                R.layout.drawer_list_item, items);
        sDrawerList.setAdapter(myAdapter);
        sDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );


        new AsyncSrcDownloader(MainActivity.this).executeOnExecutor(AsyncSrcDownloader.THREAD_POOL_EXECUTOR,"all");
        sDrawerToggle = new ActionBarDrawerToggle(
                this,sDrawerLayout,R.string.drawer_open,R.string.drawer_close
        );
        invalidateOptionsMenu();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setBackground(getResources().getDrawable(R.drawable.newspapers));

        Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();

        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter1);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("NEWSLIST", (Serializable) newsList);
        outState.putSerializable("CATEGORYLIST", catList);
        outState.putSerializable("ARTICLELIST", (Serializable) artList);
        outState.putSerializable("ITEMS", items);
        isRotate = true;
        outState.putBoolean("STATE_FLAG",isRotate);
        outState.putInt("POSITION_FRAGMENT",pager.getCurrentItem());
        outState.putInt("POSITION",position);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        newsList = (ArrayList<NewsList>) savedInstanceState.getSerializable("NEWSLIST");
        catList = (ArrayList<String>) savedInstanceState.getSerializable("CATEGORYLIST");
        artList.removeAll(artList);
        artList = (ArrayList<ArticleList>) savedInstanceState.getSerializable("ARTICLELIST");
        items1 = (ArrayList<String>) savedInstanceState.getSerializable("ITEMS");
        isRotate = savedInstanceState.getBoolean("STATE_FLAG");

        position =  savedInstanceState.getInt("POSITION");
        pIndex = savedInstanceState.getInt("POSITION_FRAGMENT");
        if(position!=-1) {
            setTitle(newsList.get(position).getName());
            reDoFragments();
            pager.setCurrentItem(pIndex);
        }

    }

    private List<Fragment> getFragments() {
        fList = new ArrayList<Fragment>();
        return fList;
    }

    private void selectItem(int position) {

        setTitle(items.get(position));
        int j = 0;
        for (j = 0 ; j<newsList.size();j++){
            if(newsList.get(j).getName().equals(items.get(position))){
                break;
            }
        }
        position = j;
        String src = newsList.get(j).getId();
        Intent newIntent = new Intent();
        newIntent.setAction(MyService.ACTION_MSG_TO_SERVICE);
        newIntent.putExtra(SOURCE_ID, src);
        sendBroadcast(newIntent);
        pager.setBackgroundColor(Color.LTGRAY);
        sDrawerLayout.closeDrawer(sDrawerList);
    }

    private void reDoFragments() {

        pager.setBackgroundColor(Color.LTGRAY);

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();
        int count = artList.size();

        for (int i = 0; i < count; i++) {

            String Title = artList.get(i).getTitle();
            String Author = artList.get(i).getAuthor();
            String Desc = artList.get(i).getDescription();
            String Imageurl = artList.get(i).getimageUrl();
            String Time = artList.get(i).getPublishedAt();
            String Url = artList.get(i).getUrl();
            int c = i+1;
            fragments.add(MyFragment.newInstance(MainActivity.this, Title,Author,Desc,Imageurl,Time,c,count,Url));
            pageAdapter.notifyChangeInPosition(i);
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        sDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (sDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        items.clear();
        for(int k=0;k<newsList.size();k++){
            String Category =  newsList.get(k).getCategory();
            String Name =  newsList.get(k).getName();
            if(Category.equals(item.toString())){
                items.add(Name);
            }else if(item.toString().equals("all")){
                items.add(Name);
            }
        }
        myAdapter.notifyDataSetChanged();
        return true;
    }


    public void updateData(ArrayList<NewsList> cList, ArrayList<String> newCategory) {


        items.clear();
        newsList.clear();
        newsList = cList;

        if(!isRotate){
            for(int i=0;i<newsList.size();i++){
                String name =  newsList.get(i).getName();
                items.add(name);
            }
        } else {
            items.addAll(items1);
        }

        myAdapter.notifyDataSetChanged();
        catList.clear();
        catList = newCategory;
        Collections.sort(catList);
        myAdapter.notifyDataSetChanged();
        this.invalidateOptionsMenu();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for (int k = 0; k < catList.size(); k++) {
            menu.add(R.menu.menu, Menu.NONE, 0, catList.get(k));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, MyService.class);
        stopService(intent);
        super.onDestroy();
    }


    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
                      return baseId + position;
        }

        public void notifyChangeInPosition(int n) {

            baseId += getCount() + n;
        }
    }

    class NewsReceiver extends BroadcastReceiver implements Serializable {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_NEWS_STORY)) {{
                Bundle bundle = intent.getExtras();
                artList = (ArrayList<ArticleList>)bundle.getSerializable(MyService.SERVICE_DATA);
                reDoFragments();
            }
            }
        }
    }
}
