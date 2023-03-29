package com.protomaps.basemap.names;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import java.util.Map;

public class OsmNames {
  public static FeatureCollector.Feature setOsmNames(FeatureCollector.Feature feature, SourceFeature source,
    int minzoom) {
    for (Map.Entry<String, Object> tag : source.tags().entrySet()) {
      var key = tag.getKey();
      if (key.equals("name") || key.startsWith("name:")) {
        // TODO (nvkelso 2023-03-26)
        //     gaurd gainst silly OSM stuff
        //     name:etymology:wikidata: Q17455
        //     name:etymology:wikipedia: de:John von Neumann
        feature.setAttrWithMinzoom(key, source.getTag(key), minzoom);
      }
    }

    return feature;
  }
}
