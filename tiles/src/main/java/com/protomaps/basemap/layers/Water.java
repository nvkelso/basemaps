package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeoUtils;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.geo.GeometryType;
import com.onthegomap.planetiler.reader.SourceFeature;
// TODO (nvkelso 2023-03-21)
// This doesn't exist
//import com.protomaps.basemap.util.NeNames;
import com.protomaps.basemap.util.OsmNames;

import java.util.ArrayList;
import java.util.List;

public class Water implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {
  @Override
  public String name() {
    return "water";
  }

  private static final double WORLD_AREA_FOR_70K_SQUARE_METERS =
          Math.pow(GeoUtils.metersToPixelAtEquator(0, Math.sqrt(70_000)) / 256d, 2);
  private static final double LOG2 = Math.log(2);

  // TODO (nvkelso 2023-03-21)
  // 1.  parse_layer_as_float
  // 2.  water_tunnel
  // 3.  set kind propertiy on NE water
  // 4.  set other properties on NE water
  // 5.  spreadsheets/sort_rank/water.csv (order of operations is unusual here)
  // 6.  vectordatasource.transform.exterior_boundaries
  // 7.  Only start including 'lake' kind names from zoom 7 for line geoms (2 parts)
  // 8.  Don't show lake labels from polygons (including from NE) at zoom 0 to 2
  // 9.  Lots of min_zoom name dropping logic in vectordatasource.transform.drop_properties for all kinds
  // 10. perform vectordatasource.transform.handle_label_placement
  // 11. perform vectordatasource.transform.update_min_zoom to make sure labels fit in polygons
  // 12. don't need id, area, boundary, other properties except at zoom 14+ (perform vectordatasource.transform.drop_properties)
  // 12. don't need name properties on polygons except at zoom 14+ (perform vectordatasource.transform.drop_properties)
  // 13. perform vectordatasource.transform.drop_features_where to drop polygons of kinds that are only label placements (like sea)
  // 14. perform vectordatasource.transform.merge_polygon_features
  // 15. perform vectordatasource.transform.merge_line_features
  // 16. perform vectordatasource.transform.rank_features on bay, strait, fjord
  // 17. vectordatasource.transform.keep_n_features for bay, strait, fjord at mid zooms (see Physical Points)
  // 18. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 19. vectordatasource.transform.drop_small_inners
  // 20. vectordatasource.transform.add_collision_rank

  public void processNe(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    var kind = "";
    // TODO (nvkelso 2023-03-27)
    //      These should all be booleans, yo
    var alkaline = "";
    var boundary = "";
    var reservoir = "";
    var theme_min_zoom = 0;
    var theme_max_zoom = 0;

    // TODO (nvkelso 2023-03-25)
    //      These should all be per feature instead of layer... but per feature per layer
    if (sourceLayer.equals("ne_110m_ocean") || sourceLayer.equals("ne_110m_lakes") || sourceLayer.equals("ne_110m_playas") || sourceLayer.equals("ne_110m_coastline")) {
      // use default zooms
      kind = "water";
    } else if (sourceLayer.equals("ne_50m_ocean") || sourceLayer.equals("ne_50m_lakes") || sourceLayer.equals("ne_50m_playas") || sourceLayer.equals("ne_50m_coastline")) {
      theme_min_zoom = 1;
      theme_max_zoom = 3;
      kind = "water";
    } else if (sourceLayer.equals("ne_10m_ocean") || sourceLayer.equals("ne_10m_lakes") || sourceLayer.equals("ne_10m_playas") || sourceLayer.equals("ne_10m_coastline")) {
      theme_min_zoom = 4;
      theme_max_zoom = 6;
      kind = "water";
    }

    if (kind == "water") {
      switch (sf.getString("featurecla")) {
        // This is a geom type of linestring in a multi-geom layer, that is marked as water boundary somehow else?
        // TODO boolean boundary
        case "Coastline" -> {
          kind = "ocean";
          boundary = "yes";
        }
        // TODO boolean alkaline
        case "Alkaline Lake" -> {
          kind = "lake";
          alkaline = "yes";
        }
        case "Lake" -> kind = "lake";
        // TODO boolean reservoir
        case "Reservoir" -> {
          kind = "lake";
          reservoir = "yes";
        }
        case "Playa" -> kind = "playa";
        case "Ocean" -> kind = "ocean";
      }
    }

    if (kind != "" && sf.hasTag("min_zoom")) {
      var feature = features.polygon(this.name())
              .setAttr("kind", kind)
              .setAttr("name", sf.getString("name", null))
              .setAttr("wikidata", sf.getLong("wikidata"))
              // TODO (nvkelso 2023-03-25)
              //      This should be a single decimal precision float not string
              .setAttr("min_zoom", sf.getLong("min_zoom"))
              .setZoomRange(sf.getString("min_zoom") == null ? theme_min_zoom : (int) Double.parseDouble(sf.getString("min_zoom")), theme_max_zoom)
              .setAttr("source", "naturalearthdata.com")
              .setBufferPixels(8);

//      if (boundary != "") {
//        features.line.setAttr("boundary", boundary);
//      }
//      if (alkaline != "") {
//        features.polygon.setAttr("alkaline", alkaline);
//      }
//      if (reservoir != "") {
//        features.polygon.setAttr("reservoir", reservoir);
//      }

      //NeNames.setNeNames(feature, sf, 0);

      if (sourceLayer.equals("ne_110m_coastline") || sourceLayer.equals("ne_50m_coastline") || sourceLayer.equals("ne_10m_coastline")) {
        // TODO (nvkelso 2023-03-25)
        //      This shouldn't be exported if null
        feature.setAttr("boundary", true);
      }
    }
  }

