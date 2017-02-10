package com.example.shubham_v.trigentmap.GeoCoading;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.shubham_v.trigentmap.GeoCding.ModelLatLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shubham_v on 31-01-2017.
 */

public class GeoCoadingSearch {

     static HashMap<String,ModelLatLong> actualList = new HashMap<>();
     static ArrayList<String> mStrings = new ArrayList<>();
     JSONObject obj;
     JSONArray features;
     InputStream  GeojsonfileName;

    public GeoCoadingSearch(InputStream geojsonfileName) {
        GeojsonfileName = geojsonfileName;
        loadJsonData();
    }

    public void loadJsonData()
    {
        String jsonString = loadJSONFromAsset();

        try {
            obj = new JSONObject(jsonString);
            features = obj.getJSONArray("features");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i=0;i<features.length();i++){
            try {
                JSONObject index =(JSONObject) features.get(i);
                JSONObject properties = index.getJSONObject("properties");
                String name = properties.getString("name");

                JSONArray coordinates = index.getJSONObject("geometry").getJSONArray("coordinates");

                String lat = coordinates.get(0).toString();
                String lon = coordinates.get(1).toString();


                if(name!="null") {
                    ModelLatLong modelLatLong = new ModelLatLong() ;
                    modelLatLong.setLat(lat);
                    modelLatLong.setLon(lon);
                    mStrings.add(name);
                    actualList.put(name, modelLatLong);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = GeojsonfileName;// getAssets().open("bengaluru.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static HashMap<String,ModelLatLong> getGeoHashMap()
    {
        return actualList;
    }

    public static ArrayList<String> getGeoNameList()
    {
        return mStrings;
    }


}
