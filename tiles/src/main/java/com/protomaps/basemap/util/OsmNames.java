package com.protomaps.basemap.util;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import java.util.Map;

import com.protomaps.basemap.util.LanguageUtils;

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

        // (nvkelso) 2023-04-11
        //           We deliberately don't overstuff name_* with name_en values
        //           as we assume the map style can do a coallese in the name source
        //           client-side. If that's not true, then you'd need to add more
        //           overstuffing logic here.
      }
    }

    return feature;
  }
}
