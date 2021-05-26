package com.avi.in.earthquakemeter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {


    //Defining USGS url by which request will be made

    private final String USGS_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmagnitude=4&limit=200";

    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating new URL object with String USGS_URL

        URL url = null;
        try {
            url = new URL(USGS_URL);
        } catch (MalformedURLException e) {
            Log.e("Problem building URL ", String.valueOf(e));
        }

        //Calling EarthquakeAsyncTask in background thread

        EartuquakeAsyncTask retrieveEarthquakeList = new EartuquakeAsyncTask();
        retrieveEarthquakeList.execute(url);

    }



    //Takes URL - connects to server , retrieves InputStream data , converts it into String JSON data
    //Parses Linkedlist from the Json and displays the list to user.

    private class EartuquakeAsyncTask extends AsyncTask<URL,Integer, LinkedList<QuakeDetails>>{

        @Override
        protected LinkedList<QuakeDetails> doInBackground(URL... urls) {

            String jsonResponse = null;
            InputStream receivedData = null;
            HttpURLConnection urlConnection = null;

            if (urls.length < 1 || urls[0] == null)
                return null;

            try {
                urlConnection = (HttpURLConnection) urls[0].openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() != 200) {
                    Log.e("Error response code : ", String.valueOf(urlConnection.getResponseCode()));
                    return null;
                }

                receivedData = urlConnection.getInputStream();
                jsonResponse = convertRawDataToString(receivedData);


            } catch (IOException e) {
                Log.e( "Problem retrieving the earthquake JSON results.", String.valueOf(e));
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



        //Executes after doInBackground ends and returns a LinkedList.
        //Takes the List to display on screen as ListView

        @Override
        protected void onPostExecute(LinkedList<QuakeDetails> earthquakes) {

            if(adapter != null)
            adapter.clear();

            ListView earthquakeList = findViewById(R.id.list);


            adapter  = new EarthquakeAdapter(MainActivity.this,earthquakes);


            earthquakeList.setAdapter(adapter);



            earthquakeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    earthquakeList.setHapticFeedbackEnabled(true);
                    earthquakeList.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);



                    QuakeDetails currentEarthquake = earthquakes.get(position);
                    String url = currentEarthquake.getUrl();


                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;



                    Intent openURL = new Intent(Intent.ACTION_VIEW);
                    openURL.setData(Uri.parse(url));
                    startActivity(openURL);

                }
            });

            Toast.makeText(MainActivity.this, "Click For A Detailed Info", Toast.LENGTH_SHORT).show();

        }




        // Converts InputStream data received from server to a String of readable characters

        protected String convertRawDataToString(InputStream rawData){

            InputStreamReader isr = new InputStreamReader(rawData);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line = "";

            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(line != null) {
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

        protected LinkedList<QuakeDetails> getLinkedListOfEarthquakes(String jsonResponse){

            LinkedList<QuakeDetails> earthquakeList = new LinkedList<>();


            JSONObject rootObj;


            {
                try {


                    rootObj = new JSONObject(jsonResponse);

                    JSONArray earthquakeEntries = rootObj.getJSONArray("features");

                    for (int i =0 ; i < earthquakeEntries.length(); i++){

                        JSONObject currentEntry = earthquakeEntries.getJSONObject(i);

                        JSONObject properties = currentEntry.getJSONObject("properties");

                        earthquakeList.add(new QuakeDetails(properties.getDouble("mag"),properties.getString("place"),properties.getLong("time"),properties.getLong("time"),properties.getString("url")));
                    }

                } catch (JSONException e) {
                   Log.e("LOG_TAG","Error parsing data from JSON",e);
                }
            }


            return earthquakeList;
        }


    }
}