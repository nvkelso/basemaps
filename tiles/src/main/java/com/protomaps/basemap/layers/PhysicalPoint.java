package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class PhysicalPoint implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "physical_point";
  }

  // TODO (nvkelso 2023-03-21)
  // 1. This is more akin to the earth layer in Tilezen v1.9
  // 2. spreadsheets/sort_rank/landuse.csv

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    if (sf.isPoint() && (sf.hasTag("place", "sea", "ocean") || sf.hasTag("natural", "peak"))) {

      // TODO: rank based on ele

      int minzoom = 12;
      if (sf.hasTag("natural", "peak")) {
        minzoom = 13;
      }
      if (sf.hasTag("place", "sea")) {
        minzoom = 3;
      }

      var feat = features.point(this.name())
        .setId(FeatureId.create(sf))
        .setAttr("place", sf.getString("place"))
        .setAttr("natural", sf.getString("natural"))
        .setAttr("ele", sf.getString("ele"))
        .setAttr("source", "openstreetmap.org")
        .setZoomRange(minzoom, 15);

      // nvkelso (20230321)
      // TODO
      //    'source', 'openstreetmap.org'

      OsmNames.setOsmNames(feat, sf, 0);
    }
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // TODO
    // This should be 256 px buffer (continents, oceans)

    return items;
  }
}
