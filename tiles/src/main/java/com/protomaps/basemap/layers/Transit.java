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
    // todo: exclude railway stations, levels
    if (sf.canBeLine() && (sf.hasTag("railway") ||
      sf.hasTag("aerialway", "cable_car") ||
      sf.hasTag("route", "ferry") ||
      sf.hasTag("aeroway", "runway", "taxiway")) &&
      (!sf.hasTag("railway", "abandoned", "construction", "platform", "proposed"))) {

      int minzoom = 11;

      if (sf.hasTag("service", "yard", "siding", "crossover")) {
        minzoom = 13;
      }

      var feature = features.line(this.name())
        .setId(FeatureId.create(sf))
        .setAttr("railway", sf.getString("railway"))
        .setAttr("route", sf.getString("route"))
        .setAttr("aeroway", sf.getString("aeroway"))
        .setAttr("service", sf.getString("service"))
        .setAttr("aerialway", sf.getString("aerialway"))
        .setAttr("network", sf.getString("network"))
        .setAttr("ref", sf.getString("ref"))
        .setAttr("highspeed", sf.getString("highspeed"))
        .setAttr("layer", sf.getString("layer"))
        .setZoomRange(minzoom, 15);

      String kind = "other";
      if (sf.hasTag("aeroway")) {
        kind = "aeroway";
      } else if (sf.hasTag("railway")) {
        kind = "railway";
      } else if (sf.hasTag("ferry")) {
        kind = "ferry";
      } else if (sf.hasTag("aerialway")) {
        kind = "aerialway";
      }

      feature.setAttr("kind", kind);
      // nvkelso (20230321)
      // TODO
      //    'source', 'openstreetmap.org'

      OsmNames.setOsmNames(feature, sf, 0);
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
      0.1, // simplify output linestrings using a 0.1px tolerance
      8 // remove any detail more than 4px outside the tile boundary
    );

    return items;
  }
}
