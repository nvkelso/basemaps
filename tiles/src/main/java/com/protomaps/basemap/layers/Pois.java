package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class Pois implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "pois";
  }

  // TODO (nvkelso 2023-03-21)
  // 1.  add_iata_code_to_airports
  // 2.  normalize_tourism_kind
  // 3.  normalize_social_kind
  // 4.  normalize_medical_kind
  // 5.  add_uic_ref
  // 6.  remove_zero_area
  // 7.  make_representative_point
  // 8.  height_to_meters
  // 9.  pois_capacity_int
  // 10. pois_direction_int
  // 11. major_airport_detector
  // 12. elevation_to_meters
  // 13. normalize_operator_values
  // 14. perform vectordatasource.transform.remove_duplicate_features when name, kind are same
  // 15. perform vectordatasource.transform.merge_duplicate_stations
  // 16. perform vectordatasource.transform.normalize_station_properties
  // 17. perform vectordatasource.transform.rank_features for stations
  // 18. perform vectordatasource.transform.rank_features on peak, volcano
  // 19. perform vectordatasource.transform.remove_duplicate_features when name is same (why with 14?)
  // 20. perform vectordatasource.transform.update_parenthetical_properties for closed and historical
  //     - This should be in process features
  // 21. vectordatasource.transform.keep_n_features for peak, volcano at mid zooms (see Physical Points)
  //     - look at "label grid" functionality in planetiler (exists)
  // 22. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 23. vectordatasource.transform.add_collision_rank

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    //var name = "";
    var kind = "";
    var kind_detail = "";
    var feature_min_zoom = 20;
    var name_min_zoom = 20;
    var theme_min_zoom = 20;
    var theme_max_zoom = 20;

    // Feature must have a name and be either a point or a polygon (to derive a point)
    if( !sf.canBeLine() &&
            (sf.isPoint() || sf.canBePolygon()) &&
            (sf.getTag( "name") != null  || sf.getTag( "name:en") != null ||
             sf.getTag("name:de") != null || sf.getTag("name:es") != null ))
    {
      if (sf.hasTag("aeroway", "aerodrome", "airport", "heliport") &&
              !sf.hasTag("aerodrome:type", "military" ))
      {
        kind = sf.getString("aeroway");
        if( sf.hasTag("international_flights", "yes" ) ) {
          kind_detail = "international";
        } else
        if( sf.hasTag("aerodrome", "international","public", "private", "airfield", "regional", "gliding" ) ) {
          kind_detail = sf.getString("aerodrome");
        } else
        if( sf.hasTag("aerodrome:type", "public", "private", "airfield", "international", "regional", "gliding" ) ) {
          kind_detail = sf.getString("aerodrome:type");
        } else
        if( sf.hasTag("aerodrome:type", "military/public" ) ) {
          kind_detail = "military_public";
        } else
        if( sf.hasTag("aerodrome", "military/public" ) ) {
          kind_detail = "military_public";
        }

        // feature_min_zoom = 20;
        // name_min_zoom = 20;
        theme_min_zoom = 7;
        theme_max_zoom = 15;
      }
      if (sf.hasTag("amenity") && sf.getString("amenity") != null) {
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
      if (sf.hasTag("boundary") && sf.getString("boundary") != null) {
        switch (sf.getString("boundary")) {
          case "national_park":
            kind = "national_park";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
          case "protected_area":
            kind = "protected_area";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 6;
            theme_max_zoom = 15;
            break;
        }
      } else if (sf.hasTag("landuse") && sf.getString("landuse") != null) {
        switch (sf.getString("landuse")) {
          case "park":
            kind = "park";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
          case "recreation_ground":
            kind = "recreation_ground";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 11;
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
        }
      } else if (sf.hasTag("leisure") && sf.getString("leisure") != null) {
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
      } else if (sf.hasTag("man_made") && sf.getString("man_made") != null) {
        switch (sf.getString("man_made")) {
          case "pier":
            kind = "pier";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 10;
            theme_max_zoom = 15;
            break;
        }
      } else if (sf.hasTag("railway") && sf.getString("railway") != null) {
        switch (sf.getString("railway")) {
          case "platform":
            kind = "platform";
            // feature_min_zoom = 20;
            // name_min_zoom = 20;
            theme_min_zoom = 14;
            theme_max_zoom = 15;
            break;
        }
      } else if (sf.hasTag("tourism") && sf.getString("tourism") != null) {
        switch (sf.getString("tourism")) {
          case "zoo":
            kind = "zoo";
            feature_min_zoom = 13;
            // name_min_zoom = 20;
            theme_min_zoom = 12;
            theme_max_zoom = 15;
            break;
        }
      } else if (sf.hasTag("zoo") && sf.getString("tourism") != null) {
        switch (sf.getString("zoo")) {
          case "wildlife_park":
            kind = "wildlife_park";
            feature_min_zoom = 17;
            // name_min_zoom = 20;
            theme_min_zoom = 15;
            theme_max_zoom = 15;
            break;
        }
      }

      if (sf.hasTag("area", "yes")) {
        if (sf.hasTag("highway", "pedestrian", "footway")) {
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
        if (sf.hasTag("man_made", "bridge") && sf.getString("man_made", "bridge") != null) {
          kind = "bridge";
          // feature_min_zoom = 20;
          // name_min_zoom = 20;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
      }

      if (kind != "") {
        if (sf.canBePolygon()) {
          var landuse_label_position = features.pointOnSurface(this.name())
                  // since POIs aren't merged, set the IDs
                  .setId(FeatureId.create(sf))
                  .setAttr("kind", kind)
                  .setAttr("min_zoom", theme_min_zoom +2)
                  .setAttr("source", "openstreetmap.org")
                  .setBufferPixels(128)
                  // HACK: Don't show POIs earlier than zoom 12 until area filters sorted
                  .setZoomRange(Math.max(theme_min_zoom + 2, 12), theme_max_zoom);

          // TODO (nvkelso 2023-03-21)
          // What is a ghostFeature?!
          //        if (ghostFeatures) {
          //          landuse_label_position.setAttr("isGhostFeature", true);
          //        }

          // TODO (nvkelso 2023-03-21)
          //      Why does this need to happen?
          //landuse_label_position.setAttr("area", "");

          OsmNames.setOsmNames(landuse_label_position, sf, Math.max(theme_min_zoom+2, 12));
        }
        // By this time we know it's a point
        else {
          var point = features.point(this.name())
                  // since POIs aren't merged, set the IDs
                  .setId(FeatureId.create(sf))
                  .setAttr("kind", kind)
                  .setAttr("min_zoom", theme_min_zoom +2)
                  .setAttr("source", "openstreetmap.org")
                  .setBufferPixels(128)
                  // HACK: Don't show POIs earlier than zoom 12 until area filters sorted
                  .setZoomRange(Math.max(theme_min_zoom +2, 12), theme_max_zoom);

          OsmNames.setOsmNames(point, sf, Math.max(theme_min_zoom+2, 12));
        }
      }
    } else {
      // There are actually a few POIs that are allowed to not have a name, in Tilezen these are PONIs
    }
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // TODO
    // This should be 64 px buffer

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


    return items;
  }
}
