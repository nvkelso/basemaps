package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.Parse;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.NeNames;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class Places implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "places";
  }

  // TODO (nvkelso 2023-03-21)
  // 1.  place_population_int
  // 2.  population_rank
  // 3.  capital_alternate_viewpoint
  // 4.  unpack_places_disputes
  // 5.  apply_places_with_viewpoints
  // 6.  calculate_default_place_min_zoom
  // 7.  override_with_ne_names
  // 8.  perform vectordatasource.transform.point_in_country_logic to get iso_codes for country and region (requires admin_areas source)
  //     - this should be new core planetiler feature separate from combined layer logic
  // 9.  perform vectordatasource.transform.tags_set_ne_min_max_zoom
  // 10. perform vectordatasource.transform.tags_set_ne_pop_min_max_default
  // 11. perform vectordatasource.transform.min_zoom_filter
  // 12. perform vectordatasource.transform.keep_n_features_gridded per zoom 7,8,9,10,11 to radically reduce file size
  // 13. perform vectordatasource.transform.rank_features on neighbourhood, microhood, macrohood
  // 14. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 15. vectordatasource.transform.max_zoom_filter
  // 16. vectordatasource.transform.add_collision_rank

  public void processNe(SourceFeature sf, FeatureCollector features) {
    var sourceLayer = sf.getSourceLayer();
    var kind = "";
    var kind_detail = "";
    // TODO (nvkelso 2023-03-25)
    //      I'm confused about Booleans in Java and Planetiler, fix
//    boolean country_capital = False;
//    boolean region_capital = False;

    var theme_min_zoom = 0;
    var theme_max_zoom = 0;
    // TODO (nvkelso 2023-03-25)
    //      These should all be per feature instead of layer... but per feature per layer
    if (sourceLayer.equals("ne_10m_populated_places")) {
      theme_min_zoom = 1;
      theme_max_zoom = 8;
    }

    // Test for props because of Natural Earth funk
    if( sf.isPoint() && sf.hasTag("featurecla") && sf.hasTag("min_zoom" ) ) {
      switch (sf.getString("featurecla")) {
        case "Admin-0 capital":
        case "Admin-0 capital alt":
        case "Admin-0 region capital":
          kind = "locality";
          //country_capital = true;
          break;
        case "Admin-1 capital":
        case "Admin-1 region capital":
          kind = "locality";
          //region_capital = true;
          break;
        case "Populated place":
          kind = "locality";
          break;
        case "Historic place":
          kind = "locality";
          kind_detail = "hamlet";
          break;
        case "Scientific station":
          kind = "locality";
          kind_detail = "scientific_station";
          break;
      }
    }

    if( kind != "" ) {
      var feat = features.point(this.name())
              .setAttr("name", sf.getString("name"))
              // TODO (nvkelso 2023-03-25)
              //      This should be a single decimal precision float not string
              .setAttr("min_zoom", sf.getLong("min_zoom"))
              //      This should be a single decimal precision float not string
              //      See below section, too
              .setZoomRange(sf.getString("min_zoom") == null ? theme_min_zoom : (int)Double.parseDouble(sf.getString("min_zoom")), theme_max_zoom)
              .setAttr("kind", kind)
              .setAttr("kind_detail", kind_detail)
              .setAttr("population", sf.getString("pop_max"))
              .setAttr("population_rank", sf.getString("rank_max"))
              .setAttr("wikidata_id", sf.getString("wikidata"))
              // TODO (nvkelso 2023-03-26)
              //      Don't think this is needed in output, but 2x check
              //.setAttr("scalerank", sf.getString("scalerank"))
              //.setAttr("BRK_A3", sf.getString("brk_a3"))
              //.setAttr("ne_id", sf.getString("ne_id"))
              // TODO (nvkelso 2023-03-25)
              // 	{% if viewpoints %}
              // 	  {%- for vpt in ne_viewpoints %}
              // 		'fclass_{{vpt}}', fclass_{{vpt}},
              // 	  {%- endfor %}
              // 	{% endif %}
              .setAttr("source", "naturalearthdata.com")
              .setBufferPixels(128);

      NeNames.setNeNames(feat, sf, 0);
    }
  }

    // 'source', 'naturalearthdata.com'
