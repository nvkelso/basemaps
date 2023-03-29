package com.protomaps.basemap.layers;

import static com.onthegomap.planetiler.util.Parse.parseDoubleOrNull;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class Buildings implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "buildings";
  }

  // TODO (nvkelso 2023-03-21)
  // 1.  detect_osm_relation
  // 2.  parse_layer_as_float
  // 3.  building_height
  // 4.  building_min_height
  // 5.  synthesize_volume
  // 6.  normalize_tourism_kind
  // 7.  building_trim_properties
  // 8.  remove_feature_id
  // 9.  truncate_min_zoom_to_1dp
  // 10. spreadsheets/sort_rank/buildings.csv
  // 11. perform vectordatasource.transform.overlap against landuse, sorting by sort_rank
  // 12. perform vectordatasource.transform.handle_label_placement
  // 13. perform vectordatasource.transform.drop_properties to drop intermediate mz_label_placement property
  // 14. perform vectordatasource.transform.remove_duplicate_features when name, kind are same
  // 15. perform vectordatasource.transform.generate_address_points
  // 16. perform vectordatasource.transform.remove_duplicate_features when name is same (eg for addresses from various sources)
  // 17. perform vectordatasource.transform.update_parenthetical_properties for closed and historical
  // 18. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 19. vectordatasource.transform.backfill_from_other_layer from pois layer get kind
  // 20. vectordatasource.transform.buildings_unify from zoom 14
  // 21. vectordatasource.transform.add_collision_rank

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    if (sf.canBePolygon() && (sf.hasTag("building") || sf.hasTag("building:part"))) {
      // TODO nvkelso (20230326)
      //      Height should be quantized, by zoom except for MAX_ZOOM
      Double height = parseDoubleOrNull(sf.getString("height"));
      // Planetiler zoom 12: quantize_height_round_nearest_20_meters
      // Planetiler zoom 13: quantize_height_round_nearest_10_meters
      // Planetiler zoom 14: quantize_height_round_nearest_10_meters

      // TODO nvkelso (20230326)
      //      Surely there's a better Planetiler way of doing this area calculation
      Double area = 0.0;
      Double volume = 0.0;
      try { area = sf.area(); } catch(GeometryException e) {  System.out.println(e); }
      if( height != null ) { volume = height * area; }

      if (sf.hasTag("building") && !sf.hasTag("building", "no")) {
        var feature = features.polygon(this.name())
                .setId(FeatureId.create(sf))
                .setAttrWithMinzoom("kind", "building", 14)
                // TODO nvkelso (20230326)
                //      kind_detail should be an allowlist set of values
                .setAttrWithMinzoom("kind_detail", sf.getString("building"), 14)
                // TODO nvkelso (20230326)
                //      Names should only be set at max_zoom – really 16 but we'll say 15 here
                .setAttrWithMinzoom("name", sf.getString("name"), 15)
                .setAttrWithMinzoom("layer", sf.getString("layer"), 12)
                .setAttrWithMinzoom("height", height, 12)
                .setAttrWithMinzoom("building_levels", sf.getString("building:levels"), 12)
                .setAttrWithMinzoom("building_min_levels", sf.getString("building:min_levels"), 12)
                .setAttrWithMinzoom("min_height", sf.getString("min_height"), 12)
                .setAttrWithMinzoom("location", sf.getString("location"), 12)
                // TODO nvkelso (20230326)
                //      These roof props should only be set at max_zoom – really 16 but we'll say 15 here
                .setAttrWithMinzoom("building_material", sf.getString("building:material"), 15)
                .setAttrWithMinzoom("roof_color", sf.getString("roof:color"), 15)
                .setAttrWithMinzoom("roof_material", sf.getString("roof:material"), 15)
                .setAttrWithMinzoom("roof_shape", sf.getString("roof:shape"), 15)
                .setAttrWithMinzoom("roof_height", sf.getString("roof:height"), 15)
                .setAttrWithMinzoom("roof_orientation", sf.getString("roof:orientation"), 15)
                //
                // TODO nvkelso (20230326)
                //      These addr props should only be set at max_zoom – really 17 but we'll say 16 here
                //      Effectively these won't be emited in tiles
                .setAttrWithMinzoom("addr_housenumber", sf.getString("addr:housenumber"), 16)
                .setAttrWithMinzoom("addr_street", sf.getString("addr:street"), 16)
                //
                .setAttr("source", "openstreetmap.org")
                .setBufferPixels(8);

        // Ensure this minzoom config matches the names config above
        OsmNames.setOsmNames(feature, sf, 15);

        // TODO nvkelso (20230326)
        //      This is more complicated, and should include scale_rank calculation and then 2x pair of
        //      filters on scale_rank and then also including height (with area and volume)

        // z13_area_volume
        if ((area >= 5000 || volume >= 150000) && !sf.hasTag("location", "underground")) {
          feature.setZoomRange(12, 15)
            .setAttr("min_zoom", 13);
        } else

          // z14_area_volume
          if (area >= 500 || volume >= 50000) {
            feature.setZoomRange(13, 15)
              .setAttrWithMinzoom("min_zoom", 14, 13);
          } else

            // z15_area_volume
            if (area >= 50 || volume >= 20000) {
              feature.setZoomRange(14, 15)
                .setAttrWithMinzoom("min_zoom", 15, 14);
            } else

              // z16_area_volume
              if (area >= 30 || volume >= 8000) {
                feature.setZoomRange(15, 15)
                  .setAttrWithMinzoom("min_zoom", 16, 15);
              }
//              else {
//                // TODO nvkelso (20230326)
//                //      really don't show them unless they are set explicately below
//                feature.setZoomRange(20, 20);
//              }
      } else if (sf.hasTag("building:part") && !sf.hasTag("building:part", "no")) {
        var feature = features.polygon(this.name())
                .setId(FeatureId.create(sf))
                .setAttrWithMinzoom("kind", "building_part", 14)
                // TODO nvkelso (20230326)
                //      This should be an allowlist set of values
                .setAttrWithMinzoom("kind_detail", sf.getString("building:part"), 14)
                // nvkelso (20230326)
                // building_part is technically part of Tilezen v1.9, but that's in error so omitting it here
                //.setAttrWithMinzoom("building_part", sf.getString("building:part"), 14)
                .setAttrWithMinzoom("building_levels", sf.getString("building:levels"), 14)
                .setAttrWithMinzoom("building_min_levels", sf.getString("building:min_levels"), 14)
                .setAttrWithMinzoom("building_material", sf.getString("building:material"), 14)
                .setAttrWithMinzoom("min_height", sf.getString("min_height"), 14)
                .setAttrWithMinzoom("location", sf.getString("location"), 14)
                // TODO nvkelso (20230326)
                //      These roof props should only be set at max_zoom – really 16 but we'll say 15 here
                .setAttrWithMinzoom("building_material", sf.getString("building:material"), 15)
                .setAttrWithMinzoom("roof_color", sf.getString("roof:color"), 15)
                .setAttrWithMinzoom("roof_material", sf.getString("roof:material"), 15)
                .setAttrWithMinzoom("roof_shape", sf.getString("roof:shape"), 15)
                .setAttrWithMinzoom("roof_height", sf.getString("roof:height"), 15)
                .setAttrWithMinzoom("roof_orientation", sf.getString("roof:orientation"), 15)
                .setAttrWithMinzoom("addr_housenumber", sf.getString("addr:housenumber"), 15)
                .setAttrWithMinzoom("addr_street", sf.getString("addr:street"), 15)
                //
                .setAttr("source", "openstreetmap.org")
                // TODO nvkelso (20230326)
                //      really don't show them unless they are set explicately below
                .setZoomRange(20, 20)
                .setBufferPixels(8);

        // TODO nvkelso (20230326)
        //      Do we really need OSM names on building parts? Probably not
        // Ensure this minzoom config matches the names config above
        // OsmNames.setOsmNames(feature, sf, 15);

        // z15_area_volume
        if (area >= 50 || volume >= 20000) {
          feature.setZoomRange(14, 15)
                  .setAttrWithMinzoom("min_zoom", 15, 14);
        } else

          // z16_area_volume
          if (area >= 30 || volume >= 8000) {
            feature.setZoomRange(15, 15)
                    .setAttrWithMinzoom("min_zoom", 16, 15);
          } else

          // z17+
          {
            feature.setZoomRange(15, 15)
                    .setAttrWithMinzoom("min_zoom", 17, 15);
          }
      }
    } else

    if (sf.hasTag("addr:housenumber") && sf.isPoint() ) {
      var feature = features.point(this.name())
              .setId(FeatureId.create(sf))
              // TODO nvkelso (20230326)
              //      These roof props should only be set at max_zoom – really 16 but we'll say 15 here
              .setZoomRange(15, 15)
              .setAttrWithMinzoom("kind", "address", 17)
              .setAttrWithMinzoom("addr_housenumber", sf.getString("addr:housenumber"), 15)
              .setAttrWithMinzoom("addr_street", sf.getString("addr:street"), 15)
              .setAttr("source", "openstreetmap.org")
              .setBufferPixels(128);

      // TODO nvkelso (20230326)
      //      Do we really need OSM names on addresses? Probably not
      // Ensure this minzoom config matches the names config above
      // OsmNames.setOsmNames(feature, sf, 15);

    } else if ( sf.hasTag("entrance") && sf.isPoint() ) {
        var feature = features.point(this.name())
              .setId(FeatureId.create(sf))
              // TODO nvkelso (20230326)
              //      Should instead be MAX_ZOOM
              .setAttrWithMinzoom("name", sf.getString("name"), 15)
                // TODO nvkelso (20230326)
                //      Should instead be MAX_ZOOM
              .setZoomRange(15, 15)
              .setAttr("source", "openstreetmap.org")
              .setBufferPixels(128);

        var entrance = sf.getString("entrance");

        switch ( entrance ) {
          case "main":
          case "staircase":
          case "service":
          case "home":
          case "unisex":
          case "garage":
          case "residence":
          case "private":
            feature.setAttr("min_zoom", 17)
                    .setAttr("kind", "entrance")
                    .setAttr("kind_detail", entrance);
            break;
          case "main_entrance":
            feature.setAttr("min_zoom", 17)
                    .setAttr("kind", "entrance")
                    // Clean up weird tagging choices
                    .setAttr("kind_detail", "main");
            break;
          case "secondary_entrance":
            feature.setAttr("min_zoom", 17)
                    .setAttr("kind", "entrance")
                    // Clean up weird tagging choices
                    .setAttr("kind_detail", "secondary");
            break;
          // map detailed kinds of exits to kind=exit with kind_detail
          case "emergency":
          case "fire_exit":
            feature.setAttr("min_zoom", 17)
                    .setAttr("kind", "exit")
                    .setAttr("kind_detail", entrance);
            break;
          // exits without detail
          case "exit":
            feature.setAttr("min_zoom", 17)
                    .setAttr("kind", "exit");
            break;
          // entrances without detail
          default:
            feature.setAttr("min_zoom", 17)
                    .setAttr("kind", "entrance");
            break;
        }
    }
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    // Don't merge polygons at MAX_ZOOM
    if (zoom >= 14)
      return items;

    // TODO nvkelso (20230326)
    //      This should be a series of drop_small_inners, then merge, drop_small_inners again, then merge again
    //      During those steps, more properties are dropped per zoom (do that above isntead), and some props like
    //      scalerank and min_zoom and per tile inclusion are re-calcualted.
    //      And it shouldn't be "nearby", instead it should be any matching attr buildings in entire tile
    return FeatureMerge.mergeNearbyPolygons(items, 1, 1, 0.5, 0.5);
  }
}
