package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.Parse;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class Landuse implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {
  public static final String NAME = "landuse";

  // TODO (nvkelso 2023-03-21)
  // 1.  normalize_operator_values
  // 2.  major_airport_detector
  // 3.  Move national_park, protected_area, nature_reserve to LANDUSE layer
  // 4.  spreadsheets/sort_rank/landuse.csv
  // 5.  perform vectordatasource.transform.handle_label_placement with some exceptions
  // 6.  perform vectordatasource.transform.drop_features_where it's property is mz_drop_polygon?!
  // 7.  perform vectordatasource.transform.drop_properties to drop intermediate mz_label_placement property
  // 8.  perform vectordatasource.transform.drop_properties to drop the names and less important additional tags of small landuse polygons
  // 9.  perform vectordatasource.transform.drop_features_where to ensure mz_is_building and label_placement features are removed
  // 10. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 11. vectordatasource.transform.drop_properties osm_relation at most zooms
  // 12. vectordatasource.transform.drop_properties names at mid-zooms
  // 13. vectordatasource.transform.drop_properties various properties at mid-zooms
  // 14. vectordatasource.transform.drop_small_inners
  // 15. vectordatasource.transform.merge_polygon_features with buffer merge and tolerance mid-zooms
  // 16. vectordatasource.transform.merge_polygon_features with buffer merge and tolerance high-zooms
  // 17. vectordatasource.transform.drop_small_inners at zoom 14
  // 18. vectordatasource.transform.add_collision_rank

  // TODO (nvkelso 2023-03-21)
  // This should eventually go in Natural, but for v1.9 baseline it's here instead
  public void processNe(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    var kind = "";
    var theme_min_zoom = 0;
    var theme_max_zoom = 0;

    // TODO (nvkelso 2023-03-25)
    //      These should all be per feature instead of layer... but per feature per layer
    if (sourceLayer.equals("ne_50m_urban_areas")) {
      // use default zooms
      kind = "urban_area";
    } else if (sourceLayer.equals("ne_10m_urban_areas")) {
      theme_min_zoom = 4;
      theme_max_zoom = 6; // This is debateable
      kind = "urban_area";
    }

    if (sf.canBePolygon() && sf.hasTag("min_zoom") && kind.equals("") == false ) {
      features.polygon("landuse")
        .setAttr("kind", kind)
        // TODO (nvkelso 2023-03-25)
        //      This should be a single decimal precision float not string
        //      (nvkelso 2023-03-26)
        //      This might also suffer from NULL problems?
        .setAttr("min_zoom", sf.getLong("min_zoom"))
        //      This should be a single decimal precision float not string
        //      See below section, too
        .setZoomRange(sf.getString("min_zoom") == null ? theme_min_zoom : (int)Double.parseDouble(sf.getString("min_zoom")), theme_max_zoom)
        .setAttr("source", "naturalearthdata.com")
        .setBufferPixels(8);
    }
  }

  public static void processFeature(SourceFeature sf, FeatureCollector features, String layerName,
    boolean ghostFeatures) {
    var sourceLayer = sf.getSourceLayer();
    //var name = "";
    var kind = "";
    var kind_detail = "";
    var feature_min_zoom = 20;
    var name_min_zoom = 20;
    var theme_min_zoom = 20;
    var theme_max_zoom = 20;

    if (sf.canBePolygon()) {
      if( sf.hasTag("aeroway") && sf.getString("aeroway") != null ) {
        switch (sf.getString("aeroway")) {
          case "aerodrome":
            kind = "aerodrome";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
          case "runway":
            kind = "runway";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 12;
            theme_max_zoom = 15;
            break;
        }
      }
      if( sf.hasTag("aeroway:area") && sf.getString("aeroway") != null ) {
        switch (sf.getString("aeroway:area")) {
          case "taxiway":
            kind = "taxiway";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 12;
            theme_max_zoom = 15;
            break;
          case "runway":
            kind = "runway";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 12;
            theme_max_zoom = 15;
            break;
        }
      }
      if( sf.hasTag("amenity") && sf.getString("amenity") != null ) {
        switch (sf.getString("amenity")) {
          case "hospital":
            kind = "hospital";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "school":
            kind = "school";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 11;
            theme_max_zoom = 15;
            break;
          case "kindergarten":
            kind = "kindergarten";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 14;
            theme_max_zoom = 15;
            break;
          case "university":
            kind = "university";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
          case "college":
            kind = "college";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
        }
      }
      if( sf.hasTag("boundary") && sf.getString("boundary") != null ) {
        switch (sf.getString("boundary")) {
          case "protected_area":
            kind = "protected_area";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 6;
            theme_max_zoom = 15;
            break;
        }
      } else
      if( sf.hasTag("landuse") && sf.getString("landuse") != null ) {
        switch (sf.getString("landuse")) {
          case "recreation_ground":
            kind = "recreation_ground";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 11;
            theme_max_zoom = 15;
            break;
          case "industrial":
            kind = "industrial";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "railway":
            kind = "railway";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "cemetery":
            kind = "cemetery";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "graveyard":
            kind = "graveyard";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "winter_sports":
            kind = "winter_sports";
            feature_min_zoom = 10;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "commercial":
            kind = "commercial";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "residential":
            kind = "residential";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
        }
      } else
      if( sf.hasTag("leisure") && sf.getString("leisure") != null ) {
        switch (sf.getString("leisure")) {
          case "park":
            kind = "park";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
          case "nature_reserve":
            kind = "nature_reserve";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
          case "garden":
            kind = "garden";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 11;
            theme_max_zoom = 15;
            break;
          case "golf_course":
            kind = "golf_course";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 9;
            theme_max_zoom = 15;
            break;
          case "dog_park":
            kind = "dog_park";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 12;
            theme_max_zoom = 15;
            break;
          case "playground":
            kind = "playground";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 12;
            theme_max_zoom = 15;
            break;
          case "pitch":
            kind = "pitch";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 11;
            theme_max_zoom = 15;
            break;
        }
      } else
      if( sf.hasTag("man_made") && sf.getString("man_made") != null ) {
        switch (sf.getString("man_made")) {
          case "pier":
            kind = "pier";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 10;
            theme_max_zoom = 15;
            break;
        }
      } else
      if( sf.hasTag("railway") && sf.getString("railway") != null ) {
        switch (sf.getString("railway")) {
          case "platform":
            kind = "platform";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 14;
            theme_max_zoom = 15;
            break;
        }
      }

      if (sf.hasTag("area", "yes") ) {
        if(sf.hasTag("highway", "pedestrian", "footway") ) {
          switch (sf.getString("highway")) {
            case "pedestrian":
              kind = "pedestrian";
              // feature_min_zoom = 20;
              // name_min_zoom = 20;
              theme_min_zoom = 12;
              theme_max_zoom = 15;
              break;
            case "footway":
              kind = "footway";
              // feature_min_zoom = 20;
              // name_min_zoom = 20;
              theme_min_zoom = 12;
              theme_max_zoom = 15;
              break;
          }
        }
        if( sf.hasTag("man_made", "bridge") && sf.getString("man_made", "bridge") != null ) {
          kind = "bridge";
          // feature_min_zoom = 20;
          // name_min_zoom = 20;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
      }

      if (kind != "") {
        var poly = features.polygon(layerName)
                // Ids are only relevant at max_zoom, else they prevent merges
                //.setId(FeatureId.create(sf))
                .setAttr("kind", kind)
                .setAttr("min_zoom", theme_min_zoom)
                .setZoomRange(theme_min_zoom, theme_max_zoom)
                .setAttr("source", "openstreetmap.org")
                .setMinPixelSize(3.0);

        // TODO (nvkelso 2023-03-21)
        // What is a ghostFeature?!
        if (ghostFeatures) {
          poly.setAttr("isGhostFeature", true);
        }

        // TODO (nvkelso 2023-03-21)
        //      Why does this need to happen?
        poly.setAttr("area", "");

        // Polygons shouldn't have names
        //OsmNames.setOsmNames(poly, sf, theme_min_zoom);
      }
    }
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    processFeature(sf, features, NAME, false);
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // TODO
    // This should be 8 px buffer for lines and polygons

        // 'mz_transit_score', (transit_routes).score,
        //       'mz_transit_root_relation_id', (transit_routes).root_relation_id,
        //       'train_routes', (transit_routes).train_routes,
        //       'subway_routes', (transit_routes).subway_routes,
        //       'light_rail_routes', (transit_routes).light_rail_routes,
        //       'tram_routes', (transit_routes).tram_routes

           //  CASE
        //       WHEN mz_poi_min_zoom IS NOT NULL AND
        //            tags ? 'railway' AND tags->'railway'='station' AND osm_id > 0
        //         THEN mz_calculate_transit_routes_and_score(osm_id, NULL)
        //     END AS transit_routes,

    // Don't merge polygons at MAX_ZOOM
    if (zoom >= 14)
      return items;

    return FeatureMerge.mergeNearbyPolygons(items, 1, 1, 0.5, 0.5);
  }
}
