package com.example.shubham_v.trigentmap.Marker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.utils.pool.Inlist;

import java.util.ArrayList;

/**
 * Created by shubham_v on 05-01-2017.
 */

public class MyMarker {
    private MapView mapView;
    Context mcontext;
    public  ItemizedLayer<MarkerItem> itemizedLayer;
    public static   ItemizedLayer<MarkerItem> NavitemizedLayer1;
    public static   ItemizedLayer<MarkerItem> NavitemizedLayer2;
    public static   ItemizedLayer<MarkerItem> NavitemizedLayer3;
    public static   ItemizedLayer<MarkerItem> NavitemizedLayer4;
    MarkerItem markerItem;

    public MyMarker(MapView MarkerMapview)
       {
           mapView = MarkerMapview;


       }
    public void Setmarker(Double markerLattitude, Double marerLogitude, Drawable markerimage) {
        GeoPoint geoPoint = new GeoPoint(markerLattitude,marerLogitude);
        itemizedLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
        itemizedLayer.addItem(createMarkerItem(geoPoint, markerimage));
        mapView.map().layers().add(itemizedLayer);
    }

    private MarkerItem createMarkerItem(GeoPoint p, Drawable resource) {

        Drawable drawable = resource;
        Bitmap bitmap = AndroidGraphics.drawableToBitmap(drawable);
       // MarkerSymbol markerSymbol = new MarkerSymbol(bitmap, 0.5f, 1);
        MarkerSymbol markerSymbol = new MarkerSymbol(bitmap,0.5f, 1,true);
        markerItem = new MarkerItem(" hello ", "shubham", p);
        markerItem.setMarker(markerSymbol);
        return markerItem;
    }

    public void RemoveMarker()
    {
        mapView.map().layers().remove(itemizedLayer);


    }
    static int countsource = 1;
    static int countDestination = 1;

    public void NavigationSourceSetmarker(Double markerLattitude, Double marerLogitude, Drawable markerimage) {

        if (countsource == 1) {
            mapView.map().layers().remove(NavitemizedLayer2);
            NavitemizedLayer1 = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
            GeoPoint geoPoint = new GeoPoint(markerLattitude, marerLogitude);
            NavitemizedLayer1.addItem(createMarkerItem(geoPoint, markerimage));
            mapView.map().layers().add(NavitemizedLayer1);
            mapView.map().updateMap(true);
           countsource = 2;
        }
        else {
            mapView.map().layers().remove(NavitemizedLayer1);
            NavitemizedLayer2 = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
            GeoPoint geoPoint = new GeoPoint(markerLattitude, marerLogitude);
            NavitemizedLayer2.addItem(createMarkerItem(geoPoint, markerimage));
            mapView.map().layers().add(NavitemizedLayer2);
            mapView.map().updateMap(true);
            countsource = 1;
        }
    }
    public void NavigationDestinationSetmarker(Double markerLattitude, Double marerLogitude, Drawable markerimage) {
             if (countDestination==1){
                    mapView.map().layers().remove(NavitemizedLayer4);
                    NavitemizedLayer3 = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
                    GeoPoint geoPoint = new GeoPoint(markerLattitude, marerLogitude);
                    NavitemizedLayer3.addItem(createMarkerItem(geoPoint, markerimage));
                    mapView.map().layers().add(NavitemizedLayer3);
                    mapView.map().updateMap(true);
                    countDestination =2;
             }

             else
             {

                 mapView.map().layers().remove(NavitemizedLayer3);
                 NavitemizedLayer4 = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
                 GeoPoint geoPoint = new GeoPoint(markerLattitude, marerLogitude);
                 NavitemizedLayer4.addItem(createMarkerItem(geoPoint, markerimage));
                 mapView.map().layers().add(NavitemizedLayer4);
                 mapView.map().updateMap(true);
                 countDestination=1;

             }
       }

}
