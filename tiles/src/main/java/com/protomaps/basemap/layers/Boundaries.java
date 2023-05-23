package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.reader.osm.OsmElement;
import com.onthegomap.planetiler.reader.osm.OsmReader;
import com.onthegomap.planetiler.reader.osm.OsmRelationInfo;
import com.onthegomap.planetiler.util.Parse;
import com.protomaps.basemap.feature.FeatureId;

//import com.protomaps.basemap.util.NeNames;
// import com.protomaps.basemap.util.OsmNames;
// TODO (nvkelso 2023-03-25)
// The above is new and unrefined
// the below doesn't exist
//import com.protomaps.basemap.names.WofNames;
import java.util.List;
import java.util.OptionalInt;

public class Boundaries implements ForwardingProfile.OsmRelationPreprocessor, ForwardingProfile.FeatureProcessor,
  ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "boundaries";
  }

  // TODO (nvkelso 2023-03-21)
  // 1. buffered_land (for calculating maritime boundary property)
  // 2. set kinds on NE sources
  // 3. set other properties on NE sources
  // 4. NE disputed boundary point-of-view
  //    (eg remap_viewpoint_kinds, unpack_viewpoint_claims, admin_level_alternate_viewpoint, create_dispute_ids)
  // 5. OSM disputed boundary point-of-view
  // 6. spreadsheets/sort_rank/boundaries.csv
  // 7. perform vectordatasource.transform.admin_boundaries for OSM polygons to get oriented lines for left/right names
  // 8. perform vectordatasource.transform.apply_disputed_boundary_viewpoints
  // 9. perform vectordatasource.transform.drop_properties to cleanup from 8
  // 10. don't need id, id:left, id:right at zooms earlier than 12
  // 11. don't need names on county or locality until zoom 12
  // 12. ensure like propertied line features are merged at zooms less than 13
  // 13. don't need names that won't fit on short line segments at zooms less than 13
  // 14. ensure like propertied line features are merged at zooms less than 13, again
  // 15. don't need names on NE county or region or macroregion lines at any low zoom
  // 16. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 17. vectordatasource.transform.drop_small_inners
  // 18. vectordatasource.transform.quantize_height with diff settings for 3 zoom ranges
  // 19. vectordatasource.transform.csv_match_properties to assign scale_rank before merging
  // 20. vectordatasource.transform.drop_properties to drop landuse_kind at low zooms selectively by value
  // 21. vectordatasource.transform.drop_properties to drop kind_detail at low zooms selectively by value
  // 22. vectordatasource.transform.drop_properties to drop various properties at most zooms (this is for max_zoom 3D stuff)
  // 23. vectordatasource.transform.drop_properties to drop various name and address properties at most zooms (file size)
  // 24. vectordatasource.transform.clamp_min_zoom (this is to encourage building merging)
  // 25. vectordatasource.transform.merge_building_features but also by dropping properties at mid-zooms
  // 26. vectordatasource.transform.drop_small_inners
  // 27. vectordatasource.transform.numeric_min_filter
  // 28. vectordatasource.transform.drop_features_where to remove medium and small buildings from zoom 12
  // 29. vectordatasource.transform.drop_features_where to remove small buildings from zoom 13
  // 30. vectordatasource.transform.add_collision_rank

  public void processNe(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    var kind = "";
    var theme_min_zoom = 0;
    var theme_max_zoom = 0;
    // TODO (nvkelso 2023-03-25)
    //      These should all be per feature instead of layer... but per feature per layer
    if (sourceLayer.equals("ne_110m_admin_0_boundary_lines_land")) {
      // use defaults
      kind = "tz_boundary";
    } else if (sourceLayer.equals("ne_50m_admin_0_boundary_lines_land") || sourceLayer.equals("ne_50m_admin_0_boundary_lines_disputed_areas") || sourceLayer.equals("ne_50m_admin_0_boundary_lines_maritime_indicator_chn") || sourceLayer.equals("ne_50m_admin_1_states_provinces_lines")) {
      theme_min_zoom = 1;
      theme_max_zoom = 3;
      kind = "tz_boundary";
    } else if (sourceLayer.equals("ne_10m_admin_0_boundary_lines_land") || sourceLayer.equals("ne_10m_admin_0_boundary_lines_map_units") || sourceLayer.equals("ne_10m_admin_0_boundary_lines_disputed_areas") || sourceLayer.equals("ne_10m_admin_0_boundary_lines_maritime_indicator_chn") || sourceLayer.equals("ne_10m_admin_1_states_provinces_lines")) {
      theme_min_zoom = 4;
      theme_max_zoom = 6;
      kind = "tz_boundary";
    }

    // TODO (nvkelso 2023-03-26)
    //      Compiler is fussy about booleans and strings, beware
    if( kind != "") {
      switch (sf.getString("featurecla")) {
        case "Disputed (please verify)" -> kind = "disputed";
        case "Indefinite (please verify)" -> kind = "indefinite";
        case "Indeterminant frontier" -> kind = "indeterminate";
        case "International boundary (verify)" -> kind = "country";
        case "Lease limit" -> kind = "lease_limit";
        case "Line of control (please verify)" -> kind = "line_of_control";
        case "Overlay limit" -> kind = "overlay_limit";
        case "Unrecognized" -> kind = "unrecognized_country";
        case "Map unit boundary" -> kind = "map_unit";
        case "Breakaway" -> kind = "disputed_breakaway";
        case "Claim boundary" -> kind = "disputed_claim";
        case "Elusive frontier" -> kind = "disputed_elusive";
        case "Reference line" -> kind = "disputed_reference_line";
        default -> kind = "";
      }
    }

    if (sf.canBeLine() && sf.hasTag("min_zoom") && (kind.equals("") == false && kind.equals("tz_boundary") == false))
    {
      features.line(this.name())
              .setAttr("name", sf.getString("name"))
              // TODO (nvkelso 2023-03-25)
              //      This should be a single decimal precision float not string
              .setAttr("min_zoom", sf.getLong("min_zoom"))
              //      This should be a single decimal precision float not string
              //      See below section, too
              .setZoomRange(sf.getString("min_zoom") == null ? theme_min_zoom : (int)Double.parseDouble(sf.getString("min_zoom")), theme_max_zoom)
              .setAttr("ne_id", sf.getString("ne_id"))
              .setAttr("BRK_A3", sf.getString("brk_a3"))
              .setAttr("kind", kind)
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

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    if (sf.canBeLine()) {
      if (sf.hasTag("natural", "coastline") || sf.hasTag("maritime", "yes")) {
        return;
      }
      List<OsmReader.RelationMember<AdminRecord>> recs = sf.relationInfo(AdminRecord.class);
      if (recs.size() > 0) {
        OptionalInt minAdminLevel = recs.stream().mapToInt(r -> r.relation().adminLevel).min();
        var linestring = features.line(this.name())
              .setId(FeatureId.create(sf))
              .setMinPixelSize(0)
              .setAttr("kind_detail", minAdminLevel.getAsInt())
              // TODO: This is a hack as I see random admin lines too early
              .setMinZoom(20);

        if (minAdminLevel.getAsInt() == 2) {
          linestring.setAttr("kind", "country")
            .setAttr("claimed_by", sf.getString("claimed_by"))
            .setAttr("recognized_by", sf.getString("recognized_by"))
            .setAttr("disputed_by", sf.getString("disputed_by"))
            .setAttr("tz_admin_level", sf.getString("admin_level"))
            // Natural Earth at low zooms, else we'd say "1" instead of "8"
            // (though NE varies the zoom per country in the data)
            .setAttr("min_zoom", 8)
            .setMinZoom(7);
        } else if (minAdminLevel.getAsInt() == 4) {
          linestring.setAttr("kind", "region")
            .setAttr("claimed_by", sf.getString("claimed_by"))
            .setAttr("recognized_by", sf.getString("recognized_by"))
            .setAttr("disputed_by", sf.getString("disputed_by"))
            .setAttr("tz_admin_level", sf.getString("admin_level"))
            // Natural Earth at low zooms, else we'd say "3" instead of "8"
            // (though NE varies the zoom per country in the data)
            .setAttr("min_zoom", 8)
            .setMinZoom(7);
        } else if (minAdminLevel.getAsInt() == 6) {
	      // TODO (nvkelso 2023-03-25)
	      //      This should be a build config, else don't export county lines
	      //      As they are often not shown in basemap designs but take up file size
	      //      Similarly, county labels in places are only sometimes labeled
          linestring.setAttr("kind", "county")
            .setAttr("min_zoom", 10)
            .setMinZoom(9);
        } else if (minAdminLevel.getAsInt() == 8) {
	      // TODO (nvkelso 2023-03-25)
	      //      This should be a build config, else don't export locality lines
	      //      As they are often not shown in basemap designs but take up file size
	      //      While city "townspots" are always labeled in places layer
          linestring.setAttr("kind", "locality")
            .setAttr("min_zoom", 11)
            .setMinZoom(10);
        }

	    linestring.setAttr("source", "openstreetmap.org");
      }
    }
  }

  @Override
  public List<OsmRelationInfo> preprocessOsmRelation(OsmElement.Relation relation) {
    if (relation.hasTag("type", "boundary") && relation.hasTag("boundary", "administrative")) {
      Integer adminLevel = Parse.parseIntOrNull(relation.getString("admin_level"));
      if (adminLevel == null || adminLevel > 8)
        return null;
      return List.of(new AdminRecord(relation.id(), adminLevel));
    }
    return null;
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // This should also be 8 px buffer for lines

    return FeatureMerge.mergeLineStrings(items,
      0.0,
      0.5,
      8
    );
  }

  private record AdminRecord(long id, int adminLevel) implements OsmRelationInfo {}
}
