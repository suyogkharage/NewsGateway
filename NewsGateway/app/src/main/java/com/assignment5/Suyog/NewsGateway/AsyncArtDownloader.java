package com.assignment5.Suyog.NewsGateway;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncArtDownloader extends android.os.AsyncTask<String, Void, String> {

    //private static final String TAG = "AsyncTask";
    //private Exception exceptionToBeThrown = null;
    private MyService myService;


    private final String newsUrl = "https://newsapi.org/v2/top-headlines?";
    private final String APIKey = "200deeb5604c4fc48dbf21ec65bd6c22";
    private String id = "";

    public AsyncArtDownloader(MyService ms) {
        myService = ms;
    }

    @Override
    protected String doInBackground(String... strings) {

        id = strings[0];

        StringBuilder sb = new StringBuilder();

        Uri.Builder buildURL = Uri.parse(newsUrl).buildUpon();
        buildURL.appendQueryParameter("sources", strings[0]);
        buildURL.appendQueryParameter("apiKey", APIKey);

        String CompleteUrl = buildURL.build().toString();
        //System.out.println("CompleteUrl articles"+CompleteUrl);
        try {
            URL url = new URL(CompleteUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();

    }

    private ArrayList<ArticleList> parseJSON(String s) {
        ArrayList<ArticleList> DescriptionList = new ArrayList<>();

        try{
            JSONObject jsonObj = new JSONObject(s);
            JSONArray offices = jsonObj.getJSONArray("articles");

            for(int i = 0 ; i < offices.length() ; i++){
                String Author = offices.getJSONObject(i).optString("author");
                String Title = offices.getJSONObject(i).optString("title");
                String Desc = offices.getJSONObject(i).optString("description");
                String ImageUrl = offices.getJSONObject(i).optString("urlToImage");
                String Url = offices.getJSONObject(i).optString("url");
                String publishedAt = offices.getJSONObject(i).optString("publishedAt");
                DescriptionList.add(new ArticleList(id,Author,Title,Desc,ImageUrl,Url,publishedAt));

            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return DescriptionList;

    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<ArticleList> DescriptionList = parseJSON(s);
        myService.updateDescription(DescriptionList);
    }
}

