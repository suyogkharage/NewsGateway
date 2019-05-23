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

public class AsyncSrcDownloader extends android.os.AsyncTask<String, Void, String> {


    private MainActivity mainActivity;
    private final String newsUrl = "https://newsapi.org/v2/sources";
    private final String APIKey = "200deeb5604c4fc48dbf21ec65bd6c22";
    private ArrayList<String> nCategory;


    public AsyncSrcDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {

        if(strings[0].equals("specific")){
            StringBuilder sb = new StringBuilder();

            Uri.Builder buildURL = Uri.parse(newsUrl).buildUpon();
            buildURL.appendQueryParameter("apiKey", APIKey);
            buildURL.appendQueryParameter("language", "en");
            buildURL.appendQueryParameter("country", "us");
            buildURL.appendQueryParameter("category", strings[1]);

            String CompleteUrl = buildURL.build().toString();
            //System.out.println("CompleteUrl sources"+CompleteUrl);

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
        }else{

            StringBuilder sb = new StringBuilder();

            Uri.Builder buildURL = Uri.parse(newsUrl).buildUpon();
            buildURL.appendQueryParameter("apiKey", APIKey);
            buildURL.appendQueryParameter("language", "en");
            buildURL.appendQueryParameter("country", "us");
            String urlToUse = buildURL.build().toString();

            try {
                URL url = new URL(urlToUse);
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

    }

    private ArrayList<NewsList> parseJSON(String s) {
        ArrayList<NewsList> nList = new ArrayList<>();
        nCategory = new ArrayList<>();
        nCategory.add("all");
        try{
            JSONObject jsonObj = new JSONObject(s);
            JSONArray offices = jsonObj.getJSONArray("sources");

            for(int i = 0 ; i < offices.length() ; i++){
                String Id = offices.getJSONObject(i).optString("id");
                String Name = offices.getJSONObject(i).optString("name");
                String Category = offices.getJSONObject(i).optString("category");
                nList.add(new NewsList(Id,Name,Category));
                if(!nCategory.contains(Category)){
                    nCategory.add(Category);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return nList;
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<NewsList> nList = parseJSON(s);
        mainActivity.updateData(nList, nCategory);
    }
}

