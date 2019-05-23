package com.assignment5.Suyog.NewsGateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {

    static final String SERVICE_DATA = "SERVICE_DATA";
    private boolean run = true;
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    private ServiceReceiver receiver;

    public List<ArticleList> artList = new ArrayList<>();
    public MyService() {}


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        receiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(receiver, filter);
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(run) {
                    if (artList.isEmpty()) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.ACTION_NEWS_STORY);

                        int count = artList.size();
                        for (int i = 0; i < count; i++) {
                            String Title = artList.get(i).getTitle();
                            String Author = artList.get(i).getAuthor();
                            String Desc = artList.get(i).getDescription();
                            String Imageurl = artList.get(i).getimageUrl();
                            String Time = artList.get(i).getPublishedAt();
                            String Url = artList.get(i).getUrl();
                            intent.putExtra("title",Title);
                            intent.putExtra("author",Author);
                            intent.putExtra("desc",Desc);
                            intent.putExtra("imageurl",Imageurl);
                            intent.putExtra("time",Time);
                            intent.putExtra("url",Url);
                            sendBroadcast(intent);
                        }
                        artList.clear();
                    }

                   }
            }
        }).start();


        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updateDescription(ArrayList<ArticleList> list) {
        if(list == null) return;
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.SERVICE_DATA, list);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        run = false;
        super.onDestroy();
    }


    class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MSG_TO_SERVICE)) {
                if(intent.hasExtra(MainActivity.SOURCE_ID)) {
                    String source = intent.getStringExtra(MainActivity.SOURCE_ID);
                    new AsyncArtDownloader(MyService.this).executeOnExecutor(AsyncSrcDownloader.THREAD_POOL_EXECUTOR,source);
                }
            }
        }
    }

}
