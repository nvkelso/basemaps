package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.*;

public class Roads implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "roads";
  }

  // TODO (nvkelso 2023-03-21)
  // 1.  parse_layer_as_float
  // 2.  road_classifier
  // 3.  road_oneway
  // 4.  road_abbreviate_name
  // 5.  route_name
  // 6.  normalize_aerialways
  // 7.  normalize_cycleway
  // 8.  add_is_bicycle_related
  // 9.  add_road_network_from_ncat
  // 10. add_vehicle_restrictions
  // 11. road_trim_properties
  // 12. spreadsheets/sort_rank/roads.csv
  // 13. drop layer property (after sort_rank is applied)
  // 14. perform vectordatasource.transform.overlap against admin_areas to get iso_code
  // 15. perform vectordatasource.transform.road_networks
  // 16. to save file size, then drop the iso_code (country_code)
  // 17. perform vectordatasource.transform.point_in_country_logic to get drivves_on_left attr for mini_roundabout
  // 18. perform vectordatasource.transform.drop_properties to not show is_bridge, is_tunnel until zoom 13+ but with details per kind
  // 19. perform vectordatasource.transform.drop_properties to not show various properties until zoom 11 (bicycle, colour, type, etc)
  // 20. perform vectordatasource.transform.drop_properties to not show various properties until zoom 14 ("all" network and shield tags)
  // 21. perform vectordatasource.transform.drop_properties to not show various properties until zoom 13 (ascent, crossing, etc tags)
  // 22. perform vectordatasource.transform.drop_properties to not show various properties until zoom 11 (name, eft tags) on paths
  // 23. perform vectordatasource.transform.drop_properties to not show various properties until zoom 13 (bus_network & etc tags) on highway, major_road_minor_road per zooms
  // 24. perform vectordatasource.transform.drop_properties to not show various properties until zoom 13 (bicycle_shield_text & etc tags) on highway, major_road_minor_road per zooms
  // 25. perform vectordatasource.transform.drop_properties to not show various properties until zoom 13 (bus_network & etc tags) on highway, major_road_minor_road per zooms (assumes is_bus route boolean!)
  // 26. perform vectordatasource.transform.drop_properties to not show various properties until zoom 13 (network, shield_text & etc tags) on original OSM tags / kind / kind_detail per zooms
  // 27. perform vectordatasource.transform.drop_properties to not show various properties until zoom 15 (ref, osm_relation etc tags)
  // 28. perform vectordatasource.transform.drop_properties to not show various properties until zoom 13 (name, all_*, service, access) per kind, kind_detail
  // 29. perform vectordatasource.transform.drop_properties to not show various properties until zoom 13 (surface, cycleway & etc) per kind, kind_detail
  // 30. perform vectordatasource.transform.drop_properties to not show various properties until zoom 14 (colour, operator & etc) per minor_road
  // 31. perform vectordatasource.transform.whitelist to simplify surface values on various kinds of roads at zooms 13 and earlier to encourage more line merging (nit: allowlist not whitelist)
  // 32. perform vectordatasource.transform.drop_properties to not show various properties until zoom 12 (surface & etc) per kind of road and zoom
  // 33. perform vectordatasource.transform.drop_properties to not show various properties until zoom 12 (cutting & etc)
  // 34. perform vectordatasource.transform.whitelist to simplify access values at zooms 12 and earlier to encourage more line merging (nit: allowlist not whitelist)
  // 35. perform vectordatasource.transform.whitelist to simplify motor_vehicle values at zooms 12 and earlier to encourage more line merging (nit: allowlist not whitelist)
  // 36. perform vectordatasource.transform.drop_properties to not show various properties until zoom 10 (names, all_network & etc) on major roads
  // 37. perform vectordatasource.transform.drop_names until zoom 10 on highway
  // 38. perform vectordatasource.transform.drop_properties for almost all properties from NE at low zooms on highway kind
  // 39. perform vectordatasource.transform.drop_properties for various properties (all_*, bus*) zoom 10 and earlier on highway kind
  // 40. perform vectordatasource.transform.drop_properties for various properties (all_networks, bus*) zoom 8 and earlier on highway kind
  // 41. perform vectordatasource.transform.drop_properties for various properties (all_*, bus*) zoom 10 and earlier on major_road kind
  // 42. perform vectordatasource.transform.drop_properties for various properties (all_networks, bus*) zoom 12 and earlier on highway, major_road kind (seems duplicate)
  // 43. perform vectordatasource.transform.drop_properties for low zooms to remove walking_* properties
  // 43. perform vectordatasource.transform.drop_properties until zoom 11 to remove bicycle* properties
  // 44. perform vectordatasource.transform.drop_properties until zoom 12 to remove bicycle* properties (partial overlap with above)
  // 45. perform vectordatasource.transform.drop_properties until zoom 13 to remove bicycle* properties (just for lcn)
  // 46. perform vectordatasource.transform.drop_properties until zoom 13 to remove bicycle* properties (just for lcn, partial overlap with above)
  // 47. perform vectordatasource.transform.drop_properties until zoom 11 to remove bicycle* properties (just for path and tracks)
  // 48. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 49. vectordatasource.transform.overlap to merge aerodrome kind_detail onto runway polygons
  // 50. vectordatasource.transform.intercut with landuse > landuse_kind, sort by sort_rank
  // 51. vectordatasource.transform.drop_properties conditionally drop landuse_kind at mid-low zooms, because they prevent merging
  // 52. vectordatasource.transform.drop_properties conditionally drop landuse_kind at mid-low zooms, because they prevent merging, per kind_detail and kind and zoom
  // 53. vectordatasource.transform.merge_line_features for all zooms but max with params
  // 54. vectordatasource.transform.drop_names_on_short_boundaries for most zooms with pixels_per_letter (mid-zooms)
  // 55. vectordatasource.transform.drop_names_on_short_boundaries for most zooms with pixels_per_letter (high-zooms)
  // 56. vectordatasource.transform.merge_line_features for most zooms
  // 57. vectordatasource.transform.merge_line_features at max_zoom
  // 58. vectordatasource.transform.add_collision_rank

  public void processNe(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    var kind = "";
    var kind_detail = "";
    var theme_min_zoom = 0;
    var theme_max_zoom = 0;
    // TODO (nvkelso 2023-03-25)
    //      These should all be per feature instead of layer... but per feature per layer
    if (sourceLayer.equals("ne_10m_roads")) {
      theme_min_zoom = 4;
      theme_max_zoom = 6;
      kind = "tz_likely_road";
    }

    if( kind != "") {
      switch (sf.getString("featurecla")) {
        case "Ferry":
          kind = "ferry";
          break;
        case "Road":
          if (sf.getString("expressway") == "1") {
            kind = "highway";
            kind_detail = "motorway";
            break;
          }
          switch (sf.getString("type")) {
            case "Major Highway":
            case "Beltway":
            case "Bypass":
              kind = "major_road";
              kind_detail = "trunk";
              break;
            case "Secondary Highway":
              kind = "major_road";
              kind_detail = "primary";
              break;
            case "Road":
              kind = "major_road";
              kind_detail = "secondary";
              break;
            case "Track":
            case "Unknown":
              kind = "minor_road";
              kind_detail = "tertiary";
              break;
            default:
              kind = "";
              break;
          }
      }
    }

    if (sf.canBeLine() && sf.hasTag("min_zoom") && kind != "" ) {
      features.line(this.name())
        // TODO (nvkelso 2023-03-25)
        //      This should be a single decimal precision float not string
//        case:
//          - when:
//            min_zoom: 3.0
//          then: 5.0
//          - when:
//            min_zoom: 4.0
//          then: 5.1
//          - when:
//            min_zoom: 5.0
//            then: 5.2
//          - else:
//            clamp: { min: 6, max: 17, value: { col: min_zoom } }
        .setAttr("min_zoom", sf.getLong("min_zoom"))
        //      This should be a single decimal precision float not string
        //      See below section, too
        .setMinPixelSize(0)
        .setPixelTolerance(0)
        .setZoomRange(sf.getString("min_zoom") == null ? theme_min_zoom : (int)Double.parseDouble(sf.getString("min_zoom")), theme_max_zoom)
        .setAttr("kind", kind)
        .setAttr("kind_detail", kind_detail)
        .setAttr("shield_text", sf.getString("name"))
//            mz_networks:
//            case:
//            - when:
//            sov_a3: CAN
//            level: ['Federal', 'Interstate', 'State']
//            then: ['road', 'CA:??:primary', { col: name }]
//            - when:
//            sov_a3: 'MEX'
//            level: 'Interstate'
//            then: ['road', 'MX', { col: name }]
//            - when:
//            sov_a3: 'MEX'
//            level: 'Federal'
//            then: ['road', 'MX:MX', { col: name }]
//            - when:
//            sov_a3: 'USA'
//            level: 'Interstate'
//            then: ['road', 'US:I', { col: name }]
//            - when:
//            sov_a3: 'USA'
//            level: 'Federal'
//            then: ['road', 'US:US', { col: name }]
//            - when:
//            continent: 'Oceania'
//            level: 'Federal'
//            then: ['road', 'NZ:SH', { col: label}]
//            - when:
//            continent: ['Europe', 'Asia']
//            level: 'E'
//            then: ['road', 'e-road', { col: name }]
//            columns: [ sov_a3, continent, label, level ]
      // TODO (nvkelso 2023-03-26)
      //      Natural Earth network values depend on sov_a3 and level and are
      //      allowlisted for just 7 situations to cover CA, MX, US, NZ, and Europe (e-road)
      //.setAttr("network", sf.getString("level"))
      // TODO (nvkelso 2023-03-26)
        //      This should be a boolean whtn toll: 1
        .setAttr("toll", sf.getString("toll"))
        // TODO (nvkelso 2023-03-25)
        // 	{% if viewpoints %}
        // 	  {%- for vpt in ne_viewpoints %}
        // 		'fclass_{{vpt}}', fclass_{{vpt}},
        // 	  {%- endfor %}
        // 	{% endif %}
        .setAttr("source", "naturalearthdata.com")
        .setBufferPixels(8);
    }
  }

    // 'source', 'naturalearthdata.com'
