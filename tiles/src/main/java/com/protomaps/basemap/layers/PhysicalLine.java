package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class PhysicalLine implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "physical_line";
  }

  // TODO (nvkelso 2023-03-21)
  // 1. This is more akin to the earth layer in Tilezen v1.9
  // 2. But it's also cramming in waterways?!
  // 3. spreadsheets/sort_rank/landuse.csv

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    if (sf.canBeLine() && (sf.hasTag("waterway") ||
      sf.hasTag("natural", "strait", "cliff")) && (!sf.hasTag("waterway", "riverbank", "reservoir"))) {
      var feat = features.line(this.name())
        .setId(FeatureId.create(sf))
        .setAttr("waterway", sf.getString("waterway"))
        .setAttr("natural", sf.getString("natural"))
        .setAttr("source", "openstreetmap.org")
        .setZoomRange(12, 15);

      String kind = "other";
      if (sf.hasTag("waterway")) {
        kind = "waterway";
      } else if (sf.hasTag("natural")) {
        kind = "natural";
      }

      feat.setAttr("kind", kind);
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
    // This should be 8 px buffer

    return items;
  }
}
