package com.example.shubham_v.trigentmap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shubham_v.trigentmap.GeoCding.ModelLatLong;
import com.example.shubham_v.trigentmap.GeoCoading.GeoCoadingSearch;

import com.example.shubham_v.trigentmap.Gps.GoogleGpsTracker;
import com.example.shubham_v.trigentmap.GraphandRouting.MapRouting;
import com.example.shubham_v.trigentmap.MapDijkstraGraph.MapPointsGraph;
import com.example.shubham_v.trigentmap.MapRendering.MapDataRender;
import com.example.shubham_v.trigentmap.Marker.MyMarker;
import com.example.shubham_v.trigentmap.backgroundwork.MapService;
import com.graphhopper.GraphHopper;

import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.VISIBLE;

public class NavigationSearch extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener {

            HashMap<String,ModelLatLong> actualList = new HashMap<>();
            ArrayList<String> SourceStrings = new ArrayList<>();
            ArrayList<String> mStrings = new ArrayList<>();
            android.widget.SearchView _sourceSearchView,_destinationSearchView;
            ListView _sourceListView,_destinationListview;
             static MapView mapView;
            ArrayAdapter<String> mAdapter;
            static MyMarker  myMarker;
            FrameLayout navigationFrameLayour;
            GraphHopper graphHopper;
            Button _FinaNavigationButton;
            double Source_mLat=0.0;
            double Source_mLong=0.0;
            double DestinationmLong=0.0;
            double DestinationmLat=0.0;
            MapRouting mapRouting;
            GeoPoint currentLocation = null;
            float FrameLayour_screen_X_cordinates = 0;
            float FrameLayout_screen_Y_cordinates = 0;



             static boolean navigationworkig = false;
             static boolean CalcPathFinishAuthenticaiton = false;
             static Drawable drawable = null;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_search);


            _sourceSearchView = (android.widget.SearchView) findViewById(R.id.source_view_search_id);
            _sourceListView = (ListView) findViewById(R.id.source_list_view);

            _destinationSearchView = (android.widget.SearchView) findViewById(R.id.destination_view_search_id);
            _destinationListview = (ListView) findViewById(R.id.destination_list_view);
            _FinaNavigationButton = (Button) findViewById(R.id.Navigation_Button_id);

            navigationFrameLayour = (FrameLayout)findViewById(R.id.Navigation_Map_FrameLayout_id);

            mapView = new MapView(this);
            myMarker = new MyMarker(mapView);

            graphHopper = MapPointsGraph.getGraphopper();
            mapRouting = new MapRouting(NavigationSearch.this,mapView,graphHopper);
            mapService = new MapService(mapView,mapRouting);
