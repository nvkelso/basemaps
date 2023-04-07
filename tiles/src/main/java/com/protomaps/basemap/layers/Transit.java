package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class Transit implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "transit";
  }

  // TODO (nvkelso 2023-03-21)
  // 1.  add_uic_ref
  // 2.  route_name
  // 3.  parse_layer_as_float
  // 4.  spreadsheets/sort_rank/transit.csv
  // 5.  vectordatasource.transform.merge_line_features for all zooms but max
  // 6.  vectordatasource.transform.keep_n_features for stations at zooms 8 to 10
  // 7.  vectordatasource.transform.keep_n_features for stations at zooms 11 and 12
  // 8.  vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 9.  vectordatasource.transform.palettize_colours into colour_name
  // 10. vectordatasource.transform.add_collision_rank

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

    if (sf.canBeLine()) {
      if (sf.hasTag("route", "train", "subway", "light_rail", "tram", "funicular", "monorail")) {
        switch (sf.getString("route")) {
          case "train":
            if (sf.hasTag("service", "high_speed", "long_distance", "international")) {
              kind = "train";
              // TODO (v2) consider setting this as default kind?
              kind_detail = sf.getString("service");
              feature_min_zoom = 5;
              name_min_zoom = 10;
              theme_min_zoom = 4;
              theme_max_zoom = 15;
            } else {
              kind = "train";
              feature_min_zoom = 6;
              name_min_zoom = 12;
              theme_min_zoom = 5;
              theme_max_zoom = 15;
            }
            break;
          case "subway":
            kind = "subway";
            feature_min_zoom = 8;
            name_min_zoom = 12;
            theme_min_zoom = 7;
            theme_max_zoom = 15;
            break;
          case "light_rail":
          case "tram":
            kind = sf.getString("route");
            feature_min_zoom = 9;
            name_min_zoom = 13;
            theme_min_zoom = 8;
            theme_max_zoom = 15;
            break;
          case "funicular":
          case "monorail":
            kind = sf.getString("route");
            feature_min_zoom = 12;
            name_min_zoom = 14;
            theme_min_zoom = 11;
            theme_max_zoom = 15;
            break;
        }
      }

      if (kind != "") {
        var line = features.line(this.name())
                // Ids are only relevant at max_zoom, else they prevent merges
                //.setId(FeatureId.create(sf))
                .setAttr("kind", kind)
                .setAttr("min_zoom", theme_min_zoom)
                .setAttr("ref", sf.getString("ref"))
                .setAttr("operator", sf.getString("operator"))
                .setAttr("type", sf.getString("type"))
                .setAttr("colour", sf.getString("colour"))
                .setAttr("network", sf.getString("network"))
                .setAttr("state", sf.getString("state"))
                .setAttr("symbol", sf.getString("symbol"))
                .setAttr("description", sf.getString("description"))
                .setAttr("distance", sf.getString("distance"))
                .setAttr("ascent", sf.getString("ascent"))
                .setAttr("descent", sf.getString("descent"))
                .setAttr("roundtrip", sf.getString("roundtrip"))
                .setAttr("route_name", sf.getString("route_name"))
                .setAttr("layer", sf.getString("layer"))
                .setAttr("service", sf.getString("service"))
                .setZoomRange(theme_min_zoom - 1, theme_max_zoom)
                .setAttr("source", "openstreetmap.org")
                .setMinPixelSize(0)
                .setPixelTolerance(0);

        if (kind_detail != "") {
          line.setAttr("kind_detail", kind_detail);
        }

        // Polygons shouldn't have names
        OsmNames.setOsmNames(line, sf, name_min_zoom);
      }

      // TODO (nvkelso 2023-03-30)
      //      Some of these should be recast from a polygon?
      if (sf.isPoint()) {
        if (sf.hasTag("railway", "halt", "stop", "tram_stop")) {
          kind = sf.getString("route");
          feature_min_zoom = 13;
          name_min_zoom = 14;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        } else if (sf.hasTag("public_transport", "platform")) {
          kind = "platform";
          feature_min_zoom = 15;
          name_min_zoom = 15;
          theme_min_zoom = 14;
          theme_max_zoom = 15;

          if (sf.hasTag("bus", "yes")) {
            kind = "bus_stop";
            feature_min_zoom = 17;
            name_min_zoom = 16;
            theme_min_zoom = 15; // MAX_ZOOM
            theme_max_zoom = 15;
          }
        } else if (sf.hasTag("public_transport", "stop_area")) {
          kind = "stop_area";
          feature_min_zoom = 15;
          name_min_zoom = 15;
          theme_min_zoom = 14;
          theme_max_zoom = 15;
        } else if (sf.hasTag("railway", "platform", "station")) {
          kind = sf.getString("railway");
          feature_min_zoom = 15;
          name_min_zoom = 15;
          theme_min_zoom = 14;
          theme_max_zoom = 15;
        } else if (sf.hasTag("railway", "platform", "station")) {
          kind = sf.getString("railway");
          feature_min_zoom = 15;
          name_min_zoom = 15;
          theme_min_zoom = 14;
          theme_max_zoom = 15;
        } else if (sf.hasTag("site", "stop_area")) {
          kind = "stop_area";
          feature_min_zoom = 15;
          name_min_zoom = 15;
          theme_min_zoom = 14;
          theme_max_zoom = 15;
        } else if (sf.hasTag("highway", "bus_stop")) {
          kind = "bus_stop";
          feature_min_zoom = 17;
          name_min_zoom = 15; // MAX_ZOOM
          theme_min_zoom = 15; // MAX_ZOOM
          theme_max_zoom = 15;
        }

        if (kind != "") {
          var point = features.point(this.name())
                  // Ids are only relevant at max_zoom, else they prevent merges
                  //.setId(FeatureId.create(sf))
                  .setAttr("kind", kind)
                  .setAttr("min_zoom", theme_min_zoom)
                  .setAttr("ref", sf.getString("ref"))
                  .setAttr("operator", sf.getString("operator"))
                  .setAttr("type", sf.getString("type"))
                  .setAttr("colour", sf.getString("colour"))
                  .setAttr("network", sf.getString("network"))
                  .setAttr("state", sf.getString("state"))
                  .setAttr("symbol", sf.getString("symbol"))
                  .setAttr("description", sf.getString("description"))
                  .setAttr("distance", sf.getString("distance"))
                  .setAttr("ascent", sf.getString("ascent"))
                  .setAttr("descent", sf.getString("descent"))
                  .setAttr("roundtrip", sf.getString("roundtrip"))
                  .setAttr("route_name", sf.getString("route_name"))
                  .setAttr("layer", sf.getString("layer"))
                  .setAttr("service", sf.getString("service"))
                  .setZoomRange(theme_min_zoom - 1, theme_max_zoom)
                  .setAttr("source", "openstreetmap.org")
                  .setMinPixelSize(0)
                  .setPixelTolerance(0);

          if (kind_detail != "") {
            point.setAttr("kind_detail", kind_detail);
          }

          // Polygons shouldn't have names
          OsmNames.setOsmNames(point, sf, name_min_zoom);
        }
      }

      // nvkelso (20230329)
      // TODO (v2) should some of these go into a different kind class with kind_details?
      //       (!sf.hasTag("railway", "abandoned", "construction", "platform", "proposed"))) {
    }
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // TODO
    // This should also be 64 px buffer for points
    items = FeatureMerge.mergeLineStrings(items,
      0.5, // after merging, remove lines that are still less than 0.5px long
      0.5, // simplify output linestrings using a 0.1px tolerance
      8 // remove any detail more than 4px outside the tile boundary
    );

    return items;
  }
}