// 	jsonb_build_object(
// 		'name', name,
// 		{%- for lang in ne_languages %}
// 		'name_{{lang}}', name_{{lang}},
// 		{%- endfor %}
// 		'population', pop_max,
// 		'featurecla', featurecla,
// 		'scalerank', scalerank,
// 		'min_zoom', mz_places_min_zoom,
// 		'wikidata', wikidataid
// 	  ) || jsonb_build_object(
// 	{#- need to have a separate call to jsonb_build_object for viewpoints because it doesn't support more than 100 parameters, and we're over that with the number of languages + number of viewpoints #}
// 	{%- for vpt in ne_viewpoints %}
// 		'fclass_{{vpt}}', fclass_{{vpt}}{{ "," if not loop.last }}
// 	{%- endfor %}
//
// 	WHERE
//
// 	  {{ bounds['point']|bbox_filter('the_geom', 3857) }} AND
// 	  mz_places_min_zoom < {{ zoom + 1 }}
// 	{% if zoom >= 8 and zoom < 10 %}
// 	  AND pop_max <= 50000
// 	{% elif zoom >= 10 and zoom < 11 %}
// 	  AND pop_max <= 20000
// 	{% elif zoom >= 11 %}
// 	  AND pop_max <= 5000
// 	{% endif %}

  @Override
  public void processFeature(SourceFeature sf, FeatureCollector features) {
    if (sf.isPoint() &&
        // nvkelso (20230321)
        // TODO expand range of values here, and a-z sort
      (sf.hasTag("place", "suburb", "town", "village", "neighbourhood", "city", "country", "state"))) {
      var kind = "";
      var kind_detail = "";
      Double feature_min_zoom = 0.0;
      Integer px512_min_zoom = 0;
      Integer px512_max_zoom = 0;
      Integer population = sf.getString("population") == null ? 0 : (int)Double.parseDouble(sf.getString("population"));

      var feat = features.point(this.name())
        .setId(FeatureId.create(sf))
        .setAttr("place", sf.getString("place"))
        .setAttr("country_code", sf.getString("country_code_iso3166_1_alpha_2"))
        .setAttr("capital", sf.getString("capital"))
        .setAttr("source", "openstreetmap.org")
        .setBufferPixels(128)
        // nvkelso (20230321)
        // TODO    I just don't want them showing up early
        .setZoomRange(20, 20);

      OsmNames.setOsmNames(feat, sf, 0);

      switch (sf.getString("place")) {
        case "country":
          kind = "country";
          // nvkelso (20230321)
          // TODO max_zoom should vary by country, or read NE property
          feature_min_zoom = 2.0;
          px512_min_zoom = 1;
          px512_max_zoom = 9;
          break;
        case "unrecognized":
          kind = "unrecognized";
          // nvkelso (20230321)
          // TODO max_zoom should vary by country, or read NE property
          feature_min_zoom = 8.0;
          px512_min_zoom = 7;
          px512_max_zoom = 9;
          break;
        case "state":
        case "province":
          kind = "region";
          kind_detail = sf.getString("place");
          feature_min_zoom = 9.0;
          px512_min_zoom = 8;
          px512_max_zoom = 11;
          break;
        case "city":
          kind = "locality";
          kind_detail = "city";
          feature_min_zoom = 9.0;
          px512_min_zoom = 8;
          px512_max_zoom = 15;
          if ( population > 0 ) {
            feature_min_zoom = 8.0;
            px512_min_zoom = 7;
            px512_max_zoom = 15;
          } else {
            population = 10000;
          }
          break;
        case "town":
          kind = "locality";
          kind_detail = "town";
          feature_min_zoom = 10.0;
          px512_min_zoom = 9;
          px512_max_zoom = 15;
          if ( population > 0 ) {
            feature_min_zoom = 9.0;
            px512_min_zoom = 8;
            px512_max_zoom = 15;
          } else {
            population = 5000;
          }
          break;
        case "village":
          kind = "locality";
          kind_detail = "village";
          feature_min_zoom = 13.0;
          px512_min_zoom = 12;
          px512_max_zoom = 15;
          if ( population > 0 ) {
            feature_min_zoom = 12.0;
            px512_min_zoom = 11;
            px512_max_zoom = 15;
          } else {
            population = 2000;
          }
          break;
        case "locality":
          kind = "locality";
          kind_detail = "locality";
          feature_min_zoom = 14.0;
          px512_min_zoom = 13;
          px512_max_zoom = 15;
          if ( population > 0 ) {
            feature_min_zoom = 13.0;
            px512_min_zoom = 12;
            px512_max_zoom = 15;
          } else {
            population = 1000;
          }
          break;
        case "hamlet":
          kind = "locality";
          kind_detail = "hamlet";
          feature_min_zoom = 14.0;
          px512_min_zoom = 13;
          px512_max_zoom = 15;
          if ( population > 0 ) {
            feature_min_zoom = 13.0;
            px512_min_zoom = 12;
            px512_max_zoom = 15;
          } else {
            population = 200;
          }
          break;
        case "isolated_dwelling":
          kind = "locality";
          kind_detail = "isolated_dwelling";
          feature_min_zoom = 15.0;
          px512_min_zoom = 14;
          px512_max_zoom = 15;
          if ( population > 0 ) {
            feature_min_zoom = 14.0;
            px512_min_zoom = 13;
            px512_max_zoom = 15;
          } else {
            population = 100;
          }
          break;
        case "farm":
          kind = "locality";
          kind_detail = "farm";
          feature_min_zoom = 15.0;
          px512_min_zoom = 14;
          px512_max_zoom = 15;
          if ( population > 0 ) {
            feature_min_zoom = 14.0;
            px512_min_zoom = 13;
            px512_max_zoom = 15;
          } else {
            population = 50;
          }
          break;
        // nvkelso (20230321)
        // TODO watch out Australia suburb to locality recast sql job
        case "suburb":
          kind = "neighbourhood";
          kind_detail = "suburb";
          feature_min_zoom = 13.0;
          px512_min_zoom = 12;
          px512_max_zoom = 15;
          break;
        // nvkelso (20230321)
        // TODO: switch real neighbourhoods to WOF source
        case "neighbourhood":
          kind = "neighbourhood";
          kind_detail = "neighbourhood";
          feature_min_zoom = 13.0;
          px512_min_zoom = 12;
          px512_max_zoom = 15;
          break;
        }

      Integer population_rank = 0;

      int[] pop_breaks = {
              1000000000,
              100000000,
              50000000,
              20000000,
              10000000,
              5000000,
              1000000,
              500000,
              200000,
              100000,
              50000,
              20000,
              10000,
              5000,
              2000,
              1000,
              200,
              0};

      for (int i = 0; i < pop_breaks.length; i++) {
        if( population >= pop_breaks[i]) {
          population_rank = pop_breaks.length - i;
        }
      }

      if( kind != "" ) {
        feat.setAttr("kind", kind)
                .setAttr("kind_detail", kind_detail)
                .setAttr("population", population)
                .setAttr("population_rank", population_rank)
                //      This should be a single decimal precision float not string
                //      See below section, too
                .setAttr("min_zoom", feature_min_zoom)
                .setZoomRange(px512_min_zoom, px512_max_zoom);
      }
    }
  }

  // nvkelso (20230321)
  // TODO lots of kind setting...
  // public void processWof(SourceFeature sf, FeatureCollector features) {
