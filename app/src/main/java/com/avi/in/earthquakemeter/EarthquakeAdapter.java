package com.avi.in.earthquakemeter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<QuakeDetails> {

    public EarthquakeAdapter(Context context, LinkedList<QuakeDetails> resource) {
        super(context,0,  resource);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_view,parent,false);
        }

       QuakeDetails currentReading =  getItem(position);


        TextView dateview = convertView.findViewById(R.id.date_text_view);
        dateview.setText(formattedDate(currentReading.getDate()));

        TextView timeView = convertView.findViewById(R.id.time_text_view);
        timeView.setText(formattedTime(currentReading.getTime()));

        // Splitting location string in 2 parts

        String primaryLocation = currentReading.getLocation();
        String offSet = "Near The";

        if(primaryLocation.contains(" of ")){

            String[] parts = new String[2];
            parts = primaryLocation.split(" of ");

            offSet = parts[0] + " of ";
            primaryLocation = parts[1];
        }

        TextView offSetLocationView = convertView.findViewById(R.id.offSet_text_view);
        offSetLocationView.setText(offSet);

        TextView primaryLocationView = convertView.findViewById(R.id.primary_location_text_view);
        primaryLocationView.setText(primaryLocation);

        //Trimming earthquake magnitude upto 1 decimal place

        Double mag = currentReading.getMagnitude();
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        String formattedMagnitude = magnitudeFormat.format(mag);

        TextView magnitudeView = convertView.findViewById(R.id.magnitude_text_view);
        magnitudeView.setText(formattedMagnitude);



        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentReading.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);



        return convertView;
    }

//Formatting Date as MM DD, YYYY

    private String formattedDate(long time){

        Date currentDate = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, YYYY");
        String date = dateFormat.format(currentDate);
        return date;
    }

    // Formatting time as hh : mm am/pm

    private String formattedTime (long unixTime){

        Date time = new Date(unixTime);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh : mm a");

        String newTime = timeFormat.format(time);
        return newTime;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }


}
