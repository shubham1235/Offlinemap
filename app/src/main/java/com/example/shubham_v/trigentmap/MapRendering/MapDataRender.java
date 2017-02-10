package com.example.shubham_v.trigentmap.MapRendering;

import android.os.Environment;
import android.widget.FrameLayout;

import org.oscim.android.MapView;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;

/**
 * Created by shubham_v on 31-01-2017.
 */

public class MapDataRender {

   public static File mapsFolder;
   public static String Mapfilename;
   public  MapView mapView;
   public  FrameLayout _MaplayerFrameLayout;

    public MapDataRender(File mapsFolder, String mapfilename, MapView mapView, FrameLayout _MaplayerFrameLayout) {
        this.mapsFolder = mapsFolder;
        Mapfilename = mapfilename;
        this.mapView = mapView;
        this._MaplayerFrameLayout = _MaplayerFrameLayout;
    }

    public MapDataRender(MapView mapView, FrameLayout _MaplayerFrameLayout) {
        this.mapView = mapView;
        this._MaplayerFrameLayout = _MaplayerFrameLayout;
    }

    public void Loadmap() {
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFile(new File(mapsFolder + Mapfilename+".map").getAbsolutePath());
        VectorTileLayer l = mapView.map().setBaseMap(tileSource);
        mapView.map().setTheme(VtmThemes.NEWTRON);
        mapView.map().layers().add(new BuildingLayer(mapView.map(), l));
        mapView.map().layers().add(new LabelLayer(mapView.map(), l));
        mapView.map().setMapPosition(12.985966, 77.596563, 1 << 10);
        _MaplayerFrameLayout.addView(mapView);
        mapView.map().updateMap(true);
    }



}
