package com.example.user.shopsap;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

class RequestTask extends AsyncTask<String, Void, String> {
    String HTML_response= "";

    OnTaskFinished onOurTaskFinished;


    public RequestTask(OnTaskFinished onTaskFinished)
    {
        onOurTaskFinished = onTaskFinished;
    }
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls)
    {
        try
        {
            URL url = new URL(urls[0]); // enter your url here which to download

            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            while ((inputLine = br.readLine()) != null)
            {
                // System.out.println(inputLine);
                HTML_response += inputLine;
            }
            br.close();

            System.out.println("Done");

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return HTML_response;
    }

    @Override
    protected void onPostExecute(String feed)
    {
        onOurTaskFinished.onFeedRetrieved(feed);
    }
}