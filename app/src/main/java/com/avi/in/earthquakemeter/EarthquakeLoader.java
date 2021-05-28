package com.avi.in.earthquakemeter;

import android.content.Context;
import android.util.Log;

import android.content.AsyncTaskLoader;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;



//Takes URL - connects to server , retrieves InputStream data , converts it into String JSON data
//Parses list from the Json and displays the list to user.

public class EarthquakeLoader extends AsyncTaskLoader<LinkedList<QuakeDetails>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private URL mUrl;

    public EarthquakeLoader(Context context, URL url) {
        super(context);

        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }



    @Override
    public LinkedList<QuakeDetails> loadInBackground() {


        String jsonResponse = "";
        InputStream receivedData = null;
        HttpURLConnection urlConnection = null;

        if (mUrl == null)
            return null;

        try {
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                receivedData = urlConnection.getInputStream();
                jsonResponse = convertRawDataToString(receivedData);

            }

            else {
                Log.e("Error response code : ", String.valueOf(urlConnection.getResponseCode()));
            }

        } catch (IOException e) {
            Log.e("Problem retrieving the earthquake JSON results.", String.valueOf(e));
        } finally {

            if (urlConnection != null)
                urlConnection.disconnect();

            if (receivedData != null) {
                try {
                    receivedData.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return getLinkedListOfEarthquakes(jsonResponse);
    }


    // Converts InputStream data received from server to a String of readable characters

    protected String convertRawDataToString(InputStream rawData) {

        InputStreamReader isr = new InputStreamReader(rawData);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line = "";

        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (line != null) {
            sb.append(line);
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    //Takes String data from upper convertRawDataToString method , do JSON Parsing and returns a
    //LinkedList of earthquake data

    protected LinkedList<QuakeDetails> getLinkedListOfEarthquakes(String jsonResponse) {

        LinkedList<QuakeDetails> earthquakeList = new LinkedList<>();


        JSONObject rootObj;


        {
            try {


                rootObj = new JSONObject(jsonResponse);

                JSONArray earthquakeEntries = rootObj.getJSONArray("features");

                for (int i = 0; i < earthquakeEntries.length(); i++) {

                    JSONObject currentEntry = earthquakeEntries.getJSONObject(i);

                    JSONObject properties = currentEntry.getJSONObject("properties");

                    earthquakeList.add(new QuakeDetails(properties.getDouble("mag"), properties.getString("place"), properties.getLong("time"), properties.getLong("time"), properties.getString("url")));
                }

            } catch (JSONException e) {
                Log.e("LOG_TAG", "Error parsing data from JSON", e);
            }
        }


        return earthquakeList;
    }

}


