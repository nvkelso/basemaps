package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import java.util.List;

public class Earth implements ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "earth";
  }

  // TODO (nvkelso 2023-03-21)
  // 1. spreadsheets/sort_rank/earth.csv
  // 2. perform vectordatasource.transform.handle_label_placement
  // 3. perform vectordatasource.transform.drop_features_where to drop polygons of kinds that are only label placements (like island)
  // 4. perform vectordatasource.transform.merge_polygon_features
  // 5. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 6. vectordatasource.transform.add_collision_rank

  public void processNe(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    var kind = "";
    var theme_min_zoom = 0;
    var theme_max_zoom = 0;

    // TODO (nvkelso 2023-03-25)
    //      These should all be per feature instead of layer... but per feature per layer
    if (sourceLayer.equals("ne_110m_land")) {
      // use default zooms
      kind = "earth";
    } else if (sourceLayer.equals("ne_50m_land")) {
      theme_min_zoom = 1;
      theme_max_zoom = 3;
      kind = "earth";
    } else if (sourceLayer.equals("ne_10m_land")) {
      theme_min_zoom = 4;
      theme_max_zoom = 6;
      kind = "earth";
    }

    if( kind != "" ) {
      features.polygon(this.name())
          .setAttr("kind", "earth")
          // TODO (nvkelso 2023-03-25)
          //      This should be a single decimal precision float not string
          .setAttr("min_zoom", sf.getLong("min_zoom"))
          //      This should be a single decimal precision float not string
          //      See below section, too
          .setZoomRange(sf.getString("min_zoom") == null ? theme_min_zoom : (int)Double.parseDouble(sf.getString("min_zoom")), theme_max_zoom)
          .setAttr("source", "naturalearthdata.com")
          .setBufferPixels(8);
    }
  }

  public void processOsm(SourceFeature sf, FeatureCollector features) {
    features.polygon(this.name())
      .setAttr("kind", "earth")
        // TODO (nvkelso 2023-03-25)
        // Should we also export way_area as area?
        //      Should this be variable zoom?
        .setAttr("min_zoom", 8.0)
        .setAttr("source", "osmdata.openstreetmap.de")
        .setZoomRange(7, 15).setBufferPixels(8);
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // TODO
    // This should be 8 px buffer for lines and polygons, 256 for points

    return FeatureMerge.mergeOverlappingPolygons(items, 1);
  }
}
