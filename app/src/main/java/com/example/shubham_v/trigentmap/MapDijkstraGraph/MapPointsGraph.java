package com.example.shubham_v.trigentmap.MapDijkstraGraph;

import android.content.Context;
import android.graphics.Path;
import android.util.Log;
import android.widget.Toast;

import com.example.shubham_v.trigentmap.GHAsyncTask;
import com.example.shubham_v.trigentmap.GraphandRouting.MapRouting;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.Constants;

import java.io.File;

/**
 * Created by shubham_v on 31-01-2017.
 */

public class MapPointsGraph {
    File _mapsFolder = null;
    static GraphHopper hopper;
    Context mContext;

    public MapPointsGraph(File _mapsFolder, Context mContext) {
        this._mapsFolder = _mapsFolder;
        this.mContext = mContext;
    }

   public void loadGraphStorage() {
        logUser("loading graph (" + Constants.VERSION + ") ... ");

        new GHAsyncTask<Void, Void, Path>() {
            protected Path saveDoInBackground(Void... v) throws Exception {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.load(new File(_mapsFolder, "").getAbsolutePath());
                log("found graph " + tmpHopp.getGraphHopperStorage().toString() + ", nodes:" + tmpHopp.getGraphHopperStorage().getNodes());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute(Path o) {
                if (hasError()) {
                    logUser("An error happened while creating graph:"
                            + getErrorMessage());
                } else {
                    logUser("Finished loading graph. Press long to define where to start and end the route.");

                    //calculate path by maprouting
              //      mapRouting = new MapRouting(getApplication(), mapView, hopper);


                }
            }
        }.execute();
    }
    private void log(String str) {
        Log.i("GH", str);
    }

    private void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    private void logUser(String str) {
        log(str);
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }

    public static GraphHopper getGraphopper()
    {
        return hopper;
    }

}