//     var sourceLayer = sf.getSourceLayer();
//     if (sourceLayer.equals("wof")) {
//       features.polygon(this.name()).setZoomRange(1, 6);
//     }
// 	// nvkelso (20230321)
// 	// TODO lots of kind setting...
// 	//   placetype:
// 	//     - neighbourhood
// 	//     - microhood
// 	//     - macrohood
// 	//     - borough
// 	// min_zoom: {col: min_zoom}
// 	// output:
// 	//   <<: *output_properties
// 	//   kind: {col: placetype}
//   }
// {# NOTE: only execute for zoom >= 11 #}
//
// SELECT
//     wof_id AS __id__,
//     {% filter geometry %}label_position{% endfilter %} AS __geometry__,
//
//     COALESCE(to_jsonb(l10n_name), '{}'::jsonb) ||
//     jsonb_build_object(
//       'name', name,
//       'source', 'whosonfirst.org',
//       'mz_n_photos', n_photos,
//       'area', area,
//       'is_landuse_aoi', is_landuse_aoi,
//       'placetype', wof_np.placetype_string,
//       'wikidata', wikidata
//     ) AS __properties__,
//
//     jsonb_build_object(
//       'min_zoom', min_zoom,
//       'max_zoom', max_zoom
//     ) AS __places_properties__
//
// FROM wof_neighbourhood wof_n
//
// INNER JOIN wof_neighbourhood_placetype wof_np ON wof_n.placetype = wof_np.placetype_code


  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // TODO
    // This should be 128 px buffer

    return items;
  }
}