  // Process the "blue" water ocean, seas, and bays that come from a
  // source that doesn't have broken polygons (but by then are generic water)
  public void processOsm(SourceFeature sf, FeatureCollector features) {
    features.polygon(this.name())
      .setAttr("kind", "ocean")
      // TODO (nvkelso 2023-03-25)
      // Should we also export way_area as area?
      //      Should this be variable zoom?
      .setAttr("min_zoom", 0.0)
      .setAttr("source", "osmdata.openstreetmap.de")
      .setZoomRange(7, 15).setBufferPixels(8);
  }

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    //var name = "";
    var kind = "";
    var kind_detail = "";
    // TODO (nvkelso 2023-03-27)
    //      These should all be booleans, yo
    var alkaline = "";
    var boundary = "";
    var reservoir = "";
    //var tunnel = "";
    //var layer = "";
    //var wikidata_id = ""; // from wikidata
    //var intermittent = ""; // yes or basin in [infiltration, detention]
    var feature_min_zoom = 20;
    var name_min_zoom = 20;
    var theme_min_zoom = 20;
    var theme_max_zoom = 20;

    if (sf.isPoint() && sf.hasTag("name") && sf.hasTag("place", "ocean", "sea")) {
      // OSM water "place" = sea labels can source from point and polygon tables
      // deal with the sea polygons later
      switch (sf.getString("place")) {
        case "ocean":
          kind = "ocean";
          feature_min_zoom = 1;
          name_min_zoom = 2;
          theme_min_zoom = 0;
          theme_max_zoom = 15;
          break;
        // Some seas are also tagged bays, and we want to prioritize sea so we order sea first
        // Most OSM seas are points, but some are polygons
        case "sea":
          kind = "sea";
          // TODO (nvkelso 2023-03-26)
          //       This should be a range (from 2-7 by area), 5 is just a default
          //       And consistency check between the zooms
          feature_min_zoom = 5;
          name_min_zoom = 3;
          theme_min_zoom = 4;
          theme_max_zoom = 15;
          break;
      }

      var feature = features.point(this.name())
              .setAttr("kind", kind)
              .setAttr("min_zoom", feature_min_zoom)
              .setAttr("label_position", true)
              .setAttr("source", "openstreetmap.org")
              .setZoomRange(theme_min_zoom, theme_max_zoom)
              .setBufferPixels(256);

      OsmNames.setOsmNames(feature, sf, name_min_zoom);
    }