//            googleGpsTracker = new GoogleGpsTracker(NavigationSearch.this);



            MapDataRender mapDataRender = new MapDataRender(mapView,navigationFrameLayour);
            mapDataRender.Loadmap();

            actualList = GeoCoadingSearch.getGeoHashMap();
            mStrings =GeoCoadingSearch.getGeoNameList();
            SourceStrings.add("USE YOUR CURRENT LOCATION");
            SourceStrings =GeoCoadingSearch.getGeoNameList();

            _sourceListView.setAdapter(mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
            _sourceListView.setTextFilterEnabled(true);

            _destinationListview.setAdapter(mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
            _destinationListview.setTextFilterEnabled(true);

            sourcesetupSearchView();
            destinationsetupSearchView();
            // mListView.setVisibility(View.INVISIBLE);

        /*     FrameLayour_screen_X_cordinates = navigationFrameLayour.getX() + navigationFrameLayour.getWidth()/2;
             FrameLayout_screen_Y_cordinates  = navigationFrameLayour.getY()+navigationFrameLayour.getHeight()/2;

*/

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)

            {
                drawable = getResources().getDrawable(R.drawable.car, null);
            }


            _sourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Drawable drawable = null;

                    String searchString =  _sourceListView.getItemAtPosition(position).toString();
                    ModelLatLong searchedLatLong = new ModelLatLong();
                    searchedLatLong = actualList.get(searchString);
                    Source_mLat = Double.parseDouble(searchedLatLong.getLon());
                    Source_mLong = Double.parseDouble(searchedLatLong.getLat());
                    Toast.makeText(NavigationSearch.this, "Latitude:"+Source_mLat+",Longitude:"+Source_mLong, Toast.LENGTH_LONG).show();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = getResources().getDrawable(R.drawable.largepinkball, null);
// test for current location                        myMarker.NavigationSourceSetmarker(Source_mLat, Source_mLong, drawable);
                        myMarker.NavigationSourceSetmarker(GoogleGpsTracker.getCurrentlocation().getLatitude(),GoogleGpsTracker.getCurrentlocation().getLongitude(), drawable);

                        mapView.map().setMapPosition(Source_mLat,Source_mLong, 9 << 10);
                        mapView.map().animator().animateTo(new GeoPoint(Source_mLat,Source_mLong));
                        mapView.map().updateMap(true);
                    }
                    _sourceSearchView.setQuery("",false);
                    _sourceListView.setVisibility(View.INVISIBLE);
                    _sourceSearchView.onActionViewCollapsed();
                    ViewGroup.LayoutParams params = _sourceListView.getLayoutParams();
                    params.height = 1;
                    _sourceListView.setLayoutParams(params);
                    _sourceListView.requestLayout();


                }
            });


            _sourceSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _sourceListView.setVisibility(VISIBLE);
                    mapRouting.RemovePathLayer();
                    ViewGroup.LayoutParams params =  _sourceListView.getLayoutParams();
                    params.height = 250;
                    _sourceListView.setLayoutParams(params);
                    _sourceListView.requestLayout();
                }
            });
            _destinationListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Drawable drawable = null;

                    String searchString = _destinationListview.getItemAtPosition(position).toString();
                    ModelLatLong searchedLatLong = new ModelLatLong();
                    searchedLatLong = actualList.get(searchString);
                     DestinationmLat = Double.parseDouble(searchedLatLong.getLon());
                     DestinationmLong = Double.parseDouble(searchedLatLong.getLat());
                    Toast.makeText(NavigationSearch.this, "Latitude:"+DestinationmLat+",Longitude:"+DestinationmLong, Toast.LENGTH_LONG).show();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = getResources().getDrawable(R.drawable.largefgreenball, null);
                        myMarker.NavigationDestinationSetmarker(DestinationmLat,DestinationmLong, drawable);
                        mapView.map().setMapPosition(DestinationmLat,DestinationmLong, 9 << 10);
                        mapView.map().animator().animateTo(new GeoPoint(DestinationmLat,DestinationmLong));
                        mapView.map().updateMap(true);
                    }
                    _destinationSearchView.setQuery("",false);
                    _destinationListview.setVisibility(View.INVISIBLE);
                    _destinationSearchView.onActionViewCollapsed();

                    ViewGroup.LayoutParams params = _destinationListview.getLayoutParams();
                    params.height = 1;
                    _destinationListview.setLayoutParams(params);
                    _destinationListview.requestLayout();

                }
            });

            _destinationSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _destinationListview.setVisibility(VISIBLE);
                     mapRouting.RemovePathLayer();
                     ViewGroup.LayoutParams params = _destinationListview.getLayoutParams();
                     params.height = 250;
                    _destinationListview.setLayoutParams(params);
                    _destinationListview.requestLayout();

                }
            });



        _FinaNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Source_mLat!=0.0 && Source_mLong!=00.0 && DestinationmLong!=0.0 && DestinationmLat!=0.0 ) {
                    CallUnbindServie(); // this for navigation bundding
                    mapRouting.RemovePathLayer();
                    CallServicebind();
                    CalcPathFinishAuthenticaiton =  mapRouting.calcPath(GoogleGpsTracker.getCurrentlocation().getLatitude(),GoogleGpsTracker.getCurrentlocation().getLongitude(),DestinationmLat,DestinationmLong);
                    mapView.map().animator().animateTo(new GeoPoint(Source_mLat,Source_mLong));


                }
                else {
                    Toast.makeText(NavigationSearch.this, "we are not able to fine path its too long or some thing worng with your source or destination", Toast.LENGTH_SHORT).show();
                }
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
            _sourceListView.clearTextFilter();
            _destinationListview.clearTextFilter();

        } else {
            _sourceListView.setFilterText(newText.toString());
            _destinationListview.setFilterText(newText.toString());

        }
        return true;
    }


    private void sourcesetupSearchView() {
        _sourceSearchView.setIconifiedByDefault(true);
        _sourceSearchView.setOnQueryTextListener(this);
        _sourceSearchView.setSubmitButtonEnabled(false);
        _sourceSearchView.setQueryHint("Search Location Here...");
        _sourceSearchView.setClickable(true);
        //mSearchView.setIconifiedByDefault(true);
        _sourceSearchView.setFocusable(true);
        _sourceSearchView.setIconified(true);
        _sourceSearchView.requestFocusFromTouch();

    }


    private void destinationsetupSearchView() {
        _destinationSearchView.setIconifiedByDefault(true);
        _destinationSearchView.setOnQueryTextListener(this);
        _destinationSearchView.setSubmitButtonEnabled(false);
        _destinationSearchView.setQueryHint("Search Location Here...");
        _destinationSearchView.setClickable(true);
        //mSearchView.setIconifiedByDefault(true);
        _destinationSearchView.setFocusable(true);
        _destinationSearchView.setIconified(true);
        _destinationSearchView.requestFocusFromTouch();

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(NavigationSearch.this, MainActivity.class);
        startActivity(i);
        finish();
    }



    static MapService mapService;
    Boolean mapServiceStatus = false;


    // this is for bind the service with navigation activity
    void CallServicebind()
    {
        Intent intent = new Intent(this,MapService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
        mapServiceStatus = true;
        Toast.makeText(this, "service is bind successfully", Toast.LENGTH_SHORT).show();
        navigationworkig = true;
    }

    // this is for bind the service with nagitaion activity
    void CallUnbindServie()
    {
        if(mapServiceStatus)
        {
            unbindService(serviceConnection);
            Toast.makeText(this, "service unbind Successfully", Toast.LENGTH_SHORT).show();
            mapServiceStatus = false;
        }

        else {
            Toast.makeText(this, "Service is unbind already ", Toast.LENGTH_SHORT).show();
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            MapService.MyLocalBinder binder = (MapService.MyLocalBinder)service;
             mapService = binder.getService();
             mapServiceStatus = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    public static  void ServieMainNavigationTastk(double navlat, double navlong)
    {
          if(navigationworkig == true ) {
              mapService.GetTimeToTimeVoiceNavigation(navlat, navlong);
              if (navlat!=0) {

                      myMarker.RemoveMarker();
                      myMarker.Setmarker(navlat, navlong, drawable);

                      mapView.map().animator().animateTo(new GeoPoint(navlat,navlong));
                      mapView.map().updateMap(true);
                  }
              } else {
                   }


          }
    }


