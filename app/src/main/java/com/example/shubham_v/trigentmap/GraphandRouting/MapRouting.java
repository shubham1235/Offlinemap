package com.example.shubham_v.trigentmap.GraphandRouting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.shubham_v.trigentmap.Marker.MyMarker;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by shubham_v on 05-01-2017.
 */

public class MapRouting  {

    Context mContext;
    MapView mapView;
    public GraphHopper hopper;
    public GHResponse resp;
    org.oscim.layers.vector.PathLayer pathLayer;
    public List<GeoPoint> setCurrentLocationBywayGeoPoints = new ArrayList<>();
    public  static ArrayList<GeoPoint> Direction_Marker_List = new ArrayList<>();
    public  static ArrayList<String> Direction_status_ArrayList = new ArrayList<>();;
    public  static ArrayList<String> Roadnames = new ArrayList<>();;
    public  static ArrayList<Double> distance_Array_list = new ArrayList<>();
    MyMarker myMarker;
    List<GeoPoint> geoPoints = new ArrayList<>();

    public MapRouting(Context context, MapView MainMapView, GraphHopper hopper1 )

    {
        mContext = context;
        mapView = MainMapView;
        hopper = hopper1;
    }


    public  boolean calcPath(final double fromLat, final double fromLon, final double toLat, final double toLon) {
        geoPoints.add(new GeoPoint(fromLat,fromLon));
        log("calculating path ...");
        new AsyncTask<Void, Void, PathWrapper>() {
            float time;

            protected PathWrapper doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().put(Parameters.Routing.INSTRUCTIONS, "true");

                resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp.getBest();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            protected void onPostExecute(PathWrapper resp) {
                if (!resp.hasErrors()) {
                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                            + toLon + " found path with distance:" + resp.getDistance()
                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                            + time + " " + resp.getDebugInfo());
                    logUser("the route is " + (int) (resp.getDistance() / 100) / 10f
                            + "km long, time:" + resp.getTime() / 60000f + "min, debug:" + time);



                    pathLayer = createPathLayer(resp);
                    mapView.map().layers().add(pathLayer);
                    mapView.map().updateMap(true);

                    int n =   resp.getInstructions().getSize();

                    for (int i = 0; n > i; i++) {
                        Instruction instruction = resp.getInstructions().get(i);
                        Direction_Marker_List.add(new GeoPoint(instruction.getPoints().getLatitude(0),instruction.getPoints().getLongitude(0)));
                        int direction = instruction.getSign();
                        String Roadname = instruction.getName();
                        if(Roadname == "")
                        {
                            Roadnames.add("Road");
                        }
                        else {
                            Roadnames.add(instruction.getName());
                        }

                        String instructionStr = "";
                        switch (direction) {
                            case 0:
                                instructionStr = "CONTINUE_ON_STREET";
                                break;
                            case 1:
                                instructionStr = "TURN_SLIGHT_RIGHT";
                                break;
                            case 2:
                                instructionStr = "TURN_RIGHT";
                                break;
                            case 3:
                                instructionStr = "TURN_SHARP_RIGHT";
                                break;
                            case -1:
                                instructionStr = "TURN_SLIGHT_LEFT";
                                break;
                            case -2:
                                instructionStr = "TURN_LEFT";
                                break;
                            case -3:
                                instructionStr = "TURN_SHARP_LEFT ";
                                break;
                            case 4:
                                instructionStr = "FINISH";
                                break;
                            case 5:
                                instructionStr = "VIA_REACHED";
                                break;
                        }
                        log(instructionStr);
                        Direction_status_ArrayList.add(instructionStr);


                    }

                    DistanceArraylistbetweenturnpoint();

                } else {

                    logUser("Error:" + resp.getErrors());
                }

            }
        }.execute();
        return true;
    }

    private org.oscim.layers.vector.PathLayer createPathLayer(PathWrapper response) {

        Style style = Style.builder().generalization(Style.GENERALIZATION_SMALL).strokeColor(0x9900cc33).strokeWidth(4 * mContext.getResources().getDisplayMetrics().density).build();
        org.oscim.layers.vector.PathLayer pathLayer = new org.oscim.layers.vector.PathLayer(mapView.map(), style);
        PointList pointList = response.getPoints();


        for (int i = 0; i < pointList.getSize(); i++) {
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
            setCurrentLocationBywayGeoPoints.add(new GeoPoint(pointList.getLatitude(i),pointList.getLongitude(i)));
        }
        pathLayer.setPoints(geoPoints);

        return pathLayer;
    }

    public  void RemovePathLayer()
    {
        mapView.map().layers().remove(pathLayer);
        geoPoints.clear();}

    private void DistanceArraylistbetweenturnpoint()
    {
        int k;
        for( k = 0; k < Direction_Marker_List.size()-1;k++)
        {
            GeoPoint geoPoint = new GeoPoint(Direction_Marker_List.get(k).getLatitude(),Direction_Marker_List.get(k).getLongitude());
            double distance =  geoPoint.distance(new GeoPoint(Direction_Marker_List.get(k+1).getLatitude(),Direction_Marker_List.get(k+1).getLongitude()));
            distance_Array_list.add(distance);

        }

    }


     public static ArrayList<GeoPoint> getPathGeoPoints()
    {
        return Direction_Marker_List;
    }

    public static ArrayList<Double> getDistancebetweenpoint()
    {
        return distance_Array_list;
    }

     public static ArrayList<String> getPathGeoPointStatus()
    {
        return  Direction_status_ArrayList;
    }

     public static ArrayList<String> getPathNames()
    {
        return Roadnames;
    }

     void log(String str) {
        Log.i("GH", str);
    }

     void logUser(String str) {
        log(str);
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }
}