    // don't export covered water features
    if (!sf.hasTag("covered", "yes") &&
            // TODO: reef (geom polygon, though)
            (sf.hasTag("natural", "water", "bay", "strait", "fjord") ||
                    sf.hasTag("waterway") ||
                    sf.hasTag("landuse", "basin", "reservoir") ||
                    sf.hasTag("leisure", "swimming_pool") ||
                    sf.hasTag("amenity", "swimming_pool"))) {

      // defaults for polygons (with area threashold applied)
      // lines below have overrides
      feature_min_zoom = 8;
      theme_min_zoom = 6;
      theme_max_zoom = 15;

      // TODO: reef (geom polygon, though)
      if (sf.hasTag("natural", "water", "bay", "strait", "fjord")) {
        kind = sf.getString("natural");
        if (sf.hasTag("water", "basin", "canal", "ditch", "drain", "lake", "river", "stream")) {
          kind_detail = sf.getString("water");
          if (sf.hasTag("water", "lagoon", "oxbow", "pond", "reservoir", "wastewater")) {
            kind_detail = "lake";
          }
          if (sf.hasTag("water", "reservoir")) {
            reservoir = "yes";
          }
          if (sf.hasTag("water", "lagoon", "salt", "salt_pool")) {
            alkaline = "yes";
          }
        }
      } else if (sf.hasTag("waterway", "riverbank", "dock", "canal", "river", "stream", "ditch", "drain")) {
        kind = sf.getString("waterway");
      } else if (sf.hasTag("landuse", "basin", "reservoir")) {
        kind = sf.getString("landuse");
      } else if (sf.hasTag("leisure", "swimming_pool")) {
        kind = "swimming_pool";
      } else if (sf.hasTag("amenity", "swimming_pool")) {
        kind = "swimming_pool";
      }

      switch (kind) {
        case "sea" -> name_min_zoom = 3;
        case "bay" -> name_min_zoom = 4;
        case "fjord" -> name_min_zoom = 4;
        case "strait" -> name_min_zoom = 5;
        case "lake" -> name_min_zoom = 4;
        case "playa" -> name_min_zoom = 5;
        case "reef" -> name_min_zoom = 5;
        case "river" -> { name_min_zoom = 11;
                          feature_min_zoom = 11;
                          theme_min_zoom = 10; }
        case "water" -> name_min_zoom = 12;
        case "riverbank" -> name_min_zoom = 12;
        case "canal" -> { name_min_zoom = 12;
                          if (sf.hasTag("boat", "yes")) {
                            feature_min_zoom = 9;
                            theme_min_zoom = 8;
                          } else {
                            feature_min_zoom = 11;
                            theme_min_zoom = 10;
                          }
        }
        case "basin" -> name_min_zoom = 12;
        case "dock" -> name_min_zoom = 12;
        case "dam" -> name_min_zoom = 13;
        case "stream" -> { name_min_zoom = 13;
                          feature_min_zoom = 11;
                          theme_min_zoom = 10; }
        case "ditch" -> { name_min_zoom = 14;
          feature_min_zoom = 16;
          theme_min_zoom = 15; }
        case "drain" -> { name_min_zoom = 14;
          feature_min_zoom = 16;
          theme_min_zoom = 15; }
        case "swimming_pool" -> name_min_zoom = 14;
        case "fountain" -> name_min_zoom = 14;
      }

      // These are for water polygons
      // While above are for lines and polygons
      // In 2.0 Tilezen we'd remove this section
      // as the kind would have this value directly
      // (otherwise name_min_zoom would be set to 12)
      // Java doesn't like nested switches, and to keep the above
      // a short form switch we add this breakout here
      if( kind == "water" ) {
        switch( kind_detail) {
          case "lake" -> name_min_zoom = 4;
          case "river" -> name_min_zoom = 11;
          case "basin" -> name_min_zoom = 12;
          case "canal" -> name_min_zoom = 12;
          case "stream" -> name_min_zoom = 13;
          case "ditch" -> name_min_zoom = 14;
          case "drain" -> name_min_zoom = 14;
        }
      }

      if (sf.canBePolygon()) {
        Double way_area = 0.0;
        try { way_area = sf.area(); } catch(GeometryException e) {  System.out.println(e); }
        feature_min_zoom = 17;

        // TODO (nvkelso) 20230327
        //      Java says these numbers are "too long" lmao
        //      in any event, they aren't relevant since we don't show lake labels until at least zoom 8?
//        if( way_area > 20000000000) {
//          feature_min_zoom = 1;
//        } else
//        if( way_area > 10000000000) {
//          feature_min_zoom = 2;
//        } else
//        if( way_area >  5000000000) {
//          feature_min_zoom = 3;
//        } else
        if( way_area >  2000000000) {
          feature_min_zoom = 4;
        } else
        if( way_area >   500000000) {
          feature_min_zoom = 5;
        } else
        if( way_area >   200000000) {
          feature_min_zoom = 6;
        } else
        if( way_area >    50000000) {
          feature_min_zoom = 7;
        } else
        if( way_area >    20000000) {
          feature_min_zoom = 8;
        } else
        if( way_area >     5000000) {
          feature_min_zoom = 9;
        } else
        if( way_area >    1000000) {
          feature_min_zoom = 10;
        } else
        if( way_area >     200000) {
          feature_min_zoom = 11;
        } else
        if( way_area >      50000) {
          feature_min_zoom = 12;
        } else
        if( way_area >      20000) {
          feature_min_zoom = 13;
        } else
        if( way_area >       2000) {
          feature_min_zoom = 14;
        } else
        if( way_area >       1000) {
          feature_min_zoom = 15;
        } else
        if( way_area >        400) {
          feature_min_zoom = 16;
        }

        var feat = features.polygon(this.name())
                // Ids are only relevant at max_zoom, else they prevent merges
                //.setId(FeatureId.create(sf))
                .setAttr("kind", kind)
                .setAttr("min_zoom", feature_min_zoom)
                .setAttr("source", "openstreetmap.org")
                .setZoomRange(theme_min_zoom, theme_max_zoom)
                .setMinPixelSize(3.0)
                .setBufferPixels(8);

        if ( kind_detail != "" ) {
          feat.setAttr("kind_detail", kind_detail);
        }
        if (sf.hasTag("water", "reservoir")) {
          feat.setAttr("reservoir", true);
        }
        if (sf.hasTag("water", "lagoon", "salt", "salt_pool")) {
          feat.setAttr("alkaline", true);
        }

        if (sf.hasTag("boundary") && sf.getString("boundary") != null) {
          feat.setAttr("boundary", sf.getString("boundary"));
        }
        if (sf.hasTag("layer") && sf.getString("layer") != null) {
          feat.setAttr("layer", sf.getString("layer"));
        }
        if (sf.hasTag("tunnel", "yes")) {
          feat.setAttr("tunnel", true);
        }
        if (sf.hasTag("wikidata_id") && sf.getString("wikidata_id") != null) {
          feat.setAttrWithMinzoom("wikidata_id", sf.getString("wikidata_id"), 14);
        }

        // Derive additional water label position points
        // Yes, this means there are points and polygons and lines all in same layer!
        if( sf.hasTag("name") && sf.getTag( "name") != null) {

          // The min_zoom should vary with area, and be
          // further dilated for labels
          // Consider moving this to post processing on polygons
          // so we don't generate lables for rediculously small areas

          // default names to zoom 16 for polygons
          name_min_zoom = 16;

          // TODO (nvkelso) 20230327
          //      Java says these numbers are "too long" lmao
          //      in any event, they aren't relevant since we don't show lake labels until at least zoom 8?
//          if( way_area > 1000000000000) {
//            name_min_zoom = 1;
//          }
//          if( way_area > 500000000000) {
//            name_min_zoom = 2;
//          } else
//          if( way_area > 250000000000) {
//            name_min_zoom = 3;
//          } else
//          if( way_area > 120000000000) {
//            name_min_zoom = 4;
//          } else
//          if( way_area >  80000000000) {
//            name_min_zoom = 5;
//          } else
//          if( way_area >  40000000000) {
//            name_min_zoom = 6;
//          } else
//          if( way_area >  10000000000) {
//            name_min_zoom = 7;
//          } else
          if( way_area >    500000000) {
            name_min_zoom = 8;
          } else
          if( way_area >    200000000) {
            name_min_zoom = 9;
          } else
          if( way_area >    40000000) {
            name_min_zoom = 10;
          } else
          if( way_area >     8000000) {
            name_min_zoom = 11;
          } else
          if( way_area >     1000000) {
            name_min_zoom = 12;
          } else
          if( way_area >      500000) {
            name_min_zoom = 13;
          } else
          if( way_area >       50000) {
            name_min_zoom = 14;
          } else
          if( way_area >       10000) {
            name_min_zoom = 15;
          }

          // Find the polygons centroid and use it as label position
          var water_label_position = features.pointOnSurface(this.name())
              .setAttr("name", sf.getString( "name"))
              .setAttr("kind", kind)
              .setAttr("min_zoom", name_min_zoom)
              .setAttr("label_position", true)
              .setAttr("source", "openstreetmap.org")
              // TODO (nvkelso) 20230329
              //      This **should** work, but the min_zoom calcs above are broken,
              //      so everything shows up at zoom 15/17 instead
              //.setZoomRange(name_min_zoom-1, theme_max_zoom)
              .setZoomRange(theme_min_zoom+2, theme_max_zoom)
              // TODO (nvkelso) 20230327
              //      This should be a boolean, and other sanity checks on values
              .setAttr("intermittent", sf.getString("intermittent"))
              .setBufferPixels(128);

          if ( kind_detail != "" ) {
            if (kind == "water") {
              // TODO (nvkelso) 20230328
              //      For some reason this doesn't overwrite an already set kind attr?
              water_label_position.setAttr("kind", kind_detail);
            } else {
              water_label_position.setAttr("kind_detail", kind_detail);
            }
          }

          if (sf.hasTag("water", "reservoir")) {
             water_label_position.setAttr("reservoir", true);
          }
          if (sf.hasTag("water", "lagoon", "salt", "salt_pool")) {
            water_label_position.setAttr("alkaline", true);
          }

          if (sf.hasTag("boundary") && sf.getString("boundary") != null) {
            water_label_position.setAttr("boundary", sf.getString("boundary"));
          }
          if (sf.hasTag("layer") && sf.getString("layer") != null) {
            water_label_position.setAttr("layer", sf.getString("layer"));
          }
          if (sf.hasTag("tunnel", "yes")) {
            water_label_position.setAttr("tunnel", true);
          }
          if (sf.hasTag("wikidata_id") && sf.getString("wikidata_id") != null) {
            water_label_position.setAttrWithMinzoom("wikidata_id", sf.getString("wikidata_id"), 14);
          }

          OsmNames.setOsmNames(water_label_position, sf, name_min_zoom-1);
        }
      }
      if (sf.canBeLine()) {
        var feat = features.line(this.name())
                // Ids are only relevant at max_zoom, else they prevent merges
                //.setId(FeatureId.create(sf))
                .setAttr("kind", kind)
                .setAttr("min_zoom", feature_min_zoom)
                .setAttr("source", "openstreetmap.org")
                .setZoomRange(theme_min_zoom, theme_max_zoom)
                .setBufferPixels(8);

        if ( kind_detail != "" ) {
          feat.setAttr("kind_detail", kind_detail);
        }
        if (sf.hasTag("boat", "yes")) {
          feat.setAttr("boat", true);
        }

        if (sf.hasTag("boundary") && sf.getString("boundary") != null) {
          feat.setAttr("boundary", sf.getString("boundary"));
        }
        if (sf.hasTag("layer") && sf.getString("layer") != null) {
          feat.setAttr("layer", sf.getString("layer"));
        }
        if (sf.hasTag("tunnel", "yes")) {
          feat.setAttr("tunnel", true);
        }
        if (sf.hasTag("wikidata_id") && sf.getString("wikidata_id") != null) {
          feat.setAttrWithMinzoom("wikidata_id", sf.getString("wikidata_id"), 14);
        }

        OsmNames.setOsmNames(feat, sf, name_min_zoom);
      }
    }
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    if (zoom >= 14)
      return items;

    List<VectorTile.Feature> processedFeatures = new ArrayList<>();
    List<VectorTile.Feature> linesToMerge = new ArrayList<>();
    List<VectorTile.Feature> polygonsToMerge = new ArrayList<>();

    for (VectorTile.Feature item : items) {
      // TODO nvkelso (20230404)
      //      Exclude polygons for some features that were only used to create label centroids
      //      Else their "water" polygon "flooding" islands!
      //if( item.attrs() in "sea", "bay", "fjord", "strait", "marina" (?) ) {
      // something more here
      //}

      if ( item.geometry().geomType() == GeometryType.LINE ) {
        linesToMerge.add(item);
      } else if (item.geometry().geomType() == GeometryType.POLYGON ) {
        polygonsToMerge.add(item);
      } else {
        processedFeatures.add(item);
      }
    }
    var lineMerged = FeatureMerge.mergeLineStrings(linesToMerge, 0.0, 0.5, 8);
    var polyMerged = FeatureMerge.mergeNearbyPolygons(polygonsToMerge, 1, 1, 0.5, 0.5);

    processedFeatures.addAll(polyMerged);
    processedFeatures.addAll(lineMerged);

    return processedFeatures;
  }
}
