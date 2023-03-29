package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class Natural implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "natural";
  }

  // TODO (nvkelso 2023-03-21)
  // 1. This should be called LANDCOVER not Natural
  // 2. Move national_park, protected_area, nature_reserve to LANDUSE layer
  // 3. From zoom 0 to 11 remap kind values for polygons "vectordatasource.transform.remap" in queries.yaml
  // 4. spreadsheets/sort_rank/landuse.csv
  // 5. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 6. vectordatasource.transform.add_collision_rank

  public void processNe(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    if (sourceLayer.equals("ne_50m_urban_areas")) {
      features.polygon(this.name()).setZoomRange(3, 4);
    } else if (sourceLayer.equals("ne_10m_urban_areas")) {
      features.polygon(this.name()).setZoomRange(5, 8);
    }
    // TODO (nvkelso 2023-03-21)
    // Set kind value & etc
    // 'source', 'naturalearthdata.com'
  }

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    if (sf.canBePolygon() && (sf.hasTag("natural", "wood", "glacier", "scrub", "sand", "wetland", "bare_rock") ||
      sf.hasTag("landuse", "forest", "meadow") || sf.hasTag("leisure", "nature_reserve") ||
      sf.hasTag("boundary", "national_park", "protected_area"))) {
      var feat = features.polygon(this.name())
        .setId(FeatureId.create(sf))
        .setAttr("natural", sf.getString("natural"))
        .setAttr("boundary", sf.getString("boundary"))
        .setAttr("landuse", sf.getString("landuse"))
        .setAttr("leisure", sf.getString("leisure"))
        .setAttr("source", "openstreetmap.org")
        .setZoomRange(7, 15);
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
