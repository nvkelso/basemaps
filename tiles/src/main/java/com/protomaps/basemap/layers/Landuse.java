package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.geo.GeometryType;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.Parse;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;

import java.util.ArrayList;
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


  // TODO (nvkelso 2023-04-04)
//        These are inline below, but should be parametarized
//    - &tier1_min_zoom
//  lookup:
//  key: { col: way_area }
//  op: '>='
//  table:
//          - [ 3, 300000000 ]
//          - [ 4, 300000000 ]
//          - [ 5, 150000000 ]
//          - [ 6, 150000000 ]
//          - [ 7, 100000000 ]
//          - [ 8,  10000000 ]
//          - [ 9,   5000000 ]
//          - [ 10,  1000000 ]
//          - [ 11,   500000 ]
//          - [ 12,   200000 ]
//          - [ 13,   100000 ]
//          - [ 14,    50000 ]
//          - [ 15,    10000 ]
//  default: 16
//          - &tier2_min_zoom
//  lookup:
//  key: { col: way_area }
//  op: '>='
//  table:
//          - [ 4, 1000000000 ]
//          - [ 5, 1000000000 ]
//          - [ 6,  150000000 ]
//          - [ 7,  100000000 ]
//          - [ 8,   10000000 ]
//          - [ 9,    5000000 ]
//          - [ 10,   1000000 ]
//          - [ 11,    500000 ]
//          - [ 12,    150000 ]
//          - [ 13,     50000 ]
//          - [ 14,     20000 ]
//          - [ 15,      2000 ]
//  default: 16
//          - &tier3_min_zoom
//  lookup:
//  key: { col: way_area }
//  op: '>='
//  table:
//          - [ 8, 10000000 ]
//          - [ 9,  5000000 ]
//          - [ 10, 1000000 ]
//          - [ 11,  500000 ]
//          - [ 12,  200000 ]
//          - [ 13,  100000 ]
//          - [ 14,   50000 ]
//          - [ 15,    2000 ]
//  default: 16
//          - &tier4_min_zoom
//  lookup:
//  key: { col: way_area }
//  op: '>='
//  table:
//          - [ 10, 1000000 ]
//          - [ 11,  500000 ]
//          - [ 12,  200000 ]
//          - [ 13,  100000 ]
//          - [ 14,   50000 ]
//          - [ 15,    2000 ]
//  default: 16
//          - &tier5_min_zoom
//  lookup:
//  key: { col: way_area }
//  op: '>='
//  table:
//          - [ 10, 1000000 ]
//          - [ 11,  400000 ]
//          - [ 12,  200000 ]
//          - [ 13,   50000 ]
//          - [ 14,   20000 ]
//          - [ 15,    2000 ]
//  default: 16
//          - &tier6_min_zoom
//  lookup:
//  key: { col: way_area }
//  op: '>='
//  table:
//          - [ 12, 500000 ]
//          - [ 13, 100000 ]
//          - [ 14,  50000 ]
//          - [ 15,   5000 ]
//  default: 16
//          - &small_parks_min_zoom
//  lookup:
//  key: { col: way_area }
//  op: '>='
//  table:
//          - [ 8,   400000000 ]
//          - [ 9,    40000000 ]
//          - [ 10,   10000000 ]
//          - [ 11,    4000000 ]
//          - [ 12,    2000000 ]
//          - [ 13,     200000 ]
//          - [ 14,     100000 ]
//          - [ 15,      10000 ]
//  default: 16
//          - &us_forest_service
//        - United States Forest Service
//        - US Forest Service
//        - U.S. Forest Service
//        - USDA Forest Service
//        - United States Department of Agriculture
//        - US National Forest Service
//        - United State Forest Service
//        - U.S. National Forest Service
//  - &us_parks_service
//        - United States National Park Service
//        - National Park Service
//        - US National Park Service
//        - U.S. National Park Service
//        - US National Park service
//  - &not_national_park_protection_title
//        - Conservation Area
//        - Conservation Park
//        - Environmental use
//        - Forest Reserve
//        - National Forest
//        - National Wildlife Refuge
//        - Nature Refuge
//        - Nature Reserve
//        - Protected Site
//        - Provincial Park
//        - Public Access Land
//        - Regional Reserve
//        - Resources Reserve
//        - State Forest
//        - State Game Land
//        - State Park
//        - Watershed Recreation Unit
//        - Wild Forest
//        - Wilderness Area
//        - Wilderness Study Area
//        - Wildlife Management
//        - Wildlife Management Area
//        - Wildlife Sanctuary
//  // allowlist for leaf_type
//  - &leaftype_kind_detail
//  kind_detail:
//          case:
//          - when: {leaf_type: [broadleaved, leafless, mixed, needleleaved]}
//  then: {col: leaf_type}
//  // allowlist of religions to use in kind_detail (do not use for plain
//  // religion tag)
//  - &religion_kind_detail
//  kind_detail:
//          case:
//          - when:
//  religion:
//          - animist
//              - bahai
//              - buddhist
//              - caodaism
//              - catholic
//              - christian
//              - confucian
//              - hindu
//              - jain
//              - jewish
//              - multifaith
//              - muslim
//              - pagan
//              - pastafarian
//              - scientologist
//              - shinto
//              - sikh
//              - spiritualist
//              - taoist
//              - tenrikyo
//              - unitarian_universalist
//              - voodoo
//              - yazidi
//              - zoroastrian
//  then: {col: religion}

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

  public static void processFeature(SourceFeature sourceFeature, FeatureCollector features, String layerName,
    boolean ghostFeatures) {
    var sourceLayer = sourceFeature.getSourceLayer();
    //var name = "";
    var kind = "";
    var kind_detail = "";
    var denomination = "";
    var feature_min_zoom = 20;
    var name_min_zoom = 20;
    var theme_min_zoom = 20;
    var theme_max_zoom = 20;

    //////////////////////////////////////////////////////////////
    // NOT IN ANY TIER
    //
    // note that these come first, as they are more specific tags
    // and should override more generic tags below, but these
    // aren't in any tier.
    //////////////////////////////////////////////////////////////
    if( sourceFeature.hasTag("zoo", "enclosure") ) {
      kind = "enclosure";
      // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("zoo", "petting_zoo") ) {
      kind = "petting_zoo";
      // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("zoo", "aviary") ) {
      kind = "aviary";
      // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("attraction", "animal") ) {
      kind = "animal";
      // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("attraction", "water_slide") ) {
      kind = "water_slide";
      // min_zoom: { clamp: { max: 16, min: 15, value: { col: zoom } } }
      // feature_min_zoom:  = 15;
      theme_min_zoom = 15;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("attraction", "roller_coaster") ) {
      kind = "roller_coaster";
      // min_zoom: { clamp: { max: 16, min: 15, value: { col: zoom } } }
      // feature_min_zoom:  = 15;
      theme_min_zoom = 15;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("attraction", "summer_toboggan") ) {
      kind = "summer_toboggan";
      // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("attraction", "carousel") ) {
      kind = "carousel";
      // min_zoom: { clamp: { max: 16, min: 15, value: { col: zoom } } }
      // feature_min_zoom:  = 15;
      theme_min_zoom = 15;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("attraction", "amusement_ride") ) {
      kind = "amusement_ride";
      // min_zoom: { clamp: { max: 16, min: 15, value: { col: zoom } } }
      // feature_min_zoom:  = 15;
      theme_min_zoom = 15;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("historic", "fort") ) {
      kind = "fort";
      // min_zoom: { clamp: { max: 16, min: 11, value: { col: zoom } } }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 2 OVERRIDES
    //
    // These are things which are "more specific" than things in
    // tier 1, so they should match first.
    //////////////////////////////////////////////////////////////
    else if( sourceFeature.hasTag("boundary", "national_park") &&
            sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
    ) {
      kind = "forest";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 4;
      theme_max_zoom = 15;
    }
    else if( ( sourceFeature.hasTag("leisure", "park") ||
               sourceFeature.hasTag("landuse", "park")
             ) &&
             sourceFeature.hasTag("park:type", "state_recreational_area")
    ) {
      kind = "park";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 4;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("boundary", "national_park") &&
            sourceFeature.hasTag("protect_class", "6") &&
            sourceFeature.hasTag("protection_title", "National Forest")
    ) {
      kind = "forest";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 4;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("boundary", "national_park") &&
            ( sourceFeature.hasTag("protect_class", "6") ||
                    sourceFeature.hasTag("designation", "area_of_outstanding_natural_beauty") )
    ) {
      kind = "park";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 4;
      theme_max_zoom = 15;
    }
    else if( ( sourceFeature.hasTag("boundary:type", "protected_area") ||
               sourceFeature.hasTag("boundary", "protected_area") ) &&
            sourceFeature.hasTag("leisure", "nature_reserve") &&
            sourceFeature.hasTag("protect_class", "4", "5") &&
            ! sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
    ) {
      kind = "nature_reserve";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 3;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 6 OVERRIDES
    //
    // These are things which are "more specific" than things in
    // tier 1, so they should match first.
    //////////////////////////////////////////////////////////////
    // common
    else if( sourceFeature.hasTag("boundary:type", "protected_area") &&
            sourceFeature.hasTag("leisure", "common") &&
            sourceFeature.hasTag("protect_class", "5") &&
            ! (sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service") ||
                    sourceFeature.hasTag("operator", "United States National Park Service", "National Park Service", "US National Park Service", "U.S. National Park Service", "US National Park service")
            )
    ) {
      kind = "common";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 1
    //////////////////////////////////////////////////////////////
    else if( sourceFeature.hasTag("historic", "battlefield") &&
            ! sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
    ) {
      kind = "battlefield";
      // tier: 1
      // feature_min_zoom = *tier1_min_zoom;
      theme_min_zoom = 2;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("boundary", "national_park") &&
            !(sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service") ||
                    sourceFeature.hasTag("protection_title", "Conservation Area", "Conservation Park", "Environmental use", "Forest Reserve", "National Forest", "National Wildlife Refuge", "Nature Refuge", "Nature Reserve", "Protected Site", "Provincial Park", "Public Access Land", "Regional Reserve", "Resources Reserve", "State Forest", "State Game Land", "State Park", "Watershed Recreation Unit", "Wild Forest", "Wilderness Area", "Wilderness Study Area", "Wildlife Management", "Wildlife Management Area", "Wildlife Sanctuary")
            ) &&
            ( sourceFeature.hasTag("protect_class", "2", "3") ||
                    sourceFeature.hasTag("operator", "United States National Park Service", "National Park Service", "US National Park Service", "U.S. National Park Service", "US National Park service") ||
                    sourceFeature.hasTag("operator:en", "Parks Canada") ||
                    sourceFeature.hasTag("designation", "national_park") ||
                    sourceFeature.hasTag("protection_title", "National Park")
            )
    ) {
      kind = "national_park";
      // tier: 1
      // feature_min_zoom = *tier1_min_zoom;
      theme_min_zoom = 2;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 2
    //////////////////////////////////////////////////////////////

    else if( sourceFeature.hasTag("boundary", "national_park") &&
            ! ( sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service") ||
                    sourceFeature.hasTag("protection_title", "Conservation Area", "Conservation Park", "Environmental use", "Forest Reserve", "National Forest", "National Wildlife Refuge", "Nature Refuge", "Nature Reserve", "Protected Site", "Provincial Park", "Public Access Land", "Regional Reserve", "Resources Reserve", "State Forest", "State Game Land", "State Park", "Watershed Recreation Uni", "Wild Forest", "Wilderness Area", "Wilderness Study Area", "Wildlife Management", "Wildlife Management Area", "Wildlife Sanctuary")
            ) &&
            (
                    sourceFeature.hasTag("protect_class", "2", "3") ||
                            sourceFeature.hasTag("protect_title", "National Park", "National Monument")
            )
    ) {
      kind = "national_park";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 3;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // mid-tier 2 overrides
    //
    // the boundary=national_park appears to get used for a ton of
    // stuff that isn't actually a national park, so we match a
    // few things in advance which seem to be more specific.
    //////////////////////////////////////////////////////////////
    else if( sourceFeature.hasTag("natural", "wetland") ) {
      kind = "wetland";
      // min_zoom: { max: [ 9, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 9;
      theme_max_zoom = 15;

      if( sourceFeature.hasTag("wetland", "bog", "fen", "mangrove", "marsh", "mud", "reedbed", "saltern", "saltmarsh", "string_bog", "swamp", "tidalflat", "wet_meadow") ) {
        kind_detail = sourceFeature.getString("wetland");
      }
    }

    //////////////////////////////////////////////////////////////
    // TIER 2 (continued...)
    //////////////////////////////////////////////////////////////
    else if( sourceFeature.hasTag("leisure", "park") ||
            sourceFeature.hasTag("landuse", "park")  ||
            sourceFeature.hasTag("boundary", "national_park")
    ) {
      kind = "park";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 3;
      theme_max_zoom = 15;
    }

    // forests
    else if( sourceFeature.hasTag("landuse", "forest") &&
            sourceFeature.hasTag("protect_class", "6")
    ) {
      kind = "forest";
      //tier: 2
      // min_zoom: { max: [ 6, *tier2_min_zoom ] }
      // feature_min_zoom:  = 6;
      theme_min_zoom = 8;
      theme_max_zoom = 15;

      // allowlist for leaf_type
      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }
    else if( sourceFeature.hasTag("landuse", "forest") &&
            sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
    ) {
      kind = "forest";
      // tier: 2
      // min_zoom: { max: [ 6, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 8;
      theme_max_zoom = 15;

      // allowlist for leaf_type
      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }

    else if( sourceFeature.hasTag("landuse", "forest") ) {
      kind = "forest";
      // tier: 2
      // min_zoom: { max: [ 9, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 8;
      theme_max_zoom = 15;

      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }
    // nature reserves
    else if( sourceFeature.hasTag("leisure", "nature_reserve") ) {
      kind = "nature_reserve";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 3;
      theme_max_zoom = 15;
    }

    // Bureau of Land Management protected areas
    else if( sourceFeature.hasTag("boundary", "protected_area") &&
            sourceFeature.hasTag("operator", "BLM", "US Bureau of Land Management")
    ) {
      kind = "protected_area";
      // tier: 2
      // min_zoom: { max: [ 8, *small_parks_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }

    // scientific protected areas?
    else if( sourceFeature.hasTag("boundary", "protected_area") &&
            sourceFeature.hasTag("protect_class", "1", "1a", "1b")
    ) {
      kind = "protected_area";
      // tier: 2
      // min_zoom: { max: [ 9, *small_parks_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 8;
      theme_max_zoom = 15;
    }
    // protected areas
    else if( sourceFeature.hasTag("boundary", "protected_area") ) {
      kind = "protected_area";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 3;
      theme_max_zoom = 15;
    }

    // woods
    else if( sourceFeature.hasTag("landuse", "wood") &&
            // *us_forest_service YAML indirection
            sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
    ) {
      kind = "wood";
      // tier: 2
      // min_zoom: { max: [ 6, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 5;
      theme_max_zoom = 15;

      // *leaftype_kind_detail indirection
      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }

    else if( sourceFeature.hasTag("landuse", "wood") ) {
      kind = "wood";
      // tier: 2
      // min_zoom: { max: [ 10, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 9;
      theme_max_zoom = 15;

      // *leaftype_kind_detail indirection
      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }

    // TODO (v2) this is a funky?
    else if( sourceFeature.hasTag("natural", "forest") &&
            // *us_forest_service YAML indirection
            sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
    ) {
      kind = "natural_forest";
      // tier: 2
      // min_zoom: { max: [ 6, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 5;
      theme_max_zoom = 15;

      // *leaftype_kind_detail indirection
      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }
    // TODO (v2) this is a funky?
    else if( sourceFeature.hasTag("natural", "forest") ) {
      kind = "natural_forest";
      // tier: 2
      // min_zoom: { max: [ 10, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 9;
      theme_max_zoom = 15;

      // *leaftype_kind_detail indirection
      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }
    // TODO (v2) this is a funky?
    else if( sourceFeature.hasTag("natural", "wood") ) {
      kind = "natural_wood";
      // tier: 2
      // min_zoom: { max: [ 9, *tier2_min_zoom ] }
      // feature_min_zoom:  = 11;
      theme_min_zoom = 9;
      theme_max_zoom = 15;

      // *leaftype_kind_detail indirection
      if( sourceFeature.hasTag("leaf_type", "broadleaved", "leafless", "mixed", "needleleaved") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }
    else if( sourceFeature.hasTag("landuse", "urban") ) {
      kind = "urban";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 3;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "rural") ) {
      kind = "rural";
      // tier: 2
      // min_zoom: *tier2_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 3;
      theme_max_zoom = 15;
    }
    // interplay with Natural Earth urban_area
    else if( sourceFeature.hasTag("landuse", "residential") ) {
      kind = "residential";
      // tier: 2
      // feature_min_zoom = { max: [ 10, *tier2_min_zoom ] };
      theme_min_zoom = 7;
      theme_max_zoom = 15;
    }

    else if( sourceFeature.hasTag("landuse", "farm") ) {
      kind = "farm";
      // tier: 2
      // feature_min_zoom = { max: [ 9, *tier2_min_zoom ] };
      theme_min_zoom = 8;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "farmland") ) {
      kind = "farmland";
      // tier: 2
      // feature_min_zoom = { max: [ 9, *tier2_min_zoom ] };
      theme_min_zoom = 8;
      theme_max_zoom = 15;
    }


    //////////////////////////////////////////////////////////////
    // NO TIER
    //
    // these are often co-tagged landuse=military, but are "more
    // specific", so we want them to match first.
    //////////////////////////////////////////////////////////////
    else if( sourceFeature.hasTag("military", "danger_area") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "danger_area";
      // min_zoom: { clamp: { max: 16, min: 11, value: { col: zoom } } }
      // feature_min_zoom = 11;
      theme_min_zoom = 10;
      theme_max_zoom = 15;
      // extra_columns: [way]
    }
    else if( sourceFeature.hasTag("military", "range") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "range";
      // min_zoom: { clamp: { max: 16, min: 11, value: { col: zoom } } }
      // feature_min_zoom = 11;
      theme_min_zoom = 10;
      theme_max_zoom = 15;
      // extra_columns: [way]
    }

    //////////////////////////////////////////////////////////////
    // TIER 3
    //////////////////////////////////////////////////////////////
    else if( sourceFeature.hasTag("military", "airfield") ||
            ( sourceFeature.hasTag("aeroway", "aerodrome") &&
                    sourceFeature.hasTag("aerodrome:type", "military")    )
    ) {
      kind = "airfield";
      kind_detail = "military";
      // tier: 3
      // feature_min_zoom = *tier3_min_zoom;
      theme_min_zoom = 7;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("aeroway", "aerodrome") &&
            ! sourceFeature.hasTag("aerodrome:type", "military") ) {
      kind = "aerodrome";
      // tier: 3
      // feature_min_zoom = *tier3_min_zoom;
      theme_min_zoom = 7;
      theme_max_zoom = 15;

      if( sourceFeature.hasTag("international_flights", "yes") ) {
        kind_detail = "international";
      }
      else if( sourceFeature.hasTag("aerodrome", "international") ) {
        kind_detail = "international";
      }
      else if( sourceFeature.hasTag("aerodrome:type", "public", "private", "airfield", "international", "regional", "gliding") ) {
        kind_detail = sourceFeature.getString("aerodrome:type");
      }
      else if( sourceFeature.hasTag("aerodrome:type", "military/public") ) {
        kind_detail = "military_public";
      }
      else if( sourceFeature.hasTag("aerodrome", "public", "private", "airfield", "international", "regional", "gliding") ) {
        kind_detail = sourceFeature.getString("aerodrome");
      }

      // TODO
      //passenger_count: {call: {func: util.safe_int, args: [{col: passenger_count}]}}
    }
    else if( sourceFeature.hasTag("military", "naval_base") ) {
      kind = "naval_base";
      // tier: 3
      // min_zoom: *tier3_min_zoom
      // feature_min_zoom:  = 8;
      theme_min_zoom = 7;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "military") ) {
      kind = "military";
      // tier: 3
      // min_zoom: *tier3_min_zoom
      // feature_min_zoom:  = 8;
      theme_min_zoom = 7;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("amenity", "university") ) {
      kind = "university";
      // tier: 3
      // min_zoom: *tier3_min_zoom
      // feature_min_zoom:  = 8;
      theme_min_zoom = 7;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("amenity", "college") ) {
      kind = "college";
      // tier: 3
      // min_zoom: *tier3_min_zoom
      // feature_min_zoom:  = 8;
      theme_min_zoom = 7;
      theme_max_zoom = 15;
    }
    // glacier
    else if( sourceFeature.hasTag("natural", "glacier") ) {
      kind = "glacier";
      // tier: 3
      // min_zoom: *tier3_min_zoom
      // feature_min_zoom:  = 8;
      theme_min_zoom = 7;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 4
    //////////////////////////////////////////////////////////////
    // cemetery & grave_yard
    else if( sourceFeature.hasTag("landuse", "cemetery") ||
            sourceFeature.hasTag("amenity", "grave_yard")
    ) {
      if( sourceFeature.hasTag("landuse", "cemetery") ) {
        kind = "cemetery";
      } else {
        kind = "grave_yard";
      }
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 11;
      theme_min_zoom = 9;
      theme_max_zoom = 15;

      // allowlist of religions to use in kind_detail
      // (but do not use for plain religion tag)
      if( sourceFeature.hasTag("religion", "animist", "bahai", "buddhist", "caodaism", "catholic", "christian", "confucian", "hindu", "jain", "jewish", "multifaith", "muslim", "pagan", "pastafarian", "scientologist", "shinto", "sikh", "spiritualist", "taoist", "tenrikyo", "unitarian_universalist", "voodoo", "yazidi", "zoroastrian") ) {
        kind_detail = sourceFeature.getString("religion");
      }

      // pass thru without sanitizing (v2 task)
      denomination = sourceFeature.getString("denomination");
    }
    // commercial
    else if( sourceFeature.hasTag("landuse", "commercial") ) {
      kind = "commercial";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // golf_course
    else if( sourceFeature.hasTag("leisure", "golf_course") ) {
      kind = "golf_course";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // hospital
    else if( sourceFeature.hasTag("amenity", "hospital") ) {
      kind = "hospital";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // industrial
    else if( sourceFeature.hasTag("landuse", "industrial") ) {
      kind = "industrial";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // plant
    else if( sourceFeature.hasTag("power", "plant") ) {
      kind = "plant";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // generator
    else if( sourceFeature.hasTag("power", "generator") ) {
      kind = "generator";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // substation
    else if( sourceFeature.hasTag("power", "substation", "station", "sub_station") ) {
      kind = "substation";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // railway
    else if( sourceFeature.hasTag("landuse", "railway") ) {
      kind = "railway";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // sports_centre
    else if( sourceFeature.hasTag("leisure", "sports_centre") ) {
      kind = "sports_centre";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 12;
      theme_max_zoom = 15;
    }
    // recreation_ground
    else if( sourceFeature.hasTag("landuse", "recreation_ground") ) {
      kind = "recreation_ground";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 12;
      theme_max_zoom = 15;
    }
    // retail
    else if( sourceFeature.hasTag("landuse", "retail") ) {
      kind = "retail";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // stadium
    else if( sourceFeature.hasTag("leisure", "stadium") ) {
      kind = "stadium";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // zoo
    else if( sourceFeature.hasTag("tourism", "zoo") ) {
      kind = "zoo";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // wildlife_park
    else if( sourceFeature.hasTag("zoo", "wildlife_park") ) {
      kind = "wildlife_park";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // winter_sports
    else if( sourceFeature.hasTag("landuse", "winter_sports") ) {
      kind = "winter_sports";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // port, ferry, container terminals
    //
    // if these are also water features, then we don't want to output a polygon
    // for this. because we can't alter the geometry type at this stage, we handle
    // this by setting a parameter that indicates we should drop the polygon at a
    // later stage.
    else if( sourceFeature.hasTag("landuse", "harbour", "port", "port_terminal", "ferry_terminal", "container_terminal") ) {
      kind = sourceFeature.getString("landuse");
      // feature_min_zoom = { clamp: { min: 13, max: 16, value: { sum: [ { col: zoom }, 1.81 ] } } };
      theme_min_zoom = 13;
      theme_max_zoom = 15;
      // TODO
      //  // mz = metazen :-)
      //  mz_drop_polygon:
      //    case:
      //      - when:
      //          any:
      //            - { waterway: true }
      //            - { natural: [water, bay, strait, fjord, reef] }
      //            - { landuse: [reservoir, basin] }
      //        then: true
    }
    // pier with mooring
    else if( sourceFeature.hasTag("man_made", "pier") &&
            sourceFeature.canBePolygon() &&
            sourceFeature.hasTag("mooring", "no", "yes", "commercial", "cruise", "customers", "declaration", "ferry", "guest", "private", "public", "waiting", "yacht", "yachts")
    ) {
      kind = "pier";
      kind_detail = sourceFeature.getString("mooring");
      // feature_min_zoom = { clamp: { min: 11, max: 16, value: { sum: [ { col: zoom }, 1.81 ] } } };
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // pier without mooring

    else if( sourceFeature.hasTag("man_made", "pier") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "pier";
      // feature_min_zoom = { clamp: { min: 11, max: 16, value: { sum: [ { col: zoom }, 1.81 ] } } };
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }

    // wastewater_plant
    else if( sourceFeature.hasTag("man_made", "wastewater_plant") ) {
      kind = "wastewater_plant";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 12;
      theme_max_zoom = 15;
    }
    // works
    else if( sourceFeature.hasTag("man_made", "works") ) {
      kind = "works";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // bridge
    else if( sourceFeature.hasTag("man_made", "bridge") ) {
      kind = "bridge";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 12;
      theme_max_zoom = 15;
    }
    // tower
    else if( sourceFeature.hasTag("man_made", "tower") ) {
      kind = "tower";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // breakwater
    else if( sourceFeature.hasTag("man_made", "breakwater") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "breakwater";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // water_works
    else if( sourceFeature.hasTag("man_made", "water_works") ) {
      kind = "water_works";
      // tier: 4
      // min_zoom: *tier4_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    // groyne
    else if( sourceFeature.hasTag("man_made", "groyne") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "groyne";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // dike
    else if( sourceFeature.hasTag("man_made", "dike") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "dike";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // cutline
    else if( sourceFeature.hasTag("man_made", "cutline") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "cutline";
      // tier: 4
      // min_zoom: { max: [ 13, *tier4_min_zoom ] }
      // feature_min_zoom:  = 10;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 5
    //////////////////////////////////////////////////////////////
    // theme_park (NOTE: also allow and normalise 'Theme Park' to deal with vandalism)
    else if( sourceFeature.hasTag("tourism", "theme_park", "Theme Park") ) {
      kind = "theme_park";
      // tier: 5
      // min_zoom: *tier5_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 10;
      theme_max_zoom = 15;
    }
    // resort
    else if( sourceFeature.hasTag("leisure", "resort") ) {
      kind = "resort";
      // tier: 5
      // min_zoom: *tier5_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 10;
      theme_max_zoom = 15;
    }
    // aquarium
    else if( sourceFeature.hasTag("tourism", "aquarium") ) {
      kind = "aquarium";
      // tier: 5
      // min_zoom: { max: [ 12, *tier5_min_zoom ] }
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // winery
    else if( sourceFeature.hasTag("tourism", "winery") ) {
      kind = "winery";
      // tier: 5
      // min_zoom: { max: [ 13, *tier5_min_zoom ] }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // maze
    else if( sourceFeature.hasTag("attraction", "maze") ) {
      kind = "maze";
      // tier: 5
      // min_zoom: *tier5_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 10;
      theme_max_zoom = 15;
    }
    // beach
    else if( sourceFeature.hasTag("natural", "beach") ) {
      kind = "beach";
      // tier: 5
      // min_zoom: *tier5_min_zoom
      // feature_min_zoom:  = 10;
      theme_min_zoom = 10;
      theme_max_zoom = 15;

      // allowlist most common surface values for beaches. i'm not quite sure i'd
      // call surface=grass a beach, but it's in the data..?
      if( sourceFeature.hasTag("surface", "grass", "gravel", "pebbles", "pebblestone", "rocky", "sand") ) {
        kind_detail = sourceFeature.getString("leaf_type");
      }
    }
    // orchard
    else if( sourceFeature.hasTag("landuse", "orchard") ) {
      kind = "orchard";
      // feature_min_zoom = { max: [ 9, *tier2_min_zoom ] };
      theme_min_zoom = 9;
      theme_max_zoom = 15;

      if( sourceFeature.hasTag("trees", "agave_plants", "almond_trees", "apple_trees", "avocado_trees", "banana_plants", "cherry_trees", "coconut_palms", "coffea_plants", "date_palms", "hazel_plants", "hop_plants", "kiwi_plants", "macadamia_trees", "mango_trees", "oil_palms", "olive_trees", "orange_trees", "papaya_trees", "peach_trees", "persimmon_trees", "pineapple_plants", "pitaya_plants", "plum_trees", "rubber_trees", "tea_plants", "walnut_trees") ) {
        kind_detail = sourceFeature.getString("trees");
      }
    }
    // plant nursery
    else if( sourceFeature.hasTag("landuse", "plant_nursery") ) {
      kind = "plant_nursery";
      // feature_min_zoom = *tier5_min_zoom;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 6
    //////////////////////////////////////////////////////////////
    // garden
    else if( sourceFeature.hasTag("leisure", "garden") ) {
      kind = "garden";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // allotments
    else if( sourceFeature.hasTag("landuse", "allotments") ) {
      kind = "allotments";
      // tier: 6
      // min_zoom: { max: [ 13, *tier6_min_zoom ] }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // pedestrian
    else if( sourceFeature.hasTag("highway", "pedestrian") &&
            sourceFeature.hasTag("area", "yes") &&
            sourceFeature.canBePolygon()
    ) {
      kind = "pedestrian";
      // tier: 6
      // min_zoom: { max: [ 13, *tier6_min_zoom ] }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // common
    else if( sourceFeature.hasTag("leisure", "common") ) {
      kind = "common";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // pitch
    else if( sourceFeature.hasTag("leisure", "pitch") ) {
      kind = "pitch";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // place_of_worship
    else if( sourceFeature.hasTag("amenity", "place_of_worship") ) {
      kind = "place_of_worship";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // playground
    else if( sourceFeature.hasTag("leisure", "playground") ) {
      kind = "playground";
      // tier: 6
      // min_zoom: { max: [ 13, *tier6_min_zoom ] }
      // feature_min_zoom:  = 13;
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }
    // school
    else if( sourceFeature.hasTag("amenity", "school") ) {
      kind = "school";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // kindergarten (new to Tilezen landuse?)
    else if( sourceFeature.hasTag("amenity", "kindergarten") ) {
      kind = "kindergarten";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 15;
      theme_min_zoom = 14;
      theme_max_zoom = 15;
    }
    // attraction
    else if( sourceFeature.hasTag("tourism", "attraction") ) {
      kind = "attraction";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // artwork
    else if( sourceFeature.hasTag("tourism", "artwork") ) {
      kind = "artwork";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // wilderness_hut
    else if( sourceFeature.hasTag("tourism", "wilderness_hut") ) {
      kind = "wilderness_hut";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // hanami
    else if( sourceFeature.hasTag("tourism", "hanami") ) {
      kind = "hanami";
      // tier: 6
      // min_zoom: *tier6_min_zoom
      // feature_min_zoom:  = 12;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // TIER 6 EXTRA - PARKING
    //////////////////////////////////////////////////////////////
    else if( sourceFeature.hasTag("amenity", "parking") ) {
      kind = "parking";
      // TODO
      // tier: 6
      //    min_zoom:
      //      lookup:
      //        key: { col: way_area }
      //        op: '>='
      //        table:
      //          - [ 14, 50000 ]
      //          - [ 15,  5000 ]
      //        default: 16
      theme_min_zoom = 13;
      theme_max_zoom = 15;
    }

    //////////////////////////////////////////////////////////////
    // NOT IN ANY TIER
    //////////////////////////////////////////////////////////////
    // park without US Parks Service operator (see above)
    else if( sourceFeature.hasTag("leisure", "park", "national_park") ||
            sourceFeature.hasTag("landuse", "park", "national_park")
    ) {
      kind = "park";
      // feature_min_zoom = { clamp: { min: 9, max: 14, value: { sum: [ { col: zoom }, 2 ] } } };
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "grass") ) {
      kind = "grass";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "meadow") ) {
      kind = "meadow";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "village_green") ) {
      kind = "village_green";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "quarry") ) {
      kind = "quarry";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("natural", "land") ) {
      kind = "land";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("natural", "scrub") ) {
      kind = "scrub";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("natural", "park") ) {
      kind = "natural_park";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("natural", "bare_rock", "desert", "grassland", "heath", "sand", "shingle") ) {
      kind = sourceFeature.getString("natural");
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "vineyard") ) {
      kind = "vineyard";
      // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
      // feature_min_zoom = 9;
      theme_min_zoom = 9;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("highway", "footway") &&
              sourceFeature.hasTag("area", "yes") &&
              sourceFeature.canBePolygon()
      ) {
      kind = "footway";
      // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
      // feature_min_zoom = 13;
      theme_min_zoom = 12;
      theme_max_zoom = 15;
    }
      else if( sourceFeature.hasTag("amenity", "library") ) {
        kind = "library";
        // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
        // feature_min_zoom = 13;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "fuel") ) {
        kind = "fuel";
        // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
        // feature_min_zoom = 13;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "cinema") ) {
        kind = "cinema";
        // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
        // feature_min_zoom = 13;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "theatre") ) {
        kind = "theatre";
        // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
        // feature_min_zoom = 13;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "prison") ) {
      kind = "prison";
      // min_zoom: { clamp: { max: 16, min: 11, value: { col: zoom } } }
      // feature_min_zoom = 11;
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    else if( (sourceFeature.hasTag("aeroway", "runway") ||
                sourceFeature.hasTag("area:aeroway", "runway")) &&
                sourceFeature.hasTag("area", "yes") &&
                sourceFeature.canBePolygon()
        ) {
          kind = "runway";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( (sourceFeature.hasTag("aeroway", "taxiway") ||
                sourceFeature.hasTag("area:aeroway", "taxiway")) &&
                sourceFeature.hasTag("area", "yes") &&
                sourceFeature.canBePolygon()
        ) {
          kind = "taxiway";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( (sourceFeature.hasTag("aeroway", "apron") ||
                sourceFeature.hasTag("area:aeroway", "apron")) &&
                sourceFeature.hasTag("area", "yes") &&
                sourceFeature.canBePolygon()
        ) {
          kind = "apron";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("tourism", "trail_riding_station") ) {
          kind = "trail_riding_station";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("natural", "scree") ) {
          kind = "scree";
          // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
          // feature_min_zoom = 9;
          theme_min_zoom = 9;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("leisure", "water_park") ) {
          kind = "water_park";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("waterway", "dam") &&
                sourceFeature.canBeLine()
        ) {
          kind = "dam";
          // feature_min_zoom = 12;
          theme_min_zoom = 11;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("waterway", "dam") &&
                sourceFeature.hasTag("area", "yes") &&
                sourceFeature.canBePolygon()
        ) {
          kind = "dam";
          // min_zoom: { clamp: { max: 16, min: 11, value: { col: zoom } } }
          // feature_min_zoom = 11;
          theme_min_zoom = 11;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("leisure", "dog_park") ) {
          kind = "dog_park";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("leisure", "track") ) {
          kind = "recreation_track";
          // min_zoom: { clamp: { max: 16, min: 12, value: { col: zoom } } }
          // feature_min_zoom = 12;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("natural", "stone") ) {
          kind = "stone";
          // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
          // feature_min_zoom = 9;
          theme_min_zoom = 9;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("natural", "rock") ) {
          kind = "rock";
          // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
          // feature_min_zoom = 9;
          theme_min_zoom = 9;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("natural", "mud") ) {
          kind = "mud";
          // min_zoom: { clamp: { max: 16, min: 9, value: { col: zoom } } }
          // feature_min_zoom = 9;
          theme_min_zoom = 9;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("tourism", "caravan_site") ) {
          kind = "caravan_site";
          // min_zoom: { clamp: { max: 16, min: 12, value: { col: zoom } } }
          // feature_min_zoom = 12;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("tourism", "picnic_site") ) {
          kind = "picnic_site";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("natural", "tree_row") ) {
          kind = "tree_row";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        // NOTE: This is a line
        else if( sourceFeature.hasTag("barrier", "hedge") ) {
          kind = "hedge";
          // min_zoom: { clamp: { max: 16, min: 13, value: { col: zoom } } }
          // feature_min_zoom = 13;
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("highway", "services") ) {
          kind = "service_area";
          // min_zoom: { clamp: { min: 11, max: 16, value: { col: zoom } } }
          // feature_min_zoom = 11;
          theme_min_zoom = 11;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("highway", "rest_area") ) {
          kind = "rest_area";
          // min_zoom: { clamp: { min: 11, max: 16, value: { col: zoom } } }
          // feature_min_zoom = 11;
          theme_min_zoom = 11;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("barrier", "city_wall") ||
                sourceFeature.hasTag("historic", "citywalls")
        ) {
          kind = "city_wall";
          // feature_min_zoom = 12;
          theme_min_zoom = 11;
          theme_max_zoom = 15;
        }
        // NOTE: This is a line
        else if( sourceFeature.hasTag("man_made", "snow_fence") ) {
          kind = "snow_fence";
          // feature_min_zoom = 15;
          theme_min_zoom = 15;
          theme_max_zoom = 15;
        }
        // NOTE: This is a line
        else if( sourceFeature.hasTag("barrier", "retaining_wall") ) {
          kind = "retaining_wall";
          // feature_min_zoom = 15;
          theme_min_zoom = 15;
          theme_max_zoom = 15;
        }
        // NOTE: This is a line
        else if( sourceFeature.hasTag("barrier", "wall") ) {
          kind = "wall";
          // feature_min_zoom = 16;
          theme_min_zoom = 15;  // MAX_ZOOM
          theme_max_zoom = 15;

          if( sourceFeature.hasTag("wall", "dry_stone", "noise_barrier", "brick", "stone", "pise", "castle_wall", "seawall", "jersey_barrier", "flood_wall", "concrete", "gabion") ) {
            kind_detail = sourceFeature.getString("wall");
          }
          // common miss-taggins in OSM
          if( sourceFeature.hasTag("wall", "drystone", "stone_wall") ) {
            kind_detail = "dry_stone";
          }
        }
        // NOTE: This is a line
        else if( sourceFeature.hasTag("barrier", "kerb", "guard_rail", "ditch") ) {
          kind = sourceFeature.getString("barrier");
          // feature_min_zoom = 16;
          theme_min_zoom = 15;  // MAX_ZOOM
          theme_max_zoom = 15;
        }
        // NOTE: This is a line
        else if( sourceFeature.hasTag("man_made", "embankment", "cutting") ) {
          kind = sourceFeature.getString("man_made");
          // feature_min_zoom = 16;
          theme_min_zoom = 15;  // MAX_ZOOM
          theme_max_zoom = 15;
        }

        // allowlist fence_type if it's present, otherwise don't include it. this
        // means we don't get random values in the output that we can't document.
        // NOTE: This is a line
        else if( sourceFeature.hasTag("barrier", "fence") &&
                sourceFeature.hasTag("fence_type", "avalanche", "barbed_wire", "bars", "brick", "chain", "chain_link", "concrete", "drystone_wall", "electric", "grate", "hedge", "metal", "metal_bars", "net", "pole", "railing", "railings", "split_rail", "steel", "stone", "wall", "wire", "wood")
        ) {
          kind = "fence";
          kind_detail = sourceFeature.getString("fence_type");;
          // feature_min_zoom = 16;
          theme_min_zoom = 15; // MAX_ZOOM
          theme_max_zoom = 15;
        }
// NOTE: This is a line
else if( sourceFeature.hasTag("barrier", "wire_fence") ) {
          kind = "fence";
          kind_detail = "wire";
          // feature_min_zoom = 16;
          theme_min_zoom = 15;  // MAX_ZOOM
          theme_max_zoom = 15;
        }
// NOTE: This is a line
else if( sourceFeature.hasTag("power", "line") ) {
          kind = "power_line";
          // feature_min_zoom = 14;
          theme_min_zoom = 14;
          theme_max_zoom = 15;
        }
// NOTE: This is a line
else if( sourceFeature.hasTag("power", "minor_line") ) {
          kind = "power_minor_line";
          // feature_min_zoom = 17;
          theme_min_zoom = 17;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("tourism", "camp_site") ) {
          kind = "camp_site";
          // feature_min_zoom = { max: [ 12, *tier5_min_zoom ] };
          theme_min_zoom = 12;
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("barrier", "gate") ) {
          kind = "gate";
          // feature_min_zoom = 16;
          theme_min_zoom = 15;  // MAX_ZOOM
          theme_max_zoom = 15;
        }
        else if( sourceFeature.hasTag("man_made", "gacranete") &&
        sourceFeature.canBeLine() ) {
          kind = "crane";
          // feature_min_zoom = 16;
          theme_min_zoom = 15;  // MAX_ZOOM
          theme_max_zoom = 15;

          if( sourceFeature.hasTag("crane:type", "portal_crane", "gantry_crane", "travel_lift", "floor-mounted_crane", "shiploader", "tower_crane" ) ) {
            kind_detail = sourceFeature.getString("crane:type");
          }
    }
    else if( sourceFeature.hasTag("landuse", "shipyard") ) {
      kind = "shipyard";
      // feature_min_zoom = 15;
      theme_min_zoom = 15;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("waterway", "boatyard") ) {
      kind = "boatyard";
      // feature_min_zoom = 15;
      theme_min_zoom = 15;
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("landuse", "wharf", "quay") ) {
      kind = kind = sourceFeature.getString("landuse");
      // feature_min_zoom = 16;
      theme_min_zoom = 15;  // MAX_ZOOM
      theme_max_zoom = 15;
    }
    else if( sourceFeature.hasTag("man_made", "quay") ) {
      kind = "quay";
      // feature_min_zoom = 16;
      theme_min_zoom = 15;  // MAX_ZOOM
      theme_max_zoom = 15;
    }

    // Low Emissions Zones
    else if( sourceFeature.hasTag("boundary", "low_emission_zone") ||
            // NOTE: this seems to be an outdated tag, but still used on 15 objects
            // at the time of writing, including the London "Ultra Low Emission Zone"
            // -- although this won't show up as it hasn't been mapped enough to form
            // a closed polygon yet.
            sourceFeature.hasTag("type", "LEZ")
    ) {
      kind = "low_emission_zone";
      // feature_min_zoom = { clamp: { min: 11, max: 16, value: { sum: [ { col: zoom }, 6.5 ] } } };
      theme_min_zoom = 11;
      theme_max_zoom = 15;
    }
    // END huge ordered if-else logic

    if (kind != "") {
      // try first for polygon representations
      if (sourceFeature.canBePolygon()) {
        var poly = features.polygon(layerName)
                // Ids are only relevant at max_zoom, else they prevent merges
                //.setId(FeatureId.create(sf))
                .setAttr("kind", kind)
                // TODO set feature min_zoom instead
                .setAttr("min_zoom", theme_min_zoom)
                .setAttrWithMinzoom("name", sourceFeature.getString("name"), 14)
                .setAttrWithMinzoom("protect_class", sourceFeature.getString("protect_class"), 13)
                .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 13)
                .setAttrWithMinzoom("sport", sourceFeature.getString("sport"), 13)
                // TODO (v2) this could use allowlist sanity check (see config above)
                .setAttrWithMinzoom("religion", sourceFeature.getString("religion"), 14)
                // TODO (nvkelso 2023-04-03)
                // grass runways need this
                .setAttr("surface", sourceFeature.getString("surface"))
                .setAttrWithMinzoom("natural", sourceFeature.getString("natural"), 14)
                .setAttrWithMinzoom("attraction", sourceFeature.getString("attraction"), 14)
                .setAttrWithMinzoom("zoo", sourceFeature.getString("zoo"), 14)
                .setAttrWithMinzoom("barrier", sourceFeature.getString("barrier"), 14)
                .setAttrWithMinzoom("fence_type", sourceFeature.getString("fence_type"), 14)
                // TODO set feature min_zoom instead but floar'd as int
                .setZoomRange(theme_min_zoom, theme_max_zoom)
                .setAttr("source", "openstreetmap.org")
                .setMinPixelSize(3.0);

                // TODO
                // export "tier" only from 14+
                // export "area" (v2 drop this?)

        if( kind_detail != "") {
          poly.setAttrWithMinzoom("kind_detail", kind_detail, 14);
        }

        if( denomination != "") {
          poly.setAttrWithMinzoom("denomination", denomination, 14);
        }

        // TODO (nvkelso 2023-03-21)
        // What is a ghostFeature?!
        if (ghostFeatures) {
          poly.setAttr("isGhostFeature", true);
        }

        // TODO (nvkelso 2023-03-21)
        //      Why does this need to happen?
        poly.setAttr("area", "");

        // Polygons shouldn't have names until final zooms
        OsmNames.setOsmNames(poly, sourceFeature, 14);

      } else if (sourceFeature.canBeLine()) {
        var line = features.line(layerName)
                // Ids are only relevant at max_zoom, else they prevent merges
                //.setId(FeatureId.create(sf))
                .setAttr("kind", kind)
                // TODO set feature min_zoom instead
                .setAttr("min_zoom", theme_min_zoom)
                .setAttrWithMinzoom("name", sourceFeature.getString("name"), 14)
                .setAttr("protect_class", sourceFeature.getString("protect_class"))
                .setAttr("operator", sourceFeature.getString("operator"))
                .setAttrWithMinzoom("sport", sourceFeature.getString("sport"), 13)
                // TODO (v2) this could use allowlist sanity check (see config above)
                .setAttrWithMinzoom("religion", sourceFeature.getString("religion"), 14)
                .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                .setAttrWithMinzoom("natural", sourceFeature.getString("natural"), 14)
                .setAttrWithMinzoom("attraction", sourceFeature.getString("attraction"), 14)
                .setAttrWithMinzoom("zoo", sourceFeature.getString("zoo"), 14)
                .setAttrWithMinzoom("barrier", sourceFeature.getString("barrier"), 14)
                .setAttrWithMinzoom("fence_type", sourceFeature.getString("fence_type"), 14)
                // TODO set feature min_zoom instead but floar'd as int
                .setZoomRange(theme_min_zoom, theme_max_zoom)
                .setAttr("source", "openstreetmap.org")
                .setMinPixelSize(0)
                .setPixelTolerance(0);

        if( kind_detail != "") {
          line.setAttrWithMinzoom("kind_detail", kind_detail, 14);
        }
        // lines shouldn't have names until final zooms
        OsmNames.setOsmNames(line, sourceFeature, 14);
      }
    }
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    processFeature(sourceFeature, features, NAME, false);
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

    List<VectorTile.Feature> processedFeatures = new ArrayList<>();
    List<VectorTile.Feature> linesToMerge = new ArrayList<>();
    List<VectorTile.Feature> polygonsToMerge = new ArrayList<>();

    for (var item : items) {
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