// 	jsonb_build_object(
// 		  'min_zoom', mz_road_min_zoom,
// 		  'scalerank', scalerank,
// 		  'featurecla', featurecla,
// 		  'type', type,
// 		  'expressway', expressway,
// 		  'toll', toll,
// 		  'sov_a3', sov_a3,
// 		  'level', level,
// 		  'continent', continent,
// 		  'label', label,
// 		  'name', name
// 	  ) AS __roads_properties__,
//
// 	  NULL::jsonb AS __water_properties__
//
// 	FROM
//
// 	  ne_10m_roads
//
// 	WHERE
//
// 	  {{ bounds['line']|bbox_filter('the_geom', 3857) }}
// 	  AND mz_road_min_zoom < {{ zoom + 1 }}


  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {

    // some features (like piers and dams) can be complex geom types (both line and polygons)
    // and let's exclude points entirely
    if (sourceFeature.canBeLine() && !sourceFeature.canBePolygon() ) {
      // basic highway features
     if( sourceFeature.hasTag("highway") &&
        !(sourceFeature.hasTag("highway", "proposed", "construction"))) {

       String highway = sourceFeature.getString("highway");
       String shield_text = sourceFeature.getString("ref");
       Integer shield_text_length = (shield_text == null ? null : shield_text.length());

       var feat = features.line(this.name())
               // This inhibits feature merging, and should only be exported at max_zoom
               //.setId(FeatureId.create(sourceFeature))
               .setMinPixelSize(0)
               .setPixelTolerance(0)
               // TODO (nvkelso 2023-03-26)
               //      This should be expressed as a boolean ("is_bridge")
               .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
               // TODO (nvkelso 2023-03-26)
               //      This should be expressed as a boolean ("is_tunnel")
               .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
               // TODO (nvkelso 2023-03-26)
               //      This should be sanity checked for ±6 int values
               .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
               .setAttrWithMinzoom("ref", sourceFeature.getString("ref"), 15)
               // TODO (nvkelso 2023-03-26)
               //      This should be sanity checked for cutting: ['yes', right, left]
               //      if cutting: both then set to 'yes' anyhow
               .setAttrWithMinzoom("cutting", sourceFeature.getString("cutting"), 14)
               // TODO (nvkelso 2023-03-26)
               //      This should be sanity checked for embankment: ['yes', right, left]
               //      if embankment: [both, two_sided] then set to 'yes' anyhow
               .setAttrWithMinzoom("embankment", sourceFeature.getString("embankment"), 14)
               // These are all to MAX_ZOOM
               .setAttrWithMinzoom("ascent", sourceFeature.getString("embankment"), 15)
               // TODO (nvkelso 2023-03-26)
               // This should sanity checked to exclude crossing: 'no' (but pass thru others)
               .setAttrWithMinzoom("crossing", sourceFeature.getString("crossing"), 15)
               .setAttrWithMinzoom("descent", sourceFeature.getString("descent"), 15)
               .setAttrWithMinzoom("description", sourceFeature.getString("description"), 15)
               .setAttrWithMinzoom("distance", sourceFeature.getString("distance"), 15)
               .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
               .setAttrWithMinzoom("oneway_bicycle", sourceFeature.getString("oneway_bicycle"), 15)
               .setAttrWithMinzoom("roundtrip", sourceFeature.getString("roundtrip"), 15)
               // TODO (nvkelso 2023-03-26)
               // This should be a boolean when segregated: 'yes'
               .setAttrWithMinzoom("segregated", sourceFeature.getString("segregated"), 15)
               .setAttrWithMinzoom("sidewalk", sourceFeature.getString("sidewalk"), 15)
               .setAttrWithMinzoom("sidewalk_left", sourceFeature.getString("sidewalk:left"), 15)
               .setAttrWithMinzoom("sidewalk_right", sourceFeature.getString("sidewalk:right"), 15)
               .setAttrWithMinzoom("sport", sourceFeature.getString("sport"), 15)
               //   "Footway" properties
               .setAttrWithMinzoom("incline", sourceFeature.getString("incline"), 15)
               .setAttrWithMinzoom("ramp", sourceFeature.getString("ramp"), 15)
               .setAttrWithMinzoom("ramp_bicycle", sourceFeature.getString("ramp_bicycle"), 15)
               .setAttrWithMinzoom("trail_visibility", sourceFeature.getString("trail_visibility"), 15)
               //   END "Footway" properties
               // End MAX_ZOOM section
               // These all start later to encourage line merging at low and mid-zooms
               //   "Footway" properties
               .setAttrWithMinzoom("foot", sourceFeature.getString("foot"), 12)
               .setAttrWithMinzoom("horse", sourceFeature.getString("horse"), 12)
               .setAttrWithMinzoom("tracktype", sourceFeature.getString("tracktype"), 12)
               .setAttrWithMinzoom("sac_scale", sourceFeature.getString("sac_scale"), 12)
               //   END "Footway" properties
               // TODO (nvkelso 2023-03-26)
               // There's more to this one, and it turns into is_bicycle_related even at low zooms
               .setAttrWithMinzoom("bicycle", sourceFeature.getString("bicycle"), 12)
               .setAttrWithMinzoom("motor_vehicle", sourceFeature.getString("motor_vehicle"), 12)
               // END zoom 12 start
               // These "max" props didn't have a zoom range, but probably should
               // This makes it less easiy to use the tiles in a trucking style, though (it's a thing)
               // If you wanted them earlier, then set just for highway and major_road kinds with variable zooms?
               .setAttrWithMinzoom("maxweight", sourceFeature.getString("maxweight"), 12)
               .setAttrWithMinzoom("maxheight", sourceFeature.getString("maxheight"), 12)
               .setAttrWithMinzoom("maxwidth", sourceFeature.getString("maxwidth"), 12)
               .setAttrWithMinzoom("maxlength", sourceFeature.getString("maxlength"), 12)
               .setAttrWithMinzoom("maxaxleload", sourceFeature.getString("maxaxleload"), 12)
               .setAttrWithMinzoom("hazmat", sourceFeature.getString("hazmat"), 12)
               // END max props
               // TODO (nvkelso 2023-03-26)
               // This should be a boolean if any value, when toll is not 'no'
               .setAttr("toll", sourceFeature.getString("toll"))
               // TODO (nvkelso 2023-03-26)
               // This should be a boolean if any value, when "hvg:toll" is not 'no'
               .setAttr("hvg_toll", sourceFeature.getString("hvg:toll"))
               .setAttr("source", "openstreetmap.org")
               // nvkelso (20230321)
               // TODO    I just don't want them showing up early
               .setZoomRange(16, 16);

       // TODO (nvkelso 2023-03-26)
       //      bicycle_network: {call: { func: mz_cycling_network, args: [tags, osm_id] }}

       switch (highway) {
         case "motorway":
           feat.setAttr("min_zoom", 5)
                   // OSM transition from OSM at zoom 8
                   .setZoomRange(7, 15)
                   .setAttr("kind", "highway")
                   .setAttr("kind_detail", "motorway")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 7)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 7)
                   // TODO (nvkelso 2023-03-28)
                   //      Fetch network from the relation instead of the way as it's more normalized and consistent
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 7)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 11)
                   // TODO (nvkelso 2023-03-26)
                   //      Make sure values are always _ not : whitespaced
                   //    - { when: { surface: 'concrete:plates' }, then: 'concrete_plates' }
                   //   - { when: { surface: 'concrete:lanes' }, then: 'concrete_lanes' }
                   //   - { when: { surface: 'cobblestone:flattened' }, then: 'cobblestone_flattened' }
                   //   - else: { col: 'surface' }
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 11)
                   // TODO (nvkelso 2023-03-26)
                   //      Ensure values are not in ['no', 'none']
                   //      Ensure "cycleway:both" gets mapped here (it's an odd case)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 11)
                   // TODO (nvkelso 2023-03-26)
                   //      Ensure values are not in ['no', 'none']
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 11)
                   // TODO (nvkelso 2023-03-26)
                   //      Ensure values are not in ['no', 'none']
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 11)
                   // TODO (nvkelso 2023-03-26)
                   //           - when: {hgv: [agricultural, delivery, designated, destination, local, 'no']}
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 11)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 10);
           OsmNames.setOsmNames(feat, sourceFeature, 11);
           break;
         case "trunk":
           feat.setAttr("min_zoom", 6)
                   // OSM transition from OSM at zoom 8
                   .setZoomRange(7, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "trunk")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 8)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 8)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 8)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 12)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 11);
           OsmNames.setOsmNames(feat, sourceFeature, 11);
           break;
         case "primary":
           feat.setAttr("min_zoom", 8)
                   // OSM transition from OSM at zoom 8
                   .setZoomRange(7, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "primary")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 10)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 10)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 10)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 12)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 12);
           OsmNames.setOsmNames(feat, sourceFeature, 12);
           break;
         case "secondary":
           //min_zoom = { clamp: { min: 0, max: 10, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 10)
                   .setZoomRange(9, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "secondary")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 11)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 11)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 11)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
           OsmNames.setOsmNames(feat, sourceFeature, 13);
           break;
         case "tertiary":
           //min_zoom = { clamp: { min: 0, max: 11, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 11)
                   .setZoomRange(10, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "tertiary")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 12)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 12)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 12)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
           OsmNames.setOsmNames(feat, sourceFeature, 13);
           break;
         case "motorway_link":
           //min_zoom = { clamp: { min: 0, max: 11, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 11)
                   .setZoomRange(10, 15)
                   .setAttr("kind", "highway")
                   .setAttr("kind_detail", "motorway_link")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 12)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 12)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 12)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
           OsmNames.setOsmNames(feat, sourceFeature, 13);
           break;
         case "trunk_link":
           //min_zoom = { clamp: { min: 0, max: 12, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 12)
                   .setZoomRange(11, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "trunk_link")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 12)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 12)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 12)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
           OsmNames.setOsmNames(feat, sourceFeature, 13);
           break;
         case "primary_link":
           //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 13)
                   .setZoomRange(12, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "primary_link")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
           OsmNames.setOsmNames(feat, sourceFeature, 14);
           break;
         case "secondary_link":
           //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 13)
                   .setZoomRange(12, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "secondary_link")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
           OsmNames.setOsmNames(feat, sourceFeature, 14);
           break;
         case "tertiary_link":
           //min_zoom = { clamp: { min: 0, max: 14, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 14)
                   .setZoomRange(13, 15)
                   .setAttr("kind", "major_road")
                   .setAttr("kind_detail", "tertiary_link")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 12)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 12)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 12)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 12)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 12)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
           OsmNames.setOsmNames(feat, sourceFeature, 14);
           break;
         case "unclassified":
           if (sourceFeature.hasTag("whitewater", "portage_way")) {
             //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } })
             feat.setAttr("min_zoom", 13)
                     .setZoomRange(12, 15)
                     .setAttr("kind", "portage_way")
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                     .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 14)
                     .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                     .setAttrWithMinzoom("route", sourceFeature.getString("route"), 14)
                     .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 14)
                     .setAttrWithMinzoom("state", sourceFeature.getString("state"), 14)
                     .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 14)
                     .setAttrWithMinzoom("type", sourceFeature.getString("type"), 14)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
             OsmNames.setOsmNames(feat, sourceFeature, 14);
           } else {
             //min_zoom = { clamp: { min: 0, max: 12, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
             feat.setAttr("min_zoom", 12)
                     .setZoomRange(11, 15)
                     .setAttr("kind", "minor_road")
                     .setAttr("kind_detail", "unclassified")
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                     .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                     .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                     .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                     .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                     .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                     .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                     .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
             OsmNames.setOsmNames(feat, sourceFeature, 14);
           }
           break;
         case "residential":
           //min_zoom = { clamp: { min: 0, max: 12, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 12)
                   .setZoomRange(11, 15)
                   .setAttr("kind", "minor_road")
                   .setAttr("kind_detail", "residential")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
           OsmNames.setOsmNames(feat, sourceFeature, 14);
           break;
         case "road":
           //min_zoom = { clamp: { min: 0, max: 12, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 12)
                   .setZoomRange(11, 15)
                   .setAttr("kind", "minor_road")
                   .setAttr("kind_detail", "road")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
           OsmNames.setOsmNames(feat, sourceFeature, 14);
           break;
         case "living_street":
           //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 13)
                   .setZoomRange(12, 15)
                   .setAttr("kind", "minor_road")
                   .setAttr("kind_detail", "living_street")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
           OsmNames.setOsmNames(feat, sourceFeature, 14);
           break;
         case "pedestrian":
           //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 13)
                   .setZoomRange(12, 15)
                   .setAttr("kind", "path")
                   .setAttr("kind_detail", "pedestrian")
                   // (nvkelso) 20230325
                   // TODO: add osm_footway_properties
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
           OsmNames.setOsmNames(feat, sourceFeature, 13);
           break;
         case "track":
           if (sourceFeature.hasTag("surface", "paved", "asphalt", "concrete") ||
                   (sourceFeature.hasTag("tracktype", "grade1") &&
                           !sourceFeature.hasTag("access", "private"))) {
             // min_zoom = { clamp: { min: 0, max: 11, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
             feat.setAttr("min_zoom", 11)
                     .setZoomRange(10, 15)
                     .setAttr("kind", "path")
                     .setAttr("kind_detail", "track")
                     // (nvkelso) 20230325
                     // TODO: add osm_footway_properties
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                     .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                     .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                     .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                     .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                     .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                     .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                     .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
             OsmNames.setOsmNames(feat, sourceFeature, 13);
           } else if ((sourceFeature.hasTag("surface", "gravel") &&
                   !sourceFeature.hasTag("tracktype", "grade3", "grade4", "grade5")) ||
                   sourceFeature.hasTag("tracktype", "grade1", "grade2")) {
             // min_zoom = { clamp: { min: 0, max: 12, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
             feat.setAttr("min_zoom", 12)
                     .setZoomRange(11, 15)
                     .setAttr("kind", "path")
                     .setAttr("kind_detail", "track")
                     // (nvkelso) 20230325
                     // TODO: add osm_footway_properties
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                     .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                     .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                     .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                     .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                     .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                     .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                     .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
             OsmNames.setOsmNames(feat, sourceFeature, 13);
           } else {
             //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
             feat.setAttr("min_zoom", 13)
                     .setZoomRange(12, 15)
                     .setAttr("kind", "path")
                     .setAttr("kind_detail", "track")
                     // (nvkelso) 20230325
                     // TODO: add osm_footway_properties
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                     .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                     .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                     .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                     .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                     .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                     .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                     .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
             OsmNames.setOsmNames(feat, sourceFeature, 13);
           }
           break;
         case "path":
         case "cycleway":
         case "bridleway":
           //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
           feat.setAttr("min_zoom", 13)
                   .setZoomRange(12, 15)
                   .setAttr("kind", "path")
                   .setAttr("kind_detail", sourceFeature.getTag("highway"))
                   // (nvkelso) 20230325
                   // TODO: add osm_footway_properties
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
           OsmNames.setOsmNames(feat, sourceFeature, 13);
           break;
         case "footway":
           if (sourceFeature.hasTag("name") ||
                   sourceFeature.hasTag("bicycle", "designated") ||
                   sourceFeature.hasTag("foot", "designated") ||
                   sourceFeature.hasTag("horse", "designated") ||
                   sourceFeature.hasTag("snowmobile", "designated") ||
                   sourceFeature.hasTag("ski", "designated")) {
             //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
             feat.setAttr("min_zoom", 13)
                     .setZoomRange(12, 15)
                     .setAttr("kind", "path")
                     .setAttr("kind_detail", "footway")
                     .setAttr("footway", sourceFeature.getTag("footway"))
                     // (nvkelso) 20230325
                     // TODO: add osm_footway_properties
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                     .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                     .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                     .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                     .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                     .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                     .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                     .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
             OsmNames.setOsmNames(feat, sourceFeature, 13);
           } else {
             if (sourceFeature.getTag("footway") == "footway" ||
                     sourceFeature.getTag("footway") == "steps") {
               //min_zoom = { clamp: { min: 0, max: 14, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
               feat.setAttr("min_zoom", 14)
                       .setZoomRange(13, 15)
                       .setAttr("kind", "path")
                       .setAttr("kind_detail", "footway")
                       .setAttr("footway", sourceFeature.getTag("footway"))
                       // (nvkelso) 20230325
                       // TODO: add osm_footway_properties
                       .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                       .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                       .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                       .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                       .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                       .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                       .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                       .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                       .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                       .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                       .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                       .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                       .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                       .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                       .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                       .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                       .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
               OsmNames.setOsmNames(feat, sourceFeature, 13);
               if (sourceFeature.getTag("footway") == "sidewalk" ||
                       sourceFeature.getTag("footway") == "crossing") {
                 //min_zoom = { clamp: { min: 0, max: 15, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
                 feat.setAttr("min_zoom", 15)
                         .setZoomRange(14, 15)
                         .setAttr("kind", "path")
                         .setAttr("kind_detail", "footway")
                         .setAttr("footway", sourceFeature.getTag("footway"))
                         // (nvkelso) 20230325
                         // TODO: add osm_footway_properties
                         .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                         .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                         .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                         .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                         .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                         .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                         .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                         .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                         .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                         .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                         .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                         .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                         .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                         .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                         .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                         .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                         .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
                 OsmNames.setOsmNames(feat, sourceFeature, 13);
               }
             }
           }
           break;
         case "steps":
           if (sourceFeature.hasTag("name") ||
                   sourceFeature.hasTag("bicycle", "designated") ||
                   sourceFeature.hasTag("foot", "designated") ||
                   sourceFeature.hasTag("horse", "designated") ||
                   sourceFeature.hasTag("snowmobile", "designated") ||
                   sourceFeature.hasTag("ski", "designated")) {
             //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
             feat.setAttr("min_zoom", 13)
                     .setZoomRange(12, 15)
                     .setAttr("kind", "path")
                     .setAttr("kind_detail", "steps")
                     .setAttr("footway", sourceFeature.getTag("footway"))
                     // (nvkelso) 20230325
                     // TODO: add osm_footway_properties
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                     .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 13)
                     .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                     .setAttrWithMinzoom("route", sourceFeature.getString("route"), 13)
                     .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 13)
                     .setAttrWithMinzoom("state", sourceFeature.getString("state"), 13)
                     .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 13)
                     .setAttrWithMinzoom("type", sourceFeature.getString("type"), 13)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
             OsmNames.setOsmNames(feat, sourceFeature, 13);
           }
           break;
         case "corridor":
           feat.setAttr("min_zoom", 16)
                   .setZoomRange(15, 15)
                   .setAttr("kind", "path")
                   .setAttr("kind_detail", "corridor")
                   // (nvkelso) 20230325
                   // TODO: add osm_footway_properties
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 15)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 15)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 15)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 15)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 15)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 15)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 15);
           OsmNames.setOsmNames(feat, sourceFeature, 15);
           break;
         case "service":
           if (sourceFeature.hasTag("whitewater", "portage_way")) {
             //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } })
             feat.setAttr("min_zoom", 13)
                     .setZoomRange(12, 15)
                     .setAttr("kind", "portage_way")
                     // (nvkelso) 20230325
                     // TODO: add osm_footway_properties
                     .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 13)
                     .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                     .setAttrWithMinzoom("network", sourceFeature.getString("network"), 13)
                     .setAttrWithMinzoom("service", sourceFeature.getString("service"), 13)
                     .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 13)
                     .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 13)
                     .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 13)
                     .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 13)
                     .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 13)
                     .setAttrWithMinzoom("access", sourceFeature.getString("access"), 13);
             OsmNames.setOsmNames(feat, sourceFeature, 13);
           } else {
             if (sourceFeature.getTag("service") == "alley") {
               //min_zoom = { clamp: { min: 0, max: 13, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
               feat.setAttr("min_zoom", 13)
                       .setZoomRange(12, 15)
                       .setAttr("kind", "minor_road")
                       .setAttr("kind_detail", "service")
                       .setAttr("service", "alley")
                       .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                       .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                       .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                       .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                       .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                       .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                       .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                       .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                       .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                       .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                       .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                       .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                       .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                       .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                       .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                       .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                       .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
               OsmNames.setOsmNames(feat, sourceFeature, 14);
             } else if (sourceFeature.getTag("service") == "driveway" ||
                     sourceFeature.getTag("service") == "parking_aisle" ||
                     sourceFeature.getTag("service") == "drive-through") {
               //min_zoom = { clamp: { min: 0, max: 15, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
               feat.setAttr("min_zoom", 15)
                       .setZoomRange(14, 15)
                       .setAttr("kind", "minor_road")
                       .setAttr("kind_detail", "service")
                       .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                       .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                       .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                       .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                       .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                       .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                       .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                       .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                       .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                       .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                       .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                       .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                       .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                       .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                       .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                       .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                       .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
               OsmNames.setOsmNames(feat, sourceFeature, 14);

               if (sourceFeature.getTag("service") == "drive-through") {
                 feat.setAttr("service", "drive_through");
               } else {
                 feat.setAttr("service", sourceFeature.getTag("service"));
               }
             } else {
               //min_zoom = { clamp: { min: 0, max: 14, value: { call: { func: mz_calculate_path_major_route, args: [ { col: fid }, { col: meta.relations } ] } } } };
               feat.setAttr("min_zoom", 14)
                       .setZoomRange(13, 15)
                       .setAttr("kind", "minor_road")
                       .setAttr("kind_detail", "service")
                       .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                       .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                       .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                       .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                       .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                       .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                       .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                       .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                       .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                       .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                       .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                       .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                       .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                       .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                       .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                       .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                       .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
               OsmNames.setOsmNames(feat, sourceFeature, 14);
             }
           }
           break;
         case "raceway":
           feat.setAttr("min_zoom", 13)
                   .setZoomRange(12, 15)
                   .setAttr("kind", "minor_road")
                   .setAttr("kind_detail", "raceway")
                   .setAttrWithMinzoom("shield_text", sourceFeature.getString("ref"), 14)
                   .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                   .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                   .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                   .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                   .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                   .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                   .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                   .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                   .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                   .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                   .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                   .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                   .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                   .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                   .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                   .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
           OsmNames.setOsmNames(feat, sourceFeature, 14);
           break;
         default:
           feat.setAttr("min_zoom", 20)
                   .setAttr("kind", "tz_this_shouldn_exist")
                   .setZoomRange(20, 20);
           break;
       }
       // end set kind attr from OSM highway tags

       // (nvkelso) 20230326 with partial fix 20230404
       // TODO: this is overly simplified and should be improved
       //       it's also potentially exporting junk at low zooms (which is why I added the default in above switch)
       //       For example Tilezen doesn't do is_bridge, is_tunnel until zoom 14 for minor roads and 12 for others
       if (sourceFeature.hasTag("bridge", "yes")) {
         feat.setAttrWithMinzoom("level", 1, 12);
         feat.setAttrWithMinzoom("is_bridge", true, 12);
       } else if (sourceFeature.hasTag("tunnel", "yes")) {
         feat.setAttrWithMinzoom("level", -1, 12);
         feat.setAttrWithMinzoom("is_tunnel", true, 12);
       }
     }

  //#############################################################
  //#
  //# OSM construction
  //#
  //#############################################################
    if( sourceFeature.hasTag("highway") &&
            sourceFeature.hasTag("construction", "motorway", "motorway_link", "trunk", "primary", "secondary", "tertiary", "trunk_link", "unclassified", "residential", "road",
                    "primary_link", "secondary_link", "living_street", "service", "pedestrian", "track", "cycleway", "bridleway",
                    "tertiary_link", "footway", "steps",
                    "corridor")) {
      String shield_text = sourceFeature.getString("ref");
      Integer shield_text_length = (shield_text == null ? null : shield_text.length());

      var feat = features.line(this.name())
              // This inhibits feature merging, and should only be exported at max_zoom
              //.setId(FeatureId.create(sourceFeature))
              .setMinPixelSize(0)
              .setPixelTolerance(0)
              // TODO (nvkelso 2023-03-26)
              //      This should be expressed as a boolean ("is_bridge")
              .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
              // TODO (nvkelso 2023-03-26)
              //      This should be expressed as a boolean ("is_tunnel")
              .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
              // TODO (nvkelso 2023-03-26)
              //      This should be sanity checked for ±6 int values
              .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
              .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
              .setAttr("source", "openstreetmap.org")
              // nvkelso (20230321)
              // TODO    I just don't want them showing up early
              .setZoomRange(20, 20);

      var construction_val = sourceFeature.getString("construction");

      switch (construction_val) {
        case "motorway":
        case "motorway_link":
        case "trunk":
        case "primary":
        case "secondary":
        case "tertiary":
        case "trunk_link":
        case "unclassified":
        case "residential":
        case "road":
        case "siding":
          feat.setAttr("kind", "construction")
                  .setAttr("kind_detail", construction_val)
                  .setAttr("min_zoom", 12)
                  .setZoomRange(11, 15)
                  .setAttrWithMinzoom("shield_text", shield_text, 14)
                  .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                  .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                  .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                  .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                  .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                  .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                  .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                  .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                  .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                  .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                  .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                  .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                  .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                  .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                  .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                  .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
          OsmNames.setOsmNames(feat, sourceFeature, 13);
          break;
        case "primary_link":
        case "secondary_link":
        case "living_street":
        case "service":
        case "pedestrian":
        case "track":
        case "cycleway":
        case "bridleway":
          feat.setAttr("kind", "construction")
                  .setAttr("kind_detail", construction_val)
                  .setAttr("min_zoom", 13)
                  .setZoomRange(12, 15)
                  .setAttrWithMinzoom("shield_text", shield_text, 14)
                  .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                  .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                  .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                  .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                  .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                  .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                  .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                  .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                  .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                  .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                  .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                  .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                  .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                  .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                  .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                  .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
          OsmNames.setOsmNames(feat, sourceFeature, 14);
          break;
        case "tertiary_link":
        case "footway":
        case "steps":
          feat.setAttr("kind", "construction")
                  .setAttr("kind_detail", construction_val)
                  .setAttr("min_zoom", 14)
                  .setZoomRange(13, 15)
                  .setAttrWithMinzoom("shield_text", shield_text, 14)
                  .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                  .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                  .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                  .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                  .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                  .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                  .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                  .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                  .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                  .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                  .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                  .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                  .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                  .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                  .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                  .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
          OsmNames.setOsmNames(feat, sourceFeature, 14);
          break;
        case "corridor":
          feat.setAttr("kind", "construction")
                  .setAttr("kind_detail", construction_val)
                  .setAttr("min_zoom", 16)
                  // TODO: MAX_ZOOM
                  .setZoomRange(15, 15)
                  .setAttrWithMinzoom("shield_text", shield_text, 14)
                  .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                  .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                  .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                  .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                  .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                  .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                  .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                  .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                  .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                  .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                  .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                  .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                  .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                  .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                  .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                  .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);
          OsmNames.setOsmNames(feat, sourceFeature, 15);
          break;
      }
    }

     // OSM aeroway
     if( sourceFeature.hasTag("aeroway", "runway", "taxiway")) {
       String shield_text = sourceFeature.getString("ref");
       Integer shield_text_length = (shield_text == null ? null : shield_text.length());

       var feat = features.line(this.name())
               // This inhibits feature merging, and should only be exported at max_zoom
               //.setId(FeatureId.create(sourceFeature))
               .setMinPixelSize(0)
               .setPixelTolerance(0)
               // TODO (nvkelso 2023-03-26)
               //      This should be expressed as a boolean ("is_bridge")
               .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
               // TODO (nvkelso 2023-03-26)
               //      This should be expressed as a boolean ("is_tunnel")
               .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
               // TODO (nvkelso 2023-03-26)
               //      This should be sanity checked for ±6 int values
               .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
               .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
               .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
               .setAttrWithMinzoom("ref", shield_text, 14)
               .setAttrWithMinzoom("shield_text", shield_text, 14)
               .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
               .setAttr("source", "openstreetmap.org");

       switch (sourceFeature.getString("aeroway")) {
         case "runway":
           feat.setAttr("min_zoom", 9)
                   // OSM transition from OSM at zoom 8
                   .setAttr("kind", "aeroway")
                   .setAttr("kind_detail", "runway")
                   .setZoomRange(8, 15);
           break;
         case "taxiway":
           feat.setAttr("min_zoom", 11)
                   // OSM transition from OSM at zoom 8
                   .setAttr("kind", "aeroway")
                   .setAttr("kind_detail", "taxiway")
                   .setZoomRange(10, 15);
           break;
       }

       OsmNames.setOsmNames(feat, sourceFeature, 14);
     }

      //#############################################################
      //
      // OSM railway
      //
      //#############################################################
      if( sourceFeature.hasTag("railway", "rail", "disused", "funicular", "light_rail", "miniature", "monorail", "narrow_gauge", "preserved", "subway", "tram")) {
        var feat = features.line(this.name())
                // This inhibits feature merging, and should only be exported at max_zoom
                //.setId(FeatureId.create(sourceFeature))
                .setMinPixelSize(0)
                .setPixelTolerance(0)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_bridge")
                .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_tunnel")
                .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be sanity checked for ±6 int values
                .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
                .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
                .setAttr("source", "openstreetmap.org")
                // nvkelso (20230321)
                // TODO    I just don't want them showing up early
                .setZoomRange(20, 20);

        var railway_val = sourceFeature.getString("railway");
        var service_val = sourceFeature.getString("service");

        if (railway_val == "rail") {
          if (sourceFeature.hasTag("service", "spur", "siding", "yard", "crossover")) {
            switch (service_val) {
              case "spur":
              case "siding":
                feat.setAttr("kind", "rail")
                    .setAttr("kind_detail", service_val)
                    .setAttr("min_zoom", 12)
                    .setZoomRange(11, 15);
                break;
              case "yard":
                feat.setAttr("kind", "rail")
                    .setAttr("kind_detail", service_val)
                    .setAttr("min_zoom", 13)
                    .setZoomRange(12, 15);
                break;
              case "crossover":
                feat.setAttr("kind", "rail")
                    .setAttr("kind_detail", service_val)
                    .setAttr("min_zoom", 15)
                    .setZoomRange(14, 15);
                break;
              case "branch":
              case "connector":
              case "wye":
              case "runaway":
              case "interchange":
              case "switch":
              case "industrial":
              case "disused":
              case "driveway":
              case "passing_loop":
                feat.setAttr("kind", "rail")
                        .setAttr("kind_detail", service_val)
                        .setAttr("min_zoom", 15)
                        .setZoomRange(14, 15);
                break;
            }
          }
          // railway has no service tag
          else {
            feat.setAttr("kind", "rail")
                    .setAttr("kind_detail", "rail")
                    .setAttr("min_zoom", 11)
                    .setZoomRange(10, 15);
          }
        }
        // railway: ["disused", "funicular", "light_rail", "miniature", "monorail", "narrow_gauge", "preserved", "subway", "tram"]
        else {
          feat.setAttr("kind", "rail")
              .setAttr("kind_detail", railway_val)
              .setAttr("min_zoom", 15)
              .setZoomRange(14, 15);

          if( sourceFeature.hasTag("usage", "tourism") ) {
            feat.setAttr("kind_detail", "tourism")
                .setAttr("min_zoom", 14)
                .setZoomRange(13, 15);
          }
        }

        // This is a hack, something in the logic above is busted
        if( railway_val == "rail" && sourceFeature.hasTag("usage", "main")) {
          feat.setAttr("kind", "rail")
                  .setAttr("kind", railway_val)
                  .setAttr("kind_detail", "main")
                  .setAttr("min_zoom", 10)
                  .setZoomRange(9, 15);
        }

          OsmNames.setOsmNames(feat, sourceFeature, 14);
      }

      //#############################################################
      //#
      //# OSM ferry
      //#
      //#############################################################

      if( sourceFeature.hasTag("route", "ferry") ) {
        String shield_text = sourceFeature.getString("ref");
        Integer shield_text_length = (shield_text == null ? null : shield_text.length());

        var feat = features.line(this.name())
                // This inhibits feature merging, and should only be exported at max_zoom
                //.setId(FeatureId.create(sourceFeature))
                .setMinPixelSize(0)
                .setPixelTolerance(0)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_bridge")
                .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_tunnel")
                .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be sanity checked for ±6 int values
                .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
                .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
                .setAttr("source", "openstreetmap.org")
                .setAttr("kind", "ferry")
                // TODO use a variable zoom for ferries
                //min_zoom: { call: { func: mz_calculate_ferry_level, args: [ { col: shape } ] } }
                .setAttr("min_zoom", 13)
                .setZoomRange(12, 15)
                .setAttrWithMinzoom("shield_text", shield_text, 14)
                .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                //.setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                //.setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                //.setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                //.setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                //.setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                //.setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);

        Double way_length = null;
        try { way_length = sourceFeature.length(); } catch(GeometryException e) {  System.out.println(e); }

        if( way_length > 1223) {
          feat.setAttr("min_zoom", 8)
                  .setZoomRange(7, 15);
        } else if (way_length > 611) {
          feat.setAttr("min_zoom", 9)
                  .setZoomRange(8, 15);
        } else if (way_length > 306) {
          feat.setAttr("min_zoom", 10)
                  .setZoomRange(9, 15);
        } else if (way_length > 153) {
          feat.setAttr("min_zoom", 11)
                  .setZoomRange(10, 15);
        } else if (way_length > 76) {
          feat.setAttr("min_zoom", 12)
                  .setZoomRange(11, 15);
        } else {
          feat.setAttr("min_zoom", 13)
                  .setZoomRange(12, 15);
        }

        OsmNames.setOsmNames(feat, sourceFeature, 13);
      }
      //#############################################################
      //#
      //# OSM aerialway
      //#
      //#############################################################

      if( sourceFeature.hasTag("aerialway", "gondola", "cable_car",
              "chair_lift",
              "drag_lift", "platter", "t-bar", "goods", "magic_carpet", "rope_tow", "yes", "zip_line", "j-bar", "unknown", "mixed_lift", "canopy", "cableway"
      ) ) {
        String shield_text = sourceFeature.getString("ref");
        Integer shield_text_length = (shield_text == null ? null : shield_text.length());

        var feat = features.line(this.name())
                .setAttr("kind", "aerialway")
                // This inhibits feature merging, and should only be exported at max_zoom
                //.setId(FeatureId.create(sourceFeature))
                .setMinPixelSize(0)
                .setPixelTolerance(0)
                // defaults for theme, overriden by kind below
                .setAttr("min_zoom", 15)
                .setZoomRange(14, 15)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_bridge")
                .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_tunnel")
                .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be sanity checked for ±6 int values
                .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
                .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
                .setAttr("source", "openstreetmap.org")
                // TODO use a variable zoom for ferries
                .setAttrWithMinzoom("shield_text", shield_text, 14)
                .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                //.setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                //.setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                //.setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                //.setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                //.setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                //.setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);

        var aerialway_val = sourceFeature.getString("aerialway");
        switch( aerialway_val ) {
          case "gondola":
          case "cable_car":
            feat.setAttr("kind_detail", aerialway_val)
                    .setAttr("min_zoom", 12)
                    .setZoomRange(11, 15);
            break;
          case "chair_lift":
            feat.setAttr("kind_detail", aerialway_val)
                    .setAttr("min_zoom", 13)
                    .setZoomRange(12, 15);
            break;
          case "drag_lift":
          case "platter":
          case "goods":
          case "magic_carpet":
          case "rope_tow":
          case "zip_line":
          case "unknown":
          case "mixed_lift":
          case "canopy":
          case "cableway":
            feat.setAttr("kind_detail", aerialway_val)
                    .setAttr("min_zoom", 15)
                    .setZoomRange(14, 15);
            break;
          case "t-bar":
            feat.setAttr("kind_detail", "t_bar")
                    .setAttr("min_zoom", 15)
                    .setZoomRange(14, 15);
            break;
          case "j-bar":
            feat.setAttr("kind_detail", "t_bar")
                    .setAttr("min_zoom", 15)
                    .setZoomRange(14, 15);
            break;
        }
        OsmNames.setOsmNames(feat, sourceFeature, 13);
      }

      //#############################################################
      //#
      //# OSM leisure
      //#
      //#############################################################
      if( sourceFeature.hasTag( "leisure", "track" ) &&
              sourceFeature.hasTag( "sport", "athletics", "running", "horse_racing", "bmx", "disc_golf", "cycling", "ski_jumping", "motor", "karting", "obstacle_course", "equestrian", "alpine_slide", "soap_box_derby", "mud_truck_racing", "skiing", "drag_racing", "archery")
      ) {
        String shield_text = sourceFeature.getString("ref");
        Integer shield_text_length = (shield_text == null ? null : shield_text.length());

        var feat = features.line(this.name())
                .setAttr("kind", "racetrack")
                .setAttr("kind_detail", sourceFeature.getString("sport"))
                // TODO (nvkelso 2023-03-29)
                //      (v2) This seems unnecesary
                .setAttr("leisure", "track")
                // This inhibits feature merging, and should only be exported at max_zoom
                //.setId(FeatureId.create(sourceFeature))
                .setMinPixelSize(0)
                .setPixelTolerance(0)
                // defaults for theme, overriden by kind below
                .setAttr("min_zoom", 14)
                .setZoomRange(13, 15)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_bridge")
                .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_tunnel")
                .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be sanity checked for ±6 int values
                .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
                .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
                .setAttr("source", "openstreetmap.org")
                // TODO use a variable zoom for ferries
                .setAttrWithMinzoom("shield_text", shield_text, 14)
                .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);

        OsmNames.setOsmNames(feat, sourceFeature, 14);
      }

      //#############################################################
      //#
      //# OSM man_made
      //#
      //#############################################################

      // By this time we're already a line, but beware sometimes polygon data
      if(  sourceFeature.hasTag( "man_made", "pier", "quay") ) {
        String shield_text = sourceFeature.getString("ref");
        Integer shield_text_length = (shield_text == null ? null : shield_text.length());

        var feat = features.line(this.name())
                .setAttr("kind", "path")
                .setAttr("kind_detail", sourceFeature.getString("man_made"))
                // This inhibits feature merging, and should only be exported at max_zoom
                //.setId(FeatureId.create(sourceFeature))
                .setMinPixelSize(0)
                .setPixelTolerance(0)
                // defaults for theme, overriden by kind below
                .setAttr("min_zoom", 13)
                .setZoomRange(12, 15)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_bridge")
                .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_tunnel")
                .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be sanity checked for ±6 int values
                .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
                .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
                .setAttr("source", "openstreetmap.org")
                // TODO use a variable zoom for ferries
                .setAttrWithMinzoom("shield_text", shield_text, 14)
                .setAttrWithMinzoom("shield_text_length", shield_text_length, 14)
                .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14);

        if( sourceFeature.hasTag( "mooring", "no", "yes", "commercial", " cruise", " customers", "declaration", "ferry", "guest", "private", "public", "waiting", "yacht", "yachts") ) {
          feat.setAttr("kind_detail", sourceFeature.getString("mooring"));
        }

        OsmNames.setOsmNames(feat, sourceFeature, 14);
      }

      //#############################################################
      //#
      //# OSM piste
      //#
      //#############################################################

      if( ! sourceFeature.hasTag( "piste:abandoned", "yes") &&
              sourceFeature.hasTag( "piste:type", "nordic", "downhill", "sleigh", "skitour", "hike", "sled", "yes", "snow_park", "playground", "ski_jump")
      ) {
        String shield_text = sourceFeature.getString("ref");
        Integer shield_text_length = (shield_text == null ? null : shield_text.length());

        var feat = features.line(this.name())
                .setAttr("kind", "piste")
                // This inhibits feature merging, and should only be exported at max_zoom
                //.setId(FeatureId.create(sourceFeature))
                .setMinPixelSize(0)
                .setPixelTolerance(0)
                .setAttr("min_zoom", 13)
                .setZoomRange(12, 15)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_bridge")
                .setAttrWithMinzoom("bridge", sourceFeature.getString("bridge"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be expressed as a boolean ("is_tunnel")
                .setAttrWithMinzoom("tunnel", sourceFeature.getString("tunnel"), 12)
                // TODO (nvkelso 2023-03-26)
                //      This should be sanity checked for ±6 int values
                .setAttrWithMinzoom("layer", sourceFeature.getString("layer"), 12)
                .setAttrWithMinzoom("oneway", sourceFeature.getString("oneway"), 15)
                .setAttr("source", "openstreetmap.org")
                // TODO (nvkelso 2023-03-29)
                //      (v2) this seems unneccesary
                .setAttrWithMinzoom("ref", shield_text, 13)
                .setAttrWithMinzoom("shield_text", shield_text, 13)
                .setAttrWithMinzoom("shield_text_length", shield_text_length, 13)
                .setAttrWithMinzoom("network", sourceFeature.getString("network"), 14)
                .setAttrWithMinzoom("service", sourceFeature.getString("service"), 14)
                .setAttrWithMinzoom("surface", sourceFeature.getString("surface"), 14)
                .setAttrWithMinzoom("cycleway", sourceFeature.getString("cycleway"), 14)
                .setAttrWithMinzoom("cycleway_left", sourceFeature.getString("cycleway:left"), 14)
                .setAttrWithMinzoom("cycleway_right", sourceFeature.getString("cycleway:right"), 14)
                .setAttrWithMinzoom("hgv", sourceFeature.getString("hgv"), 14)
                .setAttrWithMinzoom("colour", sourceFeature.getString("colour"), 15)
                .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 15)
                .setAttrWithMinzoom("route", sourceFeature.getString("route"), 15)
                .setAttrWithMinzoom("route_name", sourceFeature.getString("route_name"), 15)
                .setAttrWithMinzoom("state", sourceFeature.getString("state"), 15)
                .setAttrWithMinzoom("symbol", sourceFeature.getString("symbol"), 15)
                .setAttrWithMinzoom("type", sourceFeature.getString("type"), 15)
                .setAttrWithMinzoom("access", sourceFeature.getString("access"), 14)

                // These are from vector-datasource, but not sure wby they aren't part of core set
                .setAttrWithMinzoom("description", sourceFeature.getString("description"), 14)
                .setAttrWithMinzoom("distance", sourceFeature.getString("distance"), 14)
                .setAttrWithMinzoom("ascent", sourceFeature.getString("ascent"), 14)
                .setAttrWithMinzoom("descent", sourceFeature.getString("descent"), 14)
                .setAttrWithMinzoom("roundtrip", sourceFeature.getString("roundtrip"), 14)

                // These are piste specific
                .setAttrWithMinzoom("piste_difficulty", sourceFeature.getString("piste:difficulty"), 13)
                .setAttrWithMinzoom("piste_grooming", sourceFeature.getString("piste:grooming"), 13)
                .setAttrWithMinzoom("piste_name", sourceFeature.getString("piste:name"), 14)
                .setAttrWithMinzoom("ski", sourceFeature.getString("ski"), 13)
                .setAttrWithMinzoom("snowshoe", sourceFeature.getString("snowshoe"), 13);

        if( ! sourceFeature.hasTag( "piste:type", "yes" ) ) {
          feat.setAttr("kind_detail", sourceFeature.getString("piste:type"));
        }

        if( sourceFeature.hasTag( "piste:name" ) && sourceFeature.getString("piste:name") != null ) {
          feat.setAttr("name", sourceFeature.getString("piste:name"));
        }

        OsmNames.setOsmNames(feat, sourceFeature, 14);
      }
    }
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {

    // items = graphAnalyze(items, "highway", "motorway", "motorway_link");
    // items = graphAnalyze(items, "highway", "trunk", "trunk_link");
    // items = graphAnalyze(items, "highway", "primary", "primary_link");
    // items = graphAnalyze(items, "highway", "secondary", "secondary_link");

// 	{% if zoom >= 12 %}
// 		  'is_bus_route',
// 		  -- try to only calculate whether this is a bus route when we already know
// 		  -- that it's a road, as joining onto the rels table can be expensive.
// 		  CASE WHEN tags->'highway' IN ('motorway', 'motorway_link', 'trunk', 'trunk_link',
// 								'primary', 'primary_link', 'secondary', 'secondary_link',
// 								'tertiary', 'tertiary_link',
// 								'residential', 'unclassified', 'road', 'living_street')
// 			THEN mz_calculate_is_bus_route(osm_id)
// 		  END,
// 	{% endif %}
// 		  'mz_cycling_network', mz_cycling_network(tags, osm_id),
// 		  'mz_networks', mz_get_rel_networks(osm_id)
// 		)
// 	  END AS __roads_properties__,


    items = FeatureMerge.mergeLineStrings(items,
      0.5, // after merging, remove lines that are still less than 0.5px long
      0.5, // simplify output linestrings using a 0.1px tolerance
      8 // remove any detail more than 8px outside the tile boundary
    );

    return items;
  }
}
