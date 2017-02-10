package com.example.shubham_v.trigentmap.backgroundwork;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.example.shubham_v.trigentmap.GraphandRouting.MapRouting;

import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.renderer.MapRenderer;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by shubham_v on 16-01-2017.
 */

public class MapService extends Service  {

    private final IBinder MyMapBinder = new MyLocalBinder();
    static ArrayList<GeoPoint> NavigationGeoPointArray; // this is way lat long list
    static ArrayList<String> NavigationVoiceStatus;  // this is latlong turn status list
    static ArrayList<String> NavigationRoadname; // this is road way list
    static ArrayList<Double> DistaceBetweenpoints;

    MapView mapView;
    MapRouting  mMapRouting;
    TextToSpeech textToSpeech;

    static  int i = 0;
    static int voicecount = 0;
    static  int voicecount2= 0;
    public MapService(MapView mapView1, MapRouting mapRouting) {
         mapView = mapView1;

         mMapRouting = mapRouting;

    }
    // dont delete this
    MapService()
      {
      }

    @Override
    public void onCreate() {
        super.onCreate();

        NavigationGeoPointArray =   MapRouting.getPathGeoPoints(); // this is way lat long list
        NavigationVoiceStatus   =   MapRouting.getPathGeoPointStatus();  // this is latlong turn status list
        NavigationRoadname      =   MapRouting.getPathNames(); // this is road way list
        DistaceBetweenpoints    =   MapRouting.getDistancebetweenpoint();  // this is distace list between way points

        voicecount = 0;

        Toast.makeText(this, "this is on create", Toast.LENGTH_SHORT).show();



        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });


        Toast.makeText(this, "oncreate ", Toast.LENGTH_SHORT).show();
    }

    public boolean GetTimeToTimeVoiceNavigation(double current_lattitude,double current_logitude) {

        Toast.makeText(this, "oncreate ", Toast.LENGTH_SHORT).show();
        double CurrentLat = current_lattitude;
        double CurrentLong = current_logitude;
        boolean serviceunbindAutomatic = false;

        GeoPoint geoPoint = new GeoPoint(CurrentLat, CurrentLong);

//        double DistanceBetweenWayNodes = distance(CurrentLat, CurrentLong, NavigationGeoPointArray.get(i).getLatitude(), NavigationGeoPointArray.get(i).getLongitude())*1000;
          double DistanceBetweenWayNodes = geoPoint.distance(new GeoPoint(NavigationGeoPointArray.get(i).getLatitude(), NavigationGeoPointArray.get(i).getLongitude()));
         String NextWayNodeStatus = NavigationVoiceStatus.get(i);
       //this is working for final point nearest destination
        if (NavigationGeoPointArray.get(i).getLatitude() == NavigationGeoPointArray.get(NavigationGeoPointArray.size() - 1).getLatitude()) {
            if ( DistanceBetweenWayNodes >= 400 && DistanceBetweenWayNodes <= 500) {
                String Roadname  = NavigationRoadname.get(i);
                String speech = "your destination will be come after half kilometer" + Roadname ;
                Toast.makeText(this, speech , Toast.LENGTH_SHORT).show();

            }
            else {
                String Speech = " your destination will be come  please follow the path";
                Toast.makeText(this, Speech , Toast.LENGTH_SHORT).show();

            }
        }

        if (voicecount == 0 || voicecount == 1) {
            if (NavigationGeoPointArray.get(i).getLatitude() == NavigationGeoPointArray.get(0).getLatitude()) {

                String Roadname = NavigationRoadname.get(i+1);
                String speech = " Please go to " + Roadname;
                //despriciated
                Toast.makeText(this, speech, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                voicecount++;
            }
        }

        // this is if and switch working every time
        if(voicecount == 2 || voicecount == 3) {
            if (DistanceBetweenWayNodes <= (DistaceBetweenpoints.get(i)*60)/100 && DistanceBetweenWayNodes >= (DistaceBetweenpoints.get(i)*50)/100) {
                String speech = "";
                switch (NextWayNodeStatus) {
                    case "CONTINUE_ON_STREET":
                        speech = "CONTINUE ON STREET to  " + NavigationRoadname.get(i);
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                        voicecount++;

                        break;

                    case "TURN_SLIGHT_RIGHT":
                        speech = "TURN SLIGHT RIGHT to" + NavigationRoadname.get(i);
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                        voicecount++;
                        break;

                    case "TURN_RIGHT":
                        speech = "TURN  RIGHT to" + NavigationRoadname.get(i);
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                        voicecount++;
                        break;

                    case "TURN_SHARP_RIGHT":
                        speech = "TURN  SHARP  RIGHT to" + NavigationRoadname.get(i);
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                        voicecount++;
                        break;

                    case "TURN_SLIGHT_LEFT":
                        speech = "TURN  SLIGHT  LEFT  to" + NavigationRoadname.get(i);
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);


                        voicecount++;
                        break;

                    case "TURN_LEFT":
                        speech = "TURN  LEFT to" + NavigationRoadname.get(i);
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);


                        voicecount++;
                        break;

                    case "TURN_SHARP_LEFT ":
                        speech = "TURN SHARP LEFT  to " + NavigationRoadname.get(i);
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                        voicecount++;
                        break;

                    case "FINISH":
                        speech = "Welcome to your destination";
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                        break;

                    case "VIA_REACHED":
                        Toast.makeText(this, "we will go via this  Road", Toast.LENGTH_SHORT).show();
                        voicecount++;
                        break;
                }
            }
        }


        // this is condotion for when Distance is les then 50 and grater the 10
         if (DistanceBetweenWayNodes <= (DistaceBetweenpoints.get(i)*15)/100 && DistanceBetweenWayNodes >= (DistaceBetweenpoints.get(i)*4)/100) {
             String speech = "";
             switch (NextWayNodeStatus) {
                 case "CONTINUE_ON_STREET":
                     speech = "CONTINUE ON STREET to  " + NavigationRoadname.get(i);


                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                     i++;
                     voicecount = 1;

                     break;

                 case "TURN_SLIGHT_RIGHT":
                     speech = "TURN SLIGHT RIGHT to" + NavigationRoadname.get(i);
                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                     i++;
                     voicecount = 1;

                     break;

                 case "TURN_RIGHT":
                     speech = "TURN  RIGHT to" + NavigationRoadname.get(i);
                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                     i++;
                     voicecount = 1;

                     break;

                 case "TURN_SHARP_RIGHT":
                     speech = "TURN  SHARP  RIGHT to" + NavigationRoadname.get(i);
                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                     i++;
                     voicecount = 1;

                     break;

                 case "TURN_SLIGHT_LEFT":
                     speech = "TURN  SLIGHT  LEFT  to" + NavigationRoadname.get(i);
                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);


                     i++;
                     voicecount = 1;

                     break;

                 case "TURN_LEFT":
                     speech = "TURN  LEFT to" + NavigationRoadname.get(i);
                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);


                     i++;
                     voicecount = 1;

                     break;

                 case "TURN_SHARP_LEFT ":
                     speech = "TURN SHARP LEFT  to " + NavigationRoadname.get(i);
                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                     i++;
                     voicecount = 1;

                     break;

                 case "FINISH":
                     speech = "Welcome to your destination";
                     textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);


                     serviceunbindAutomatic = true;

                     break;

                 case "VIA_REACHED":
                     Toast.makeText(this, "we will go via this  Road", Toast.LENGTH_SHORT).show();

                     i++;
                     voicecount = 1;
                     break;
             }
         }
         return serviceunbindAutomatic;
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return MyMapBinder;
    }

    public  class  MyLocalBinder extends Binder{

        public   MapService getService()
        {
            return MapService.this;
        }
    }
}
