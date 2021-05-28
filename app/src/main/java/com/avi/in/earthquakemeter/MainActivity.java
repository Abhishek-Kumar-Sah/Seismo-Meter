package com.avi.in.earthquakemeter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;

import android.app.LoaderManager;

import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<LinkedList<QuakeDetails>> {


    //Defining USGS url by which request will be made

    private final String USGS_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmagnitude=4";
    private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthquakeAdapter adapter;
    URL url;

    private TextView emptyTextView;
    private ProgressBar progressBar;
    private ListView earthquakeList;
    private boolean isConnected;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyTextView = findViewById(R.id.empty_text_view);
        progressBar = findViewById(R.id.progress_bar_view);

        //Creating new URL object with String USGS_URL

        {
            try {
                url = new URL(USGS_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }



        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
           isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();





        earthquakeList = findViewById(R.id.list);

        //Setting text to show if no earthquakes can be fetched
        earthquakeList.setEmptyView(emptyTextView);

        adapter = new EarthquakeAdapter(MainActivity.this,new LinkedList<QuakeDetails>() );


        earthquakeList.setAdapter(adapter);


        earthquakeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                earthquakeList.setHapticFeedbackEnabled(true);
                earthquakeList.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);


                QuakeDetails currentEarthquake = adapter.getItem(position);
                String url = currentEarthquake.getUrl();


                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;


                Intent openURL = new Intent(MainActivity.this, MyWebView.class);
                openURL.putExtra("passedURL", url);
                startActivity(openURL);

            }
        });



        //Calling EarthquakeAsyncTask in background thread

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);




        //pull down refresh listener
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                progressBar.setVisibility(View.VISIBLE);
                emptyTextView.setText("");

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                pullToRefresh.setRefreshing(true);
                pullToRefresh.performHapticFeedback(HapticFeedbackConstants.GESTURE_START);


                loaderManager.destroyLoader(EARTHQUAKE_LOADER_ID);
                loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);



                pullToRefresh.setRefreshing(false);
            }
        });




    }


    @Override
    public Loader<LinkedList<QuakeDetails>> onCreateLoader(int id, Bundle args) {

        return new EarthquakeLoader(this,url);
    }



    @Override
    public void onLoadFinished(Loader<LinkedList<QuakeDetails>> loader, LinkedList<QuakeDetails> earthquakes) {

        progressBar.setVisibility(View.GONE);
        if(isConnected) {
            emptyTextView.setText(R.string.No_Earthquakes);
        }else{
            emptyTextView.setText(R.string.No_Connection);
        }

        if (adapter != null)
            adapter.clear();

        if (earthquakes != null && !earthquakes.isEmpty()) {

            adapter.addAll(earthquakes);





        }
    }


    @Override
    public void onLoaderReset(Loader<LinkedList<QuakeDetails>> loader) {

        adapter.clear();

    }
}














//    private class EartuquakeAsyncTask extends AsyncTask<URL,Integer, LinkedList<QuakeDetails>>{
//
//        @Override
//        protected LinkedList<QuakeDetails> doInBackground(URL... urls) {
//
//            String jsonResponse = null;
//            InputStream receivedData = null;
//            HttpURLConnection urlConnection = null;
//
//            if (urls.length < 1 || urls[0] == null)
//                return null;
//
//            try {
//                urlConnection = (HttpURLConnection) urls[0].openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                if (urlConnection.getResponseCode() != 200) {
//                    Log.e("Error response code : ", String.valueOf(urlConnection.getResponseCode()));
//                    return null;
//                }
//
//                receivedData = urlConnection.getInputStream();
//                jsonResponse = convertRawDataToString(receivedData);
//
//
//            } catch (IOException e) {
//                Log.e( "Problem retrieving the earthquake JSON results.", String.valueOf(e));
//            } finally {
//
//                if (urlConnection != null)
//                    urlConnection.disconnect();
//
//                if (receivedData != null) {
//                    try {
//                        receivedData.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            return getLinkedListOfEarthquakes(jsonResponse);
//        }
//
//
//
//        //Executes after doInBackground ends and returns a LinkedList.
//        //Takes the List to display on screen as ListView
//
//        @Override
//        protected void onPostExecute(LinkedList<QuakeDetails> earthquakes) {
//
//            if(adapter != null)
//            adapter.clear();
//
//            ListView earthquakeList = findViewById(R.id.list);
//
//
//            adapter  = new EarthquakeAdapter(MainActivity.this,earthquakes);
//
//
//            earthquakeList.setAdapter(adapter);
//
//
//
//            earthquakeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    earthquakeList.setHapticFeedbackEnabled(true);
//                    earthquakeList.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//
//
//
//                    QuakeDetails currentEarthquake = earthquakes.get(position);
//                    String url = currentEarthquake.getUrl();
//
//
//                    if (!url.startsWith("http://") && !url.startsWith("https://"))
//                        url = "http://" + url;
//
//
//                    Intent openURL = new Intent(MainActivity.this, MyWebView.class);
//                    openURL.putExtra("passedURL",url);
//                    startActivity(openURL);
//
//                }
//            });
//
//            Toast.makeText(MainActivity.this, "Click For A Detailed Info", Toast.LENGTH_SHORT).show();
//
//        }
//
//
//
//
//        // Converts InputStream data received from server to a String of readable characters
//
//        protected String convertRawDataToString(InputStream rawData){
//
//            InputStreamReader isr = new InputStreamReader(rawData);
//            BufferedReader br = new BufferedReader(isr);
//
//            StringBuilder sb = new StringBuilder();
//            String line = "";
//
//            try {
//                line = br.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            while(line != null) {
//                sb.append(line);
//                try {
//                    line = br.readLine();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return sb.toString();
//        }
//
//        //Takes String data from upper convertRawDataToString method , do JSON Parsing and returns a
//        //LinkedList of earthquake data
//
//        protected LinkedList<QuakeDetails> getLinkedListOfEarthquakes(String jsonResponse){
//
//            LinkedList<QuakeDetails> earthquakeList = new LinkedList<>();
//
//
//            JSONObject rootObj;
//
//
//            {
//                try {
//
//
//                    rootObj = new JSONObject(jsonResponse);
//
//                    JSONArray earthquakeEntries = rootObj.getJSONArray("features");
//
//                    for (int i =0 ; i < earthquakeEntries.length(); i++){
//
//                        JSONObject currentEntry = earthquakeEntries.getJSONObject(i);
//
//                        JSONObject properties = currentEntry.getJSONObject("properties");
//
//                        earthquakeList.add(new QuakeDetails(properties.getDouble("mag"),properties.getString("place"),properties.getLong("time"),properties.getLong("time"),properties.getString("url")));
//                    }
//
//                } catch (JSONException e) {
//                   Log.e("LOG_TAG","Error parsing data from JSON",e);
//                }
//            }
//
//
//            return earthquakeList;
//        }
//
//
//    }
//}