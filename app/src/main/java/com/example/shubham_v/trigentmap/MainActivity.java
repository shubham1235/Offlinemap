package com.example.shubham_v.trigentmap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.shubham_v.trigentmap.GeoCding.ModelLatLong;
import com.example.shubham_v.trigentmap.GeoCoading.GeoCoadingSearch;

import com.example.shubham_v.trigentmap.Gps.GoogleGpsTracker;
import com.example.shubham_v.trigentmap.GraphandRouting.MapRouting;
import com.example.shubham_v.trigentmap.MapDijkstraGraph.MapPointsGraph;
import com.example.shubham_v.trigentmap.MapRendering.MapDataRender;
import com.example.shubham_v.trigentmap.Marker.MyMarker;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.view.FrameBuffer;
import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener {

    //for geocoding
    MapView mapView;
    ModelLatLong searchedLatLong;

    MyMarker myMarker;

    //File names
    public String _mapfilename = "/bangalore";
    public File _mapsFolder;
    public InputStream _GeojosnDataFileName;

    //Layouts
    FrameLayout _MaplayerFrameLayout;

    //item on ui
    Button _Navigation_Activity;
    Button _Current_location_button_on_mainActivity;

    //geoJsonDATA search
    private static final String TAG = "SearchViewFilterMode";
    android.widget.SearchView mSearchView;
    ListView mListView;
    HashMap<String,ModelLatLong> actualList = new HashMap<>();
    ArrayList<String> mStrings = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidGraphicFactory.createInstance(this.getApplication());
        _mapsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/graphhopper/maps/");

        try {
            _GeojosnDataFileName =  getAssets().open("bengaluru.json"); //new FileInputStream(_mapsFolder+_mapfilename+".json");

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //declare ids
        _Navigation_Activity = (Button) findViewById(R.id.Navigation_Activity_id);
        _MaplayerFrameLayout = (FrameLayout) findViewById(R.id.main_map_linear_layout);
        _Current_location_button_on_mainActivity = (Button) findViewById(R.id.current_location_button_id);
        mSearchView = (android.widget.SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.list_view);

        //create class object or methods
        mapView = new MapView(this);
        searchedLatLong = new ModelLatLong();
        myMarker = new MyMarker(mapView);

        MapDataRender mapDataRender = new MapDataRender(_mapsFolder,_mapfilename,mapView,_MaplayerFrameLayout);
        mapDataRender.Loadmap();

        MapPointsGraph mapPointsGraph = new MapPointsGraph(_mapsFolder,this);
        mapPointsGraph.loadGraphStorage();


        if (_GeojosnDataFileName!= null) {
            GeoCoadingSearch geoCoadingSearch = new GeoCoadingSearch(_GeojosnDataFileName);
            actualList = GeoCoadingSearch.getGeoHashMap();
            mStrings = GeoCoadingSearch.getGeoNameList();
        }
        else {
            Toast.makeText(this, "check your map files in this location //graphhopper/maps// somting wrog with your maps data", Toast.LENGTH_SHORT).show();
        }
        //intent to Navigation search Activity
        _Navigation_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NavigationSearch.class);
                startActivity(i);
                finish();

            }
        });

        // find  currentLocatuon button </code>

        _Current_location_button_on_mainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GeoPoint _currentLocationGeoPoints = GoogleGpsTracker.getCurrentlocation();

                if (_currentLocationGeoPoints != null) {

                    Drawable drawable = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = getResources().getDrawable(R.drawable.current_pos, null);
                        myMarker.RemoveMarker();
                        myMarker.Setmarker(_currentLocationGeoPoints.getLatitude(), _currentLocationGeoPoints.getLongitude(), drawable);
                        mapView.map().setMapPosition(_currentLocationGeoPoints.getLatitude(), _currentLocationGeoPoints.getLongitude(), 1 << 15);
                        mapView.map().animator().animateTo(_currentLocationGeoPoints);
                        mapView.map().updateMap(true);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "some this wrong with Gps please try after some time or check your mobile Gps setting", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSearchView = (android.widget.SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.list_view);
        mSearchView = (android.widget.SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
        mListView.setTextFilterEnabled(true);
        setupSearchView();
        // mListView.setVisibility(View.INVISIBLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Drawable drawable = null;

                String searchString = mListView.getItemAtPosition(position).toString();
                ModelLatLong searchedLatLong = new ModelLatLong();
                searchedLatLong = actualList.get(searchString);
                double mLat = Double.parseDouble(searchedLatLong.getLon());
                double mLong = Double.parseDouble(searchedLatLong.getLat());
                Toast.makeText(MainActivity.this, "Latitude:"+mLat+",Longitude:"+mLong, Toast.LENGTH_LONG).show();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    drawable = getResources().getDrawable(R.drawable.pinkballmarker, null);
                    myMarker.RemoveMarker();
                    myMarker.Setmarker(mLat, mLong, drawable);
                    mapView.map().setMapPosition(mLat, mLong, 9 << 10);
                    mapView.map().animator().animateTo(new GeoPoint(mLat, mLong));
                    mapView.map().updateMap(true);
                }
                mSearchView.setQuery("",false);
                mListView.setVisibility(View.INVISIBLE);
                mSearchView.onActionViewCollapsed();

                ViewGroup.LayoutParams params = mListView.getLayoutParams();
                params.height = 1;
                mListView.setLayoutParams(params);
                mListView.requestLayout();

            }
        });


        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setVisibility(VISIBLE);
                ViewGroup.LayoutParams params = mListView.getLayoutParams();
                params.height = 250;
                mListView.setLayoutParams(params);
                mListView.requestLayout();
            }
        });

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
        }
        return true;
    }


    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint("Search Location Here...");
        mSearchView.setClickable(true);

        //mSearchView.setIconifiedByDefault(true);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(true);
        mSearchView.requestFocusFromTouch();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
