package com.protomaps.basemap.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.protomaps.basemap.feature.FeatureId;
import com.protomaps.basemap.names.OsmNames;
import java.util.List;

public class Pois implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {

  @Override
  public String name() {
    return "pois";
  }

  // TODO (nvkelso 2023-03-21)
  // 1.  add_iata_code_to_airports
  // 2.  normalize_tourism_kind
  // 3.  normalize_social_kind
  // 4.  normalize_medical_kind
  // 5.  add_uic_ref
  // 6.  remove_zero_area
  // 7.  make_representative_point
  // 8.  height_to_meters
  // 9.  pois_capacity_int
  // 10. pois_direction_int
  // 11. major_airport_detector
  // 12. elevation_to_meters
  // 13. normalize_operator_values
  // 14. perform vectordatasource.transform.remove_duplicate_features when name, kind are same
  // 15. perform vectordatasource.transform.merge_duplicate_stations
  // 16. perform vectordatasource.transform.normalize_station_properties
  // 17. perform vectordatasource.transform.rank_features for stations
  // 18. perform vectordatasource.transform.rank_features on peak, volcano
  // 19. perform vectordatasource.transform.remove_duplicate_features when name is same (why with 14?)
  // 20. perform vectordatasource.transform.update_parenthetical_properties for closed and historical
  //     - This should be in process features
  // 21. vectordatasource.transform.keep_n_features for peak, volcano at mid zooms (see Physical Points)
  //     - look at "label grid" functionality in planetiler (exists)
  // 22. vectordatasource.transform.drop_properties_with_prefix mz_ and tz_
  // 23. vectordatasource.transform.add_collision_rank

  // TODO (nvkelso 2023-04-04)
  //      These are inline below, but should be parametarized
//     - &transit_properties
//        mz_transit_score: {col: mz_transit_score}
//        mz_transit_root_relation_id: {col: mz_transit_root_relation_id}
//        train_routes: {col: train_routes}
//        subway_routes: {col: subway_routes}
//        light_rail_routes: {col: light_rail_routes}
//        tram_routes: {col: tram_routes}
//     - &health_facility_type_kind_detail
//        kind_detail:
//                case:
//                - when: {'health_facility:type': [CSCom, chemist_dispensing, clinic, counselling_centre, dispensary, first_aid, health_center, health_centre, hospital, laboratory, medical_clinic, office, pharmacy]}
//        then: {col: 'health_facility:type'}
//        // common properties for {bi|motor}cycle parking
//      - &cycle_parking_properties
//        access: {col: access}
//        operator: {col: operator}
//        capacity: {col: capacity}
//        covered:
//                case:
//                - when:
//        covered: 'yes'
//        then: true
//                - when:
//        covered: 'no'
//        then: false
//        fee:
//                case:
//                - when:
//        fee: true
//        not:
//        fee: ['no', 'Free', 'free', '0', 'No', 'none']
//        then: true
//                - else: false
//        cyclestreets_id: {col: cyclestreets_id}
//        maxstay: {col: maxstay}
//        surveillance:
//                case:
//                - when:
//        surveillance: true
//        not:
//        surveillance: ['no', 'none']
//        then: true
//
//                - &tier1_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 3, 50000000000 ]
//                - [ 4, 25000000000 ]
//                - [ 5, 10000000000 ]
//                - [ 6,   150000000 ]
//                - [ 7,   100000000 ]
//                - [ 8,    10000000 ]
//                - [ 9,     5000000 ]
//                - [ 10,    1000000 ]
//                - [ 11,     500000 ]
//                - [ 12,     200000 ]
//                - [ 13,     100000 ]
//                - [ 14,      50000 ]
//                - [ 15,       2000 ]
//                - [ 16,       1000 ]
//        default: 17
//                - &tier2_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 4, 1000000000 ]
//                - [ 5, 1000000000 ]
//                - [ 6,  150000000 ]
//                - [ 7,  100000000 ]
//                - [ 8,   10000000 ]
//                - [ 9,    5000000 ]
//                - [ 10,   1000000 ]
//                - [ 11,    500000 ]
//                - [ 12,    250000 ]
//                - [ 13,    100000 ]
//                - [ 14,     50000 ]
//                - [ 15,      2000 ]
//        default: 16
//                - &tier3_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 8,  10000000 ]
//                - [ 9,   5000000 ]
//                - [ 10,  1000000 ]
//                - [ 11,   500000 ]
//                - [ 12,   200000 ]
//                - [ 13,   100000 ]
//                - [ 14,    50000 ]
//                - [ 15,     2000 ]
//        default: 16
//                - &tier4_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 10, 1000000 ]
//                - [ 11,  500000 ]
//                - [ 12,  200000 ]
//                - [ 13,  100000 ]
//                - [ 14,   50000 ]
//                - [ 15,    2000 ]
//        default: 16
//                - &tier5_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 10, 1000000 ]
//                - [ 11,  400000 ]
//                - [ 12,  200000 ]
//                - [ 13,   50000 ]
//                - [ 14,   20000 ]
//                - [ 15,    2000 ]
//        default: 16
//                - &tier6_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 12, 500000 ]
//                - [ 13, 100000 ]
//                - [ 14,  50000 ]
//                - [ 15,   5000 ]
//        default: 16
//                - &small_parks_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 8,   400000000 ]
//                - [ 9,    40000000 ]
//                - [ 10,   10000000 ]
//                - [ 11,    4000000 ]
//                - [ 12,    2000000 ]
//                - [ 13,     200000 ]
//                - [ 14,     100000 ]
//                - [ 15,      10000 ]
//        default: 16
//                - &green_areas_min_zoom
//        lookup:
//        key: { col: way_area }
//        op: '>='
//        table:
//                - [ 6,  5000000000 ]
//                - [ 7,  2500000000 ]
//                - [ 8,  1000000000 ]
//                - [ 9,   100000000 ]
//                - [ 10,   50000000 ]
//                - [ 11,   25000000 ]
//                - [ 12,    5000000 ]
//                - [ 13,     200000 ]
//                - [ 14,      50000 ]
//                - [ 15,      10000 ]
//                - [ 16,       1000 ]
//        default: 17
//                - &us_forest_service
//              - United States Forest Service
//              - US Forest Service
//              - U.S. Forest Service
//              - USDA Forest Service
//              - United States Department of Agriculture
//              - US National Forest Service
//              - United State Forest Service
//              - U.S. National Forest Service
//        - &us_parks_service
//              - United States National Park Service
//              - National Park Service
//              - US National Park Service
//              - U.S. National Park Service
//              - US National Park service
//        - &not_national_park_protection_title
//              - Conservation Area
//              - Conservation Park
//              - Environmental use
//              - Forest Reserve
//              - National Forest
//              - National Wildlife Refuge
//              - Nature Refuge
//              - Nature Reserve
//              - Protected Site
//              - Provincial Park
//              - Public Access Land
//              - Regional Reserve
//              - Resources Reserve
//              - State Forest
//              - State Game Land
//              - State Park
//              - Watershed Recreation Unit
//              - Wild Forest
//              - Wilderness Area
//              - Wilderness Study Area
//              - Wildlife Management
//              - Wildlife Management Area
//              - Wildlife Sanctuary
//        // allowlist of religions to use in kind_detail (do not use for plain
//        // religion tag)
//        - &religion_kind_detail
//        kind_detail:
//                case:
//                - when:
//        religion:
//                - animist
//                    - bahai
//                    - buddhist
//                    - caodaism
//                    - catholic
//                    - christian
//                    - confucian
//                    - hindu
//                    - jain
//                    - jewish
//                    - multifaith
//                    - muslim
//                    - pagan
//                    - pastafarian
//                    - scientologist
//                    - shinto
//                    - sikh
//                    - spiritualist
//                    - taoist
//                    - tenrikyo
//                    - unitarian_universalist
//                    - voodoo
//                    - yazidi
//                    - zoroastrian
//        then: {col: religion}
//        - &sport_kind_detail
//        kind_detail:
//                case:
//                - when:
//        sport:
//                - 10pin
//                    - 9pin
//                    - american_football
//                    - archery
//                    - athletics
//                    - badminton
//                    - baseball
//                    - basketball
//                    - beachvolleyball
//                    - billiards
//                    - bmx
//                    - boules
//                    - bowls
//                    - canoe
//                    - chess
//                    - climbing
//                    - cricket
//                    - cricket_nets
//                    - cycling
//                    - equestrian
//                    - exercise
//                    - field_hockey
//                    - fitness
//                    - football
//                    - free_flying
//                    - futsal
//                    - gaelic_games
//                    - golf
//                    - gymnastics
//                    - handball
//                    - hockey
//                    - horse_racing
//                    - ice_hockey
//                    - ice_skating
//                    - karting
//                    - model_aerodrome
//                    - motocross
//                    - motor
//                    - multi
//                    - netball
//                    - padel
//                    - pelota
//                    - rugby
//                    - rugby_league
//                    - rugby_union
//                    - running
//                    - scuba_diving
//                    - shooting
//                    - skateboard
//                    - skating
//                    - skiing
//                    - soccer
//                    - soccer;basketball
//                    - softball
//                    - swimming
//                    - table_tennis
//                    - team_handball
//                    - tennis
//                    - trampoline
//                    - volleyball
//                    - yoga
//        then: {col: sport}
//        - &cuisine_kind_detail
//        kind_detail:
//                case:
//                - when:
//        cuisine:
//                - american
//                    - asian
//                    - barbecue
//                    - breakfast
//                    - burger
//                    - cake
//                    - chicken
//                    - chinese
//                    - coffee_shop
//                    - crepe
//                    - donut
//                    - fish
//                    - fish_and_chips
//                    - french
//                    - friture
//                    - georgian
//                    - german
//                    - greek
//                    - ice_cream
//                    - indian
//                    - international
//                    - italian
//                    - japanese
//                    - kebab
//                    - korean
//                    - lebanese
//                    - local
//                    - mediterranean
//                    - mexican
//                    - noodle
//                    - pizza
//                    - ramen
//                    - regional
//                    - sandwich
//                    - seafood
//                    - spanish
//                    - steak_house
//                    - sushi
//                    - tapas
//                    - thai
//                    - turkish
//                    - vegetarian
//                    - vietnamese
//        then: {col: cuisine}
//              - when:
//        cuisine:
//                - italian_pizza
//                    - italian;pizza
//        then: italian
//              - when:
//        cuisine:
//                - pizza;italian
//        then: pizza
//        - &no_name_okay
//              - aerialway: pylon
//              - aeroway: [ gate, helipad ]
//                - amenity:
//                - atm
//                - bbq
//                - bench
//                - bicycle_parking
//                - bicycle_rental
//                - bicycle_repair_station
//                - boat_storage
//                - bureau_de_change
//                - car_rental
//                - car_sharing
//                - car_wash
//                - charging_station
//                - customs
//                - drinking_water
//                - fuel
//                - harbourmaster
//                - hunting_stand
//                - karaoke_box
//                - life_ring
//                - money_transfer
//                - motorcycle_parking
//                - parking
//                - picnic_table
//                - post_box
//                - ranger_station
//                - recycling
//                - sanitary_dump_station
//                - shelter
//                - shower
//                - taxi
//                - telephone
//                - toilets
//                - waste_basket
//                - waste_disposal
//                - water_point
//                - watering_place
//              - barrier:
//                - block
//                - bollard
//                - border_control
//                - cattle_grid
//                - chain
//                - cycle_barrier
//                - gate
//                - kissing_gate
//                - lift_gate
//                - stile
//                - swing_gate
//                - toll_booth
//              - emergency:
//                - defibrillator
//                - fire_hydrant
//                - lifeguard_tower
//                - phone
//              - harbour: harbour_master
//              - highway: [ bus_stop, elevator, ford, turning_circle, turning_loop, mini_roundabout, motorway_junction, platform,
//        rest_area, street_lamp, traffic_signals, trailhead ]
//                - historic: [landmark, wayside_cross]
//                - landuse: quarry
//              - leisure: [ dog_park, firepit, fishing, pitch, playground, slipway, swimming_area ]
//                - lock: yes
//              - man_made: [ adit, communications_tower, crane, mast, mineshaft, obelisk, observatory,
//        offshore_platform, petroleum_well, power_wind, telescope, water_tower,
//        water_well, watermill, windmill ]
//                - military: bunker
//              - mooring: true
//                - natural: [ cave_entrance, peak, volcano, geyser, hot_spring, rock, saddle,
//        stone, spring, tree, waterfall ]
//                - power: [ pole, tower, generator ]
//                - public_transport: [ platform, stop_area ]
//                - railway: [ halt, level_crossing, platform, stop, subway_entrance, tram_stop ]
//                - "health_facility:type": [field_hospital, health_centre]
//                - "seamark:building:function": harbour_master
//              - "seamark:small_craft_facility:category": fuel_station
//              - icn_ref: true
//                - iwn_ref: true
//                - lcn_ref: true
//                - lwn_ref: true
//                - ncn_ref: true
//                - nwn_ref: true
//                - rcn_ref: true
//                - rwn_ref: true
//                - whitewater: [ egress, hazard, put_in, put_in;egress, rapid ]
//                - tourism: [ alpine_hut, information, picnic_site, viewpoint, wilderness_hut ]
//                - waterway:
//                - boat_lift
//                - boatyard
//                - dam
//                - fuel
//                - lock
//                - sanitary_dump_station
//                - waterfall

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    var sourceLayer = sourceFeature.getSourceLayer();
    //var name = "";
    String kind = "";
    String kind_detail = "";
    Integer feature_min_zoom = 20;
    Integer name_min_zoom = 20;
    Integer theme_min_zoom = 20;
    Integer theme_max_zoom = 20;

    //
    // NOTE: these globals are shared between the POIs and landuse. if you make an
    // update here, please remember to also change it there.
    //

    // common POI properties
    var mooring = "";
    var protect_class = "";
    var operator = "";
    var religion = "";
    var denomination = "";
    var sport = "";
    var ref = "";
    var shield_text = "";
    var attraction = "";
    var zoo = "";
    var exit_to = "";
    var wikidata_id = "";
    var direction = "";
    var elevation = "";
    var height = "";
    var sanitary_dump_station = "";

    // common-optional properties for transit POI features
    // eg &transit_properties
    var mz_transit_score = "";
    var mz_transit_root_relation_id = "";
    var train_routes = "";
    var subway_routes = "";
    var light_rail_routes = "";
    var tram_routes = "";
    var state = "";

    // common-optional properties for cycling features
    var walking_network = "";
    var bicycle_network = "";

    // common-optional properties for {bi|motor}cycle parking POI features
    // eg &cycle_parking_properties = "";
    var access = "";
    var operator_var = "";
    var network = "";
    var capacity = "";
    Boolean covered = false;
    Boolean fee = false;
    var cyclestreets_id = "";
    var maxstay = "";
    Boolean surveillance = false;

    // charging stations
    Boolean bicycle = false;
    Boolean car = false;
    Boolean truck = false;
    Boolean scooter = true;

    // Feature must have a name and be either a point or a polygon (to derive a point)
    if( !sourceFeature.canBeLine() && (sourceFeature.isPoint() || sourceFeature.canBePolygon()) ) {

      // There are actually a few POIs that are allowed to not have a name, in Tilezen these are PONIs

      // TODO (nvkelso) 2023-04-05
      //      This one is very important!

      // remove things without a name, unless they're in the list of things for which
      // no name is okay.
      // - &no_name_okay
      //  - filter:
      //      - name: false
      //      - not:
      //          any: *no_name_okay
//      if( (sourceFeature.getTag( "name") != null  || sourceFeature.getTag( "name:en") != null ||
//              sourceFeature.getTag("name:de") != null || sourceFeature.getTag("name:es") != null
//      ) &&
//              // but not when any: *no_name_okay
//              ! ( sourceFeature.hasTag("aerialway", "pylon") ||
//                      sourceFeature.hasTag("aeroway", "gate", "helipad") ||
//                      sourceFeature.hasTag("amenity", "atm", "bbq", "bench", "bicycle_parking", "bicycle_rental", "bicycle_repair_station", "boat_storage", "bureau_de_change", "car_rental", "car_sharing", "car_wash", "charging_station", "customs", "drinking_water", "fuel", "harbourmaster", "hunting_stand", "karaoke_box", "life_ring", "money_transfer", "motorcycle_parking", "parking", "picnic_table", "post_box", "ranger_station", "recycling", "sanitary_dump_station", "shelter", "shower", "taxi", "telephone", "toilets", "waste_basket", "waste_disposal", "water_point", "watering_place") ||
//                      sourceFeature.hasTag("barrier", "block", "bollard", "border_control", "cattle_grid", "chain", "cycle_barrier", "gate", "kissing_gate", "lift_gate", "stile", "swing_gate", "toll_booth") ||
//                      sourceFeature.hasTag("emergency", "defibrillator", "fire_hydrant", "lifeguard_tower", "phone") ||
//                      sourceFeature.hasTag("harbour", "harbour_master") ||
//                      sourceFeature.hasTag("highway", "bus_stop", "elevator", "ford", "turning_circle", "turning_loop", "mini_roundabout", "motorway_junction", "platform", "rest_area", "street_lamp", "traffic_signals", "trailhead") ||
//                      sourceFeature.hasTag("historic", "landmark", "wayside_cross") ||
//                      sourceFeature.hasTag("landuse", "quarry") ||
//                      sourceFeature.hasTag("leisure", "dog_park", "firepit", "fishing", "pitch", "playground", "slipway", "swimming_area") ||
//                      sourceFeature.hasTag("lock", "yes") ||
//                      sourceFeature.hasTag("man_made", "adit", "communications_tower", "crane", "mast", "mineshaft", "obelisk", "observatory", "offshore_platform", "petroleum_well", "power_wind", "telescope", "water_tower", "water_well", "watermill", "windmill") ||
//                      sourceFeature.hasTag("military", "bunker") ||
//                      sourceFeature.hasTag("mooring", "true") ||
//                      sourceFeature.hasTag("natural", "cave_entrance", "peak", "volcano", "geyser", "hot_spring", "rock", "saddle", "stone", "spring", "tree", "waterfall") ||
//                      sourceFeature.hasTag("power", "pole", "tower", "generator") ||
//                      sourceFeature.hasTag("public_transport", "platform", "stop_area") ||
//                      sourceFeature.hasTag("publrailwayic_transport", "halt", "level_crossing", "platform", "stop", "subway_entrance", "tram_stop") ||
//                      sourceFeature.hasTag("health_facility:type", "field_hospital", "health_centre") ||
//                      sourceFeature.hasTag("seamark:building:function", "harbour_master") ||
//                      sourceFeature.hasTag("seamark:small_craft_facility:category", "fuel_station") ||
//                      sourceFeature.hasTag("icn_ref") ||
//                      sourceFeature.hasTag("iwn_ref") ||
//                      sourceFeature.hasTag("lwn_ref") ||
//                      sourceFeature.hasTag("ncn_ref") ||
//                      sourceFeature.hasTag("nwn_ref") ||
//                      sourceFeature.hasTag("rcn_ref") ||
//                      sourceFeature.hasTag("rwn_ref") ||
//                      sourceFeature.hasTag("whitewater", "egress", "hazard", "put_in", "put_in;egress", "rapid") ||
//                      sourceFeature.hasTag("tourism", "alpine_hut", "information", "picnic_site", "viewpoint", "wilderness_hut") ||
//                      sourceFeature.hasTag("waterway", "boat_lift", "boatyard", "dam", "fuel", "lock", "sanitary_dump_station", "waterfall")
//              )
//      ) {
//        kind = "";
//      }

      // remove disused things, they're not real POIs any more
      if( sourceFeature.hasTag("disused") &&
              ! sourceFeature.hasTag("disused", "no")
      ) {
        kind = "";
      }

      // remove abandoned or disused watermills
      else if( sourceFeature.hasTag("abandoned:man_made", "watermill") ||
              ( sourceFeature.hasTag("watermill:disused") &&
                      !sourceFeature.hasTag("watermill:disused", "no") )
      ) {
        kind = "";
      }

      ////////////////////////////////////////////////////////////
      // NOT IN ANY TIER
      //
      // These depend on being run before the rule for tourism=attraction,
      // otherwise that rule will trigger first. So these have been moved
      // up here.
      ////////////////////////////////////////////////////////////

      // if it's a ruin, then the ruin would be the attraction?
      else if( sourceFeature.hasTag("ruins", "yes") &&
              sourceFeature.hasTag("man_made", "lighthouse", "watermill", "windmill")
      ) {
        kind = "ruins";
        kind_detail = sourceFeature.getString("man_made");

        if( sourceFeature.hasTag("tourism", "attraction") ) {
          // feature_min_zoom = 14;
          theme_min_zoom = 13;
          theme_max_zoom = 15;
        } else {
          // feature_min_zoom = 17;
          theme_min_zoom = 15;    // MAX_ZOOM
          theme_max_zoom = 15;
        }
      }
      // attractions which aren't ruined
      else if( sourceFeature.hasTag("man_made", "lighthouse", "watermill", "windmill") ) {
        kind = sourceFeature.getString("man_made");

        if( sourceFeature.hasTag("tourism", "attraction") ) {
          // feature_min_zoom = 14;
          theme_min_zoom = 13;
          theme_max_zoom = 15;
        } else {
          // feature_min_zoom = 17;
          theme_min_zoom = 15;    // MAX_ZOOM
          theme_max_zoom = 15;
        }
      }
      else if( sourceFeature.hasTag("man_made", "observatory") ) {
        kind = "observatory";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // TIER 2 OVERRIDES
      //
      // These are things which are "more specific" than things in
      // tier 1, so they should match first.
      ////////////////////////////////////////////////////////////

      else if( sourceFeature.hasTag("boundary", "national_park") &&
              sourceFeature.hasTag("protect_class", "6") &&
              sourceFeature.hasTag("protection_title", "National Forest")
      ) {
        kind = "forest";
        // min_zoom: { max: [ 7, *tier2_min_zoom ] }
        // feature_min_zoom = 7;
        theme_min_zoom = 7;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("boundary", "national_park") &&
              sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
      ) {
        kind = "forest";
        // tier = 2;
        // min_zoom: { max: [ 8, *tier2_min_zoom ] }
        // feature_min_zoom = 8;
        theme_min_zoom = 8;
        theme_max_zoom = 15;
      }
      else if( ( sourceFeature.hasTag("leisure", "park") ||
              sourceFeature.hasTag("landuse", "park")
      ) &&
              sourceFeature.hasTag("park:type", "state_recreational_area")
      ) {
        kind = "park";
        // tier = 2;
        // min_zoom: { max: [ 9, *tier2_min_zoom ] }
        // feature_min_zoom = 9;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("boundary", "national_park") &&
              ( sourceFeature.hasTag("protect_class", "6") ||
                      sourceFeature.hasTag("designation", "area_of_outstanding_natural_beauty")
              )
      ) {
        kind = "park";
        // min_zoom: { max: [ 9, *tier2_min_zoom ] }
        // tier = 2;
        // feature_min_zoom = 9;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      else if( ( sourceFeature.hasTag("boundary:type", "protected_area") ||
              sourceFeature.hasTag("boundary", "protected_area") ) &&
              sourceFeature.hasTag("leisure", "nature_reserve") &&
              sourceFeature.hasTag("protect_class", "4", "5") &&
              ! sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
      ) {
        kind = "nature_reserve";
        // min_zoom: { max: [ 9, *green_areas_min_zoom ] }
        // feature_min_zoom = 9;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // TIER 6 OVERRIDES
      //
      // These are things which are "more specific" than things in
      // tier 1, so they should match first.
      ////////////////////////////////////////////////////////////

      // common
      else if( sourceFeature.hasTag("boundary:type", "protected_area") &&
              sourceFeature.hasTag("leisure", "common") &&
              sourceFeature.hasTag("protect_class", "5") &&
              ! (sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service") ||
                      sourceFeature.hasTag("operator", "United States National Park Service", "National Park Service", "US National Park Service", "U.S. National Park Service", "US National Park service")
              )
      ) {
        kind = "common";
        // tier = 6;
        // min_zoom: *tier6_min_zoom
        // feature_min_zoom = *tier6_min_zoom;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // TIER 1
      ////////////////////////////////////////////////////////////

      else if( sourceFeature.hasTag("historic", "battlefield") &&
              ! sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
      ) {
        kind = "battlefield";
        // tier = 1;
        // min_zoom: { max: [ 10, *tier1_min_zoom ] }
        // feature_min_zoom = 10;
        theme_min_zoom = 10;
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
        // min_zoom: { max: [ 5, *tier1_min_zoom ] }
        // feature_min_zoom = 5;
        theme_min_zoom = 5;
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // TIER 2
      ////////////////////////////////////////////////////////////

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
        // min_zoom: { max: [ 10, *tier2_min_zoom ] }
        // feature_min_zoom = 10;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // mid-tier 2 overrides
      //
      // the boundary=national_park appears to get used for a ton of
      // stuff that isn't actually a national park, so we match a
      // few things in advance which seem to be more specific.
      ////////////////////////////////////////////////////////////

      else if( sourceFeature.hasTag("natural", "wetland") ) {
        kind = "wetland";
        // tier = 4;
        // feature_min_zoom = { max: [ 15, *tier4_min_zoom ] };
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("wetland", "bog", "fen", "mangrove", "marsh", "mud", "reedbed", "saltern", "saltmarsh", "string_bog", "swamp", "tidalflat", "wet_meadow") ) {
          kind_detail = sourceFeature.getString("wetland");
        }
      }

      ////////////////////////////////////////////////////////////
      // TIER 2 (continued...)
      ////////////////////////////////////////////////////////////

      else if( sourceFeature.hasTag("leisure", "park") ||
              sourceFeature.hasTag("landuse", "park")  ||
              sourceFeature.hasTag("boundary", "national_park")
      ) {
        kind = "park";
        // tier = 2;
        // min_zoom: *small_parks_min_zoom
        // feature_min_zoom = *small_parks_min_zoom;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }

      // forests
      else if( sourceFeature.hasTag("landuse", "forest") &&
              sourceFeature.hasTag("protect_class", "6")
      ) {
        kind = "forest";
        // tier = 2;
        // min_zoom: { max: [ 8, *tier2_min_zoom ] }
        // feature_min_zoom = 8;
        theme_min_zoom = 8;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("landuse", "forest") &&
              sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
      ) {
        kind = "forest";
        // tier = 2;
        // min_zoom: { max: [ 8, *tier2_min_zoom ] }
        // feature_min_zoom = 8;
        theme_min_zoom = 8;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("landuse", "forest") ) {
        kind = "forest";
        // min_zoom: { max: [ 10, *tier2_min_zoom ] }
        // feature_min_zoom = 10;
        theme_min_zoom = 10;
        theme_max_zoom = 15;
      }
      // nature reserves
      else if( sourceFeature.hasTag("leisure", "nature_reserve") ) {
        kind = "nature_reserve";
        // min_zoom: { max: [ 9, *small_parks_min_zoom ] }
        // feature_min_zoom = 9;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      // Bureau of Land Management protected areas
      else if( sourceFeature.hasTag("boundary", "protected_area") &&
              sourceFeature.hasTag("operator", "BLM", "US Bureau of Land Management")
      ) {
        kind = "protected_area";
        // min_zoom: { max: [ 9, *small_parks_min_zoom ] }
        // feature_min_zoom = 9;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      // scientific protected areas?
      else if( sourceFeature.hasTag("boundary", "protected_area") &&
              sourceFeature.hasTag("protect_class", "1", "1a", "1b")
      ) {
        kind = "protected_area";
        // min_zoom: { max: [ 9, *small_parks_min_zoom ] }
        // feature_min_zoom = 9;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      // protected areas
      else if( sourceFeature.hasTag("boundary", "protected_area") ) {
        kind = "protected_area";
        // min_zoom: { max: [ 7, *tier2_min_zoom ] }
        // feature_min_zoom = 7;
        theme_min_zoom = 7;
        theme_max_zoom = 15;
      }
      // woods
      else if( sourceFeature.hasTag("landuse", "wood") &&
              // *us_forest_service YAML indirection
              sourceFeature.hasTag("operator", "United States Forest Service", "US Forest Service", "U.S. Forest Service", "USDA Forest Service", "United States Department of Agriculture", "US National Forest Service", "United State Forest Service", "U.S. National Forest Service")
      ) {
        kind = "wood";
        // min_zoom: { max: [ 9, *tier2_min_zoom ] }
        // feature_min_zoom = 9;
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("landuse", "wood") ) {
        kind = "wood";
        // tier = 2;
        // min_zoom: 17
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // farm
      else if( sourceFeature.hasTag("landuse", "farm") ) {
        kind = "farm";
        // min_zoom: { max: [ 15, *tier2_min_zoom ] }
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }

      // urban, rural, residential, farmland - no POIs

      ////////////////////////////////////////////////////////////
      // TIER 3
      ////////////////////////////////////////////////////////////

      // (military) airfield
      else if( sourceFeature.hasTag("military", "airfield") ||
              // backfill for things tagged primarily as aeroways, but we want to show as military.
              ( sourceFeature.hasTag("aeroway", "aerodrome") &&
                      sourceFeature.hasTag("aerodrome:type", "military")
              )
      ) {
        kind = "airfield";
        // tier = 3;
        // min_zoom: { min: [ { max: [ 8, { sum: [ { col: zoom }, 2 ] }, *tier3_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ 8, { sum: [ { col: zoom }, 2 ] }, *tier3_min_zoom ] }, 14 ] };
        theme_min_zoom = 8;
        theme_max_zoom = 15;
      }

      // aerodrome
      else if( sourceFeature.hasTag("aeroway", "aerodrome", "airport", "heliport") &&
              ! sourceFeature.hasTag("aerodrome:type", "military")
      ) {
        kind = sourceFeature.getString("aeroway");
        // tier: 3
        // feature_min_zoom = { min: [ { max: [ { sum: [ { col: zoom }, 4.12 ] }, *tier3_min_zoom ] }, 13 ] };
        theme_min_zoom = 8;
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
      // naval_base
      else if( sourceFeature.hasTag("military", "naval_base") ) {
        kind = "naval_base";
        // tier = 3;
        // min_zoom: { min: [ { max: [ 8, { sum: [ { col: zoom }, 2 ] }, *tier3_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ 8, { sum: [ { col: zoom }, 2 ] }, *tier3_min_zoom ] }, 14 ] };
        theme_min_zoom = 8;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("military", "range") ) {
        kind = "range";
        // tier = 3;
        // min_zoom: { clamp: { max: 16, min: 11, value: { sum: [ { col: zoom }, 1 ] } } }
        // feature_min_zoom = { clamp: { max: 16, min: 11, value: { sum: [ { col: zoom }, 1 ] } } };
        theme_min_zoom = 11;
        theme_max_zoom = 15;
      }
      // danger_area
      else if( sourceFeature.hasTag("military", "danger_area") ) {
        kind = "danger_area";
        // tier = 3;
        // min_zoom: { clamp: { max: 16, min: 11, value: { sum: [ { col: zoom }, 1 ] } } }
        // feature_min_zoom = { clamp: { max: 16, min: 11, value: { sum: [ { col: zoom }, 1 ] } } };
        theme_min_zoom = 11;
        theme_max_zoom = 15;
      }
      // bunker
      else if( sourceFeature.hasTag("military", "bunker") ) {
        kind = "bunker";
        // feature_min_zoom = {case: [{when: {name: true}, then: 16}, {else: 18}]};
        if( sourceFeature.hasTag("name") ) {
          theme_min_zoom = 16;
        } else {
          theme_min_zoom = 18;
        }
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("bunker_type", "pillbox", "munitions", "gun_emplacement", "hardened_aircraft_shelter", "blockhouse", "technical", "mg_nest", "missile_silo") ) {
          kind_detail = sourceFeature.getString("bunker_type");
        }
      }
      // military
      else if( sourceFeature.hasTag("landuse", "military") ) {
        kind = "military";
        // tier = 3;
        // min_zoom: *small_parks_min_zoom
        // feature_min_zoom = *small_parks_min_zoom;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }

      // university
      else if( sourceFeature.hasTag("amenity", "university") ) {
        kind = "university";
        // tier = 3;
        // min_zoom: // see below
        // feature_min_zoom = // see below
        theme_min_zoom = 11;
        theme_max_zoom = 15;

        // TODO
        //  min_zoom:
        //    lookup:
        //      key: { col: way_area }
        //      op: '>='
        //      table:
        //        - [ 10, 10000000 ]
        //        - [ 11,  1000000 ]
        //        - [ 12,   500000 ]
        //        - [ 13,   100000 ]
        //        - [ 14,    50000 ]
        //        - [ 15,     2000 ]
        //      default: 16
      }

      // college
      else if( sourceFeature.hasTag("amenity", "college") ) {
        kind = "college";
        // tier = 3;
        // min_zoom: // see below
        // feature_min_zoom = // see below
        theme_min_zoom = 13;
        theme_max_zoom = 15;

        // TODO
        //  min_zoom:
        //    lookup:
        //      key: { col: way_area }
        //      op: '>='
        //      table:
        //        - [ 12,   500000 ]
        //        - [ 13,   100000 ]
        //        - [ 14,    50000 ]
        //        - [ 15,     2000 ]
        //      default: 16
      }

      ////////////////////////////////////////////////////////////
      // TIER 4
      ////////////////////////////////////////////////////////////

      // breakwater - no POIs
      // bridge - no POIs

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

        // TODO
        // min_zoom:
        //    lookup:
        //      key: { col: way_area }
        //      op: '>='
        //      table:
        //        - [ 12, 3000000 ]
        //        - [ 13,  100000 ]
        //        - [ 14,   50000 ]
        //        - [ 15,    2000 ]
        //      default: 16

        // allowlist of religions to use in kind_detail
        // (but do not use for plain religion tag)
        if( sourceFeature.hasTag("religion", "animist", "bahai", "buddhist", "caodaism", "catholic", "christian", "confucian", "hindu", "jain", "jewish", "multifaith", "muslim", "pagan", "pastafarian", "scientologist", "shinto", "sikh", "spiritualist", "taoist", "tenrikyo", "unitarian_universalist", "voodoo", "yazidi", "zoroastrian") ) {
          kind_detail = sourceFeature.getString("religion");
        }

        // pass thru without sanitizing (v2 task)
        denomination = sourceFeature.getString("denomination");
      }

      // commercial - no POI
      // cutline - no POI
      // dike - no POI}

      // generator
      else if( sourceFeature.hasTag("power", "generator") ) {
        kind = "generator";
        // tier = 4;
        // feature_min_zoom = { max: [15, *tier4_min_zoom ] };
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // allowlist common values
        //
        if( sourceFeature.hasTag("generator:source", "wind", "solar", "hydro", "oil", "gas", "coal", "biomass", "biogas", "diesel", "nuclear", "biofuel", "geothermal", "waste") ) {
          kind_detail = sourceFeature.getString("generator:source");
        }
        // remap photovoltaic -> solar, unless i'm missing something,
        // it's just a more specific way of saying the same thing.
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Dthermal
        else if( sourceFeature.hasTag("generator:source", "photovoltaic", "thermal") ) {
          kind_detail = "solar";
        }
        else if( sourceFeature.hasTag("generator:source", "photovoltaic", "thermal") ) {
          kind_detail = "solar";
        }
        // backfill with previously supported values
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Danaerobic_digestion
        else if( sourceFeature.hasTag("generator:method", "anaerobic_digestion") ) {
          kind_detail = "biomass";
        }
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Dbarrage
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Dstream
        else if( sourceFeature.hasTag("generator:method", "barrage", "stream") ) {
          kind_detail = "tidal";
        }
        // combustion - can't map; too generic!
        else if( sourceFeature.hasTag("generator:method", "fission") ) {
          kind_detail = "nuclear";
        }
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Dgasification
        else if( sourceFeature.hasTag("generator:method", "gasification") ) {
          // or "waste", but biomass is the more common tag
          kind_detail = "biomass";
        }
        else if( sourceFeature.hasTag("generator:method", "photovoltaic") ) {
          kind_detail = "solar";
        }
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Drun-of-the-river
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Dwater-pumped-storage
        // https://wiki.openstreetmap.org/wiki/Tag%3Agenerator%3Amethod%3Dwater-storage
        else if( sourceFeature.hasTag("generator:method", "run-of-the-river", "water-pumped-storage", "water-storage") ) {
          kind_detail = "hydro";
        }
        else if( sourceFeature.hasTag("generator:method", "wind_turbine") ) {
          kind_detail = "wind";
        }
        // fix up some common misspellings and alternative tag uses.
        else if( sourceFeature.hasTag("generator:method", "dam") ) {
          kind_detail = "hydro";
        }
        else if( sourceFeature.hasTag("generator:method", "solar", "solar_photovoltaic_panel", "photovoltaik", "solar_panel") ) {
          kind_detail = "solar";
        }
        else if( sourceFeature.hasTag("generator:method", "wind") ) {
          kind_detail = "wind";
        }
      }

      // golf_course
      else if( sourceFeature.hasTag("leisure", "golf_course") ) {
        kind = "golf_course";
        // tier = 4;
        // min_zoom: 15;
        // feature_min_zoom = 13;
        theme_min_zoom = 13;
        theme_max_zoom = 15;

        // TODO
        //   min_zoom:
        //     lookup:
        //       key: { col: way_area }
        //       op: '>='
        //       table:
        //         - [ 12, 2000000 ]
        //         - [ 13,  500000 ]
        //      default: 14
      }

      // groyne - no POI

      // field_hospital - seems more specific than hospital, so match it first.
      else if( sourceFeature.hasTag("health_facility:type", "field_hospital") ) {
        kind = "field_hospital";
        // tier = 4;
        // min_zoom: 15;
        // feature_min_zoom = 15;
        theme_min_zoom = 14;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }

      // hospital
      else if( sourceFeature.hasTag("amenity", "hospital") ) {
        kind = "hospital";
        // tier = 4;
        // min_zoom: { min: [ { max: [ { sum: [ { col: zoom }, 3.32 ] }, *tier4_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ { sum: [ { col: zoom }, 3.32 ] }, *tier4_min_zoom ] }, 14 ] }
        theme_min_zoom = 11;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }

      // industrial - no POI
      // pier - no POI

      // plant
      else if( sourceFeature.hasTag("power", "plant") ) {
        kind = "plant";
        // tier = 4;
        // min_zoom: { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }

      // railway - no POI

      // recreation_ground
      else if( sourceFeature.hasTag("landuse", "recreation_ground") ) {
        kind = "recreation_ground";
        // tier = 4;
        // min_zoom: { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }

      // retail - no POI}

      // sports_centre
      else if( sourceFeature.hasTag("leisure", "sports_centre") ) {
        if( sourceFeature.hasTag("sport", "fitness", "gym") ) {
          kind = "fitness";
        } else {
          kind = "sports_centre";
        }

        // tier = 4;
        // min_zoom: { max: [ 12, *tier4_min_zoom ] }
        // feature_min_zoom = 10
        theme_min_zoom = 10;
        theme_max_zoom = 15;

        if( ! sourceFeature.hasTag("sport", "fitness", "gym") ) {
          kind_detail = sourceFeature.getString("sport");
        }
      }
      // stadium
      else if( sourceFeature.hasTag("leisure", "horse_riding", "stadium") ) {
        kind = sourceFeature.getString("leisure");
        // tier = 4;
        // min_zoom: { min: [ { max: [ { sum: [ { col: zoom }, 2.3 ] }, *tier4_min_zoom ] }, 15 ] }
        // feature_min_zoom = { min: [ { max: [ { sum: [ { col: zoom }, 2.3 ] }, *tier4_min_zoom ] }, 15 ] };
        theme_min_zoom = 10;
        theme_max_zoom = 15;
      }
      // casino
      else if( sourceFeature.hasTag("amenity", "casino") ) {
        kind = "casino";
        // tier = 4;
        // min_zoom: { min: [ { max: [ { sum: [ { col: zoom }, 2.3 ] }, *tier4_min_zoom ] }, 15 ] }
        // feature_min_zoom = { min: [ { max: [ { sum: [ { col: zoom }, 2.3 ] }, *tier4_min_zoom ] }, 15 ] };
        theme_min_zoom = 10;
        theme_max_zoom = 15;
      }
      // substation
      else if( sourceFeature.hasTag("power", "substation") ) {
        kind = "substation";
        // tier = 4;
        // min_zoom = 16; // see below
        // feature_min_zoom = { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] };
        theme_min_zoom = 12;
        theme_max_zoom = 15;

        // TODO
        //   min_zoom:
        //     lookup:
        //       key: { col: way_area }
        //       op: '>='
        //       table:
        //         - [ 14, 200000 ]
        //         - [ 15,  50000 ]
        //         - [ 16,  10000 ]
        //       default: 17
      }

      // man_made=tower - no POI

      // wastewater_plant
      else if( sourceFeature.hasTag("man_made", "wastewater_plant") ) {
        kind = "wastewater_plant";
        // tier = 4;
        // min_zoom: { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      // works
      else if( sourceFeature.hasTag("man_made", "works") ) {
        kind = "works";
        // tier = 4;
        // min_zoom: { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      // water_works
      else if( sourceFeature.hasTag("man_made", "water_works") ) {
        kind = "water_works";
        // tier = 4;
        // min_zoom: { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ 12, { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 14 ] };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      // wildlife_park - no POI
      else if( sourceFeature.hasTag("zoo", "wildlife_park") ) {
        kind = "wildlife_park";
        // tier = 4;
        // min_zoom: 17
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // winter_sports
      else if( sourceFeature.hasTag("landuse", "winter_sports") ) {
        kind = "winter_sports";
        // tier = 4;
        // min_zoom: { min: [ { max: [ 10, { sum: [ { col: zoom }, 1 ] }, *tier4_min_zoom ] }, 13 ] }
        // feature_min_zoom = { min: [ { max: [ 10, { sum: [ { col: zoom }, 1 ] }, *tier4_min_zoom ] }, 13 ] };
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      // zoo
      else if( sourceFeature.hasTag("tourism", "zoo") ) {
        kind = "zoo";
        // tier = 4;
        // min_zoom: { min: [ { max: [ { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 13 ] }
        // feature_min_zoom = { min: [ { max: [ { sum: [ { col: zoom }, 3 ] }, *tier4_min_zoom ] }, 13 ] };
        theme_min_zoom = 9;
        theme_max_zoom = 15;
      }
      // other random landuse
      else if( sourceFeature.hasTag("landuse", "container_terminal", "port_terminal") ) {
        kind = sourceFeature.getString("landuse");
        // tier = 4;
        // min_zoom: { clamp: { min: 13, max: 16, value: { sum: [ { col: zoom }, 2.81 ] } } }
        // feature_min_zoom = { clamp: { min: 13, max: 16, value: { sum: [ { col: zoom }, 2.81 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("landuse", "shipyard") ) {
        kind = sourceFeature.getString("landuse");
        // min_zoom: { max: [ 15, *tier4_min_zoom ] }
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("landuse", "quay", "wharf") ) {
        kind = sourceFeature.getString("landuse");
        // tier = 4;
        // min_zoom: 16
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // seems odd quay is both landuse and man_made...
      else if( sourceFeature.hasTag("man_made", "quay") ) {
        kind = "quay";
        // tier = 4;
        // min_zoom: 16
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("waterway", "boatyard") ) {
        kind = sourceFeature.getString("waterway");
        // min_zoom: { max: [ 15, *tier4_min_zoom ] }
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // TIER 5
      ////////////////////////////////////////////////////////////}

      // aquarium
      else if( sourceFeature.hasTag("tourism", "aquarium") ) {
        kind = "aquarium";
        // tier = 5;
        // min_zoom: { min: [ { max: [ 14, { sum: [ { col: zoom }, 3.09 ] }, *tier5_min_zoom ] }, 17 ] }
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      // beach
      else if( sourceFeature.hasTag("natural", "beach") ) {
        kind = "beach";
        // tier = 5;
        // min_zoom: { min: [ { max: [ { sum: [ { col: zoom }, 3.2 ] }, *tier5_min_zoom ] }, 14 ] }
        // feature_min_zoom = { min: [ { max: [ { sum: [ { col: zoom }, 3.2 ] }, *tier5_min_zoom ] }, 14 ] };
        theme_min_zoom = 14;
        theme_max_zoom = 15;

        // allowlist most common surface values for beaches. i'm not quite sure i'd
        // call surface=grass a beach, but it's in the data..?
        if( sourceFeature.hasTag("surface", "grass", "gravel", "pebbles", "pebblestone", "rocky", "sand") ) {
          kind_detail = sourceFeature.getString("surface");
        }
      }

      // glacier - no POI

      // maze
      else if( sourceFeature.hasTag("attraction", "animal", "water_slide", "roller_coaster", "summer_toboggan", "carousel", "amusement_ride", "maze") ) {

        // no point changing this - it's already masked below any value from
        // tier5_min_zoom
        kind = sourceFeature.getString("attraction");
        // tier = 5;
        // min_zoom: 17
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // resort
      else if( sourceFeature.hasTag("leisure", "resort") ) {
        kind = "resort";
        // tier = 5;
        // min_zoom: { min: [ { max: [ 14, { sum: [ { col: zoom }, 5.32 ] }, *tier5_min_zoom ] }, 17 ] }
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      // theme_park (NOTE: also allow and normalise 'Theme Park' to deal with vandalism)
      else if( sourceFeature.hasTag("tourism", "theme_park", "Theme Park") ) {
        kind = "theme_park";
        // tier = 5;
        // min_zoom: { min: [ { max: [ 13, { sum: [ { col: zoom }, 6.32 ] }, *tier5_min_zoom ] }, 17 ] }
        // feature_min_zoom = 13;
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      // winery
      else if( sourceFeature.hasTag("tourism", "winery") ) {
        kind = "winery";
        // tier = 5;
        // min_zoom: { min: [ { max: [ 14, { sum: [ { col: zoom }, 2.85 ] }, *tier5_min_zoom ] }, 17 ] }
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      // obelisk - note that this takes precedence over artwork, monument and memorial!
      else if( sourceFeature.hasTag("man_made", "obelisk") ) {
        kind = "obelisk";
        // tier = 5;
        // min_zoom: 16; // see below
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        // keep information about whether this was a monument or memorial
        if( sourceFeature.hasTag("historic", "monument", "memorial") ) {
          kind_detail = sourceFeature.getString("historic");
        }

        // TODO
        //    min_zoom:
        //      lookup:
        //        key: { call: { func: mz_to_float_meters, args: [ { col: height } ] } }
        //        op: '>='
        //        table:
        //          - [ 14, 20 ]  / z14 if height >= 20m
        //          - [ 15, 10 ]  / z15 if height >= 10m
        //        default: 16
      }

      ////////////////////////////////////////////////////////////
      // TIER 6
      ////////////////////////////////////////////////////////////

      // allotments
      else if( sourceFeature.hasTag("landuse", "allotments") &&
              ! sourceFeature.hasTag("access", "private", "no")
      ) {
        kind = "allotments";
        // tier = 6;
        // min_zoom: { min: [ { max: [ 16, { col: zoom }, *tier6_min_zoom ] }, 17 ] }
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }

      // artwork, hanami
      else if( sourceFeature.hasTag("tourism", "artwork", "hanami", "trail_riding_station") ) {
        // already clamped below polygon area min
        kind = sourceFeature.getString("tourism");
        // tier = 6;
        // min_zoom: 17
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }

      // common - no POI

      // garden
      else if( sourceFeature.hasTag("leisure", "garden") &&
              ! sourceFeature.hasTag("access", "private", "no")
      ) {
        kind = "garden";
        // tier = 6;
        // min_zoom: { min: [ { max: [ 12, { col: zoom }, *tier6_min_zoom ] }, 17 ] }
        // feature_min_zoom = 12;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }

      // hedge - no POI
      // pedestrian - no POI

      // pitch - either has a name, or shows at a higher zoom for some sports
      else if( sourceFeature.hasTag("leisure", "pitch") &&
              ( sourceFeature.hasTag("name") && sourceFeature.getString("name") != null )
      ) {
        // already clamped below area min

        kind = "pitch";
        // tier: 6
        // min_zoom: 16
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("sport", "10pin", "9pin", "american_football", "archery", "athletics", "badminton", "baseball", "basketball", "beachvolleyball", "billiards", "bmx", "boules", "bowls", "canoe", "chess", "climbing", "cricket", "cricket_nets", "cycling", "equestrian", "exercise", "field_hockey", "fitness", "football", "free_flying", "futsal", "gaelic_games", "golf", "gymnastics", "handball", "hockey", "horse_racing", "ice_hockey", "ice_skating", "karting", "model_aerodrome", "motocross", "motor", "multi", "netball", "padel", "pelota", "rugby", "rugby_league", "rugby_union", "running", "scuba_diving", "shooting", "skateboard", "skating", "skiing", "soccer", "soccer;basketball", "softball", "swimming", "table_tennis", "team_handball", "tennis", "trampoline", "volleyball", "yoga") ) {
          kind_detail = sourceFeature.getString("sport");
        }
      }

      // pitch - only show no-name pitches for some smaller set of sports, and at a
      // higher zoom.
      else if( sourceFeature.hasTag("leisure", "pitch") ) {
        // already clamped below area min
        kind = "pitch";
        // tier: 6
        // min_zoom: 17
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("sport", "10pin", "9pin", "american_football", "archery", "athletics", "badminton", "baseball", "basketball", "beachvolleyball", "billiards", "bmx", "boules", "bowls", "canoe", "chess", "climbing", "cricket", "cricket_nets", "cycling", "equestrian", "exercise", "field_hockey", "fitness", "football", "free_flying", "futsal", "gaelic_games", "golf", "gymnastics", "handball", "hockey", "horse_racing", "ice_hockey", "ice_skating", "karting", "model_aerodrome", "motocross", "motor", "multi", "netball", "padel", "pelota", "rugby", "rugby_league", "rugby_union", "running", "scuba_diving", "shooting", "skateboard", "skating", "skiing", "soccer", "soccer;basketball", "softball", "swimming", "table_tennis", "team_handball", "tennis", "trampoline", "volleyball", "yoga") ) {
          kind_detail = sourceFeature.getString("sport");
        }
      }

      // place_of_worship
      else if( sourceFeature.hasTag("amenity", "place_of_worship") ) {
        kind = "place_of_worship";
        // tier = 6;
        // min_zoom: { min: [ { max: [ { sum: [ { mul: [2, {col: zoom}]}, -9.55 ] }, *tier6_min_zoom ] }, 17 ] }
        // feature_min_zoom = { min: [ { max: [ { sum: [ { mul: [2, {col: zoom}]}, -9.55 ] }, *tier6_min_zoom ] }, 17 ] };
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }

      // playground
      else if( sourceFeature.hasTag("leisure", "playground") &&
              ( sourceFeature.hasTag("name") && sourceFeature.getString("name") != null )
      ) {
        // already clamped below area limit
        kind = "playground";
        // tier = 6;
        // min_zoom: 17
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "playground") ) {
        kind = "playground";
        // tier = 6;
        // min_zoom: 18
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // school
      else if( sourceFeature.hasTag("amenity", "school", "kindergarten") ) {
        kind = sourceFeature.getString("amenity");
        // tier = 6;
        // min_zoom: 17
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        // TODO
        //    min_zoom:
        //      lookup:
        //        key: { col: way_area }
        //        op: '>='
        //        table:
        //          - [ 13,  100000 ]
        //          - [ 14,   50000 ]
        //          - [ 15,   10000 ]
        //          - [ 16,    5000 ]
        //        default: 17
      }

      // tree_row - no POI}

      // wilderness_hut
      else if( sourceFeature.hasTag("tourism", "wilderness_hut") ) {
        kind = "wilderness_hut";
        // tier = 6;
        // min_zoom: 15
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // TIER 6 EXTRA - PARKING
      ////////////////////////////////////////////////////////////

      // parking
      else if( sourceFeature.hasTag("amenity", "parking") ) {
        if( sourceFeature.hasTag("parking", "multi-storey", "underground", "rooftop") ) {
          kind = "parking_garage";
        } else {
          kind = "parking";
        }
        // tier = 6;
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        //capacity:  { call: { func: tz_estimate_parking_capacity, args: [ { col: capacity }, { col: 'parking' }, { col: 'building:levels' }, { col: 'way_area' } ] } }
        capacity = sourceFeature.getString("capacity");

        //   min_zoom:
        //     lookup:
        //       key:  { call: { func: tz_estimate_parking_capacity, args: [ { col: capacity }, { col: 'parking' }, { col: 'building:levels' }, { col: 'way_area' } ] } }
        //       op: '>='
        //       table:
        //         - [ 14, 2000 ]
        //         - [ 15,  350 ]
        //         - [ 16,  100 ]
        //         - [ 17,   10 ]
        //       default: 18
      }

      ////////////////////////////////////////////////////////////
      // NOT IN ANY TIER
      ////////////////////////////////////////////////////////////

      else if( sourceFeature.hasTag("historic", "battlefield") ) {
        kind = "battlefield";
        // feature_min_zoom = { clamp: { min: 10, max: 17, value: { sum: [ { col: zoom }, 4 ] } } };
        theme_min_zoom = 10;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("natural", "peak", "volcano") ) {
        kind = sourceFeature.getString("natural");
        // feature_min_zoom = 12;
        theme_min_zoom = 11;
        theme_max_zoom = 15;

        // TODO this should be an int
        elevation = sourceFeature.getString("ele");

        // TODO
        //   min_zoom:
        //     lookup:
        //       key: { call: { func: mz_to_float_meters, args: [ { col: ele } ] } }
        //       op: '>='
        //       table:
        //         - [ 9,  4000 ]
        //         - [ 10, 3000 ]
        //         - [ 11, 2000 ]
        //         - [ 12, 1000 ]
        //       default: 13
      }
      else if( sourceFeature.hasTag("railway", "station") &&
              ( sourceFeature.hasTag("historic", "false") ||
                      sourceFeature.hasTag("historic", "no")
              )
      ) {
        kind = "station";
        // feature_min_zoom = 10;
        theme_min_zoom = 9;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");

        state = sourceFeature.getString("railway");
      }
      else if( sourceFeature.hasTag("natural", "spring") ) {
        kind = "spring";
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("railway", "level_crossing") ) {
        kind = "level_crossing";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // filter the waterway / boat fuel before generic amenity=fuel, as they appear
      // to be often tagged with both.
      else if( sourceFeature.hasTag("waterway", "fuel") ||
              sourceFeature.hasTag("seamark:small_craft_facility:category", "fuel_station")
      ) {
        kind = "waterway_fuel";
        // feature_min_zoom = { clamp: { min: 14, max: 16, value: { sum: [ { col: zoom }, 2.7 ] } } };
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "bank", "cinema", "courthouse", "embassy", "fire_station", "fuel", "library", "police", "post_office") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = { clamp: { min: 14, max: 16, value: { sum: [ { col: zoom }, 3.0 ] } } };
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "theatre") ) {
        kind = "theatre";
        // feature_min_zoom = { clamp: { min: 15, max: 17, value: { sum: [ { col: zoom }, 4.9 ] } } };
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      // TODO this is a dup of one just above
      else if( sourceFeature.hasTag("waterway", "fuel") ||
              sourceFeature.hasTag("seamark:small_craft_facility:category", "fuel_station")
      ) {
        kind = "waterway_fuel";
        // feature_min_zoom = { clamp: { min: 13, max: 16, value: { sum: [ { col: zoom }, 2.7 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "biergarten", "pub", "bar", "nightclub", "restaurant", "fast_food", "cafe") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = { clamp: { min: 15, max: 17, value: { sum: [ { col: zoom }, 2.5 ] } } }
        theme_min_zoom = 14;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("cuisine", "american", "asian", "barbecue", "breakfast", "burger", "cake", "chicken", "chinese", "coffee_shop", "crepe", "donut", "fish", "fish_and_chips", "french", "friture", "georgian", "german", "greek", "ice_cream", "indian", "international", "italian", "japanese", "kebab", "korean", "lebanese", "local", "mediterranean", "mexican", "noodle", "pizza", "ramen", "regional", "sandwich", "seafood", "spanish", "steak_house", "sushi", "tapas", "thai", "turkish", "vegetarian", "vietnamese" ) ) {
          kind_detail = sourceFeature.getString("cuisine");
        }
        // Redirect some odd OSM tagging
        else if( sourceFeature.hasTag("cuisine", "italian_pizza", "italian;pizza") ) {
          kind_detail = "italian";
        }
        else if( sourceFeature.hasTag("cuisine", "pizza;italian") ) {
          kind_detail = "pizza";
        }
      }
      else if( sourceFeature.hasTag("shop", "coffee", "deli") ) {
        kind = sourceFeature.getString("shop");
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 2.5 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "pharmacy", "veterinary") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.3 ] } } }
        theme_min_zoom = 13;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("craft", "brewery", "carpenter", "confectionery", "dressmaker", "electrician", "gardener", "handicraft", "hvac", "metal_construction", "painter", "photographer", "photographic_laboratory", "plumber", "pottery", "sawmill", "shoemaker", "stonemason", "tailor", "winery") ) {
        kind = sourceFeature.getString("craft");
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.3 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "charity", "chemist", "cosmetics", "fishmonger", "furniture", "golf", "pet", "shoes", "variety_store") ) {
        kind = sourceFeature.getString("shop");
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.3 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "camera", "copyshop", "photo", "tyres") ) {
        kind = sourceFeature.getString("shop}");
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "nursing_home") ) {
        kind = "nursing_home";
        // min_zoom = { clamp: { min: 15, max: 16, value: { sum: [ { col: zoom }, 1.25 ] } } }
        // feature_min_zoom = 15;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("shop", "music") ) {
        kind = "music";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 1.27 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "community_centre") ) {
        kind = "community_centre";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.98 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "sports") ) {
        kind = "sports";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 1.53 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "fishing") ) {
        kind = "fishing";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "hunting") ) {
        kind = "hunting";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "outdoor") ) {
        kind = "outdoor";
        // feature_min_zoom = { clamp: { min: 15, max: 16, value: { col: zoom } } };
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "dive_centre") ) {
        kind = "dive_centre";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "scuba_diving") ) {
        kind = "scuba_diving";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "atv", "motorcycle", "snowmobile") ) {
        kind = sourceFeature.getString("shop}");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "mall") ) {
        kind = "mall";
        // feature_min_zoom = { clamp: { min: 12, max: 17, value: { sum: [ { col: zoom }, 2.77 ] } } };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "prison") ) {
        kind = "prison";
        // feature_min_zoom = { clamp: { min: 13, max: 15, value: { sum: [ { col: zoom }, 2.55 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("tourism", "museum") ) {
        kind = "museum";
        // feature_min_zoom = { clamp: { min: 12, max: 16, value: { sum: [ { col: zoom }, 1.43 ] } } };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("historic", "landmark") ) {
        kind = "landmark";
        // feature_min_zoom = { clamp: { min: 12, max: 15, value: { sum: [ { col: zoom }, 1.76 ] } } };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "marina") ) {
        kind = "marina";
        // feature_min_zoom = { clamp: { min: 12, max: 17, value: { sum: [ { col: zoom }, 3.45 ] } } };
        theme_min_zoom = 12;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("sanitary_dump_station", "yes", "customers", "public") ) {
          sanitary_dump_station = sourceFeature.getString("sanitary_dump_station");
        }
      }
      else if( sourceFeature.hasTag("amenity", "townhall") ) {
        kind = "townhall";
        // feature_min_zoom = { clamp: { min: 12, max: 16, value: { sum: [ { col: zoom }, 1.85 ] } } };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "laundry", "dry_cleaning", "toys", "ice_cream", "wine", "alcohol") ) {
        kind = sourceFeature.getString("shop");
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 4.9 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "ice_cream") ) {
        kind = "ice_cream";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 4.9 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "ferry_terminal") ||
              sourceFeature.hasTag("landuse", "ferry_terminal")
      ) {
        kind = "ferry_terminal";
        // feature_min_zoom = { clamp: { min: 13, max: 16, value: { sum: [ { col: zoom }, 2.81 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "electronics") ) {
        kind = "electronics";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.3 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "department_store", "supermarket", "doityourself", "hardware", "trade", "garden_centre") ) {
        kind = sourceFeature.getString("shop");
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.29 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "marketplace") ) {
        kind = "marketplace";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.29 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("rental", "ski") ||
              sourceFeature.hasTag("amenity", "ski_rental")
      ) {
        kind = "ski_rental";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 1.27 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "ski") ) {
        kind = "ski";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 1.27 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "ski_school") ) {
        kind = "ski_school";
        // feature_min_zoom = { clamp: { min: 13, max: 15, value: { sum: [ { col: zoom }, 2.3 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "snow_cannon") ) {
        kind = "snow_cannon";
        // feature_min_zoom = { clamp: { min: 13, max: 18, value: { sum: [ { col: zoom }, 4.9 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "fitness_centre", "gym") ||
              sourceFeature.hasTag("amenity", "gym")
      ) {
        kind = "fitness";
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 3.98 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "fitness_station") ) {
        kind = "fitness_station";
        // feature_min_zoom = { clamp: { min: 17, max: 18, value: { sum: [ { col: zoom }, 3.98 ] } } };
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "beach_resort", "adult_gaming_centre") ) {
        kind = sourceFeature.getString("leisure");
        // feature_min_zoom = { clamp: { min: 14, max: 16, value: { sum: [ { col: zoom }, 0.5 ] } } };
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("tourism", "hotel", "motel") ) {
        kind = sourceFeature.getString("tourism");
        // feature_min_zoom = { clamp: { min: 13, max: 17, value: { sum: [ { col: zoom }, 4.3 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      // TODO (v2) does this belong in roads layer?
      else if( sourceFeature.hasTag("highway", "motorway_junction") ) {
        kind = "motorway_junction";
        // feature_min_zoom = 12;
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("historic", "monument") ) {
        kind = "monument";
        // feature_min_zoom = { clamp: { min: 15, max: 17, value: { sum: [ { col: zoom }, 2.24 ] } } };
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("zoo", "enclosure", "petting_zoo", "aviary") ) {
        kind = sourceFeature.getString("zoo");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("waterway", "waterfall") ||
              sourceFeature.hasTag("natural", "waterfall")
      ) {
        kind = "waterfall";
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;

        // TODO (nvkelso) 2023-04-05
        //       this should be a safe int
        height = sourceFeature.getString("height");

        // TODO
        //    min_zoom:
        //      lookup:
        //        key: { call: { func: mz_to_float_meters, args: [ { col: height } ] } }
        //        op: '>='
        //        table:
        //          - [ 12, 300 ]
        //          - [ 13,  50 ]
        //          - [ 14,  10 ]
        //          - [ 15,   1 ]
        //        default: { clamp: { min: 12, max: 14, value: { sum: [ { col: zoom }, 1.066 ] } } }

      }
      else if( sourceFeature.hasTag("natural", "geyser") ) {
        kind = "geyser";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("natural", "hot_spring") ) {
        kind = "hot_spring";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("historic", "fort") ) {
        kind = "fort";
        // feature_min_zoom = { clamp: { min: 13, max: 16, value: { sum: [ { col: zoom }, 2.5 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("tourism", "gallery") ) {
        kind = "gallery";
        // feature_min_zoom = { clamp: { min: 15, max: 17, value: { sum: [ { col: zoom }, 1.43 ] } } };
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("health_facility:type", "health_centre") ) {
        kind = "health_centre";
        // feature_min_zoom = { clamp: { min: 15, max: 17, value: { sum: [ { col: zoom }, 1.43 ] } } };
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("health_facility:type", "dispensary") ) {
        kind = "dispensary";
        // feature_min_zoom = 17
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("social_facility", "ambulatory_care") ) {
        kind = "ambulatory_care";
        // feature_min_zoom = 17
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("amenity", "clinic", "dentist", "doctors", "social_facility") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 17
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("healthcare", "chiropractor", "hospice", "midwife", "occupational_therapist", "optometrist", "paediatrics", "physiotherapist", "podiatrist", "psychotherapist", "rehabilitation", "speech_therapist") ) {
        kind = sourceFeature.getString("healthcare");
        // feature_min_zoom = 17
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("healthcare", "alternative") ) {
        kind = "healthcare_alternative";
        // feature_min_zoom = 17
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("healthcare", "centre") ) {
        kind = "healthcare_centre";
        // feature_min_zoom = 17
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("healthcare", "laboratory") ) {
        kind = "healthcare_laboratory";
        // feature_min_zoom = 17
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("health_facility:type", "CSCom", "chemist_dispensing", "clinic", "counselling_centre", "dispensary", "first_aid", "health_center", "health_centre", "hospital", "laboratory", "medical_clinic", "office", "pharmacy") ) {
          kind_detail = sourceFeature.getString("health_facility:type");
        }
      }
      else if( sourceFeature.hasTag("healthcare", "blood_donation", "blood_bank") ) {
        kind = "blood_bank";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "baby_hatch", "childcare") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "boat_rental") ||
              sourceFeature.hasTag("shop", "boat_rental") ||
              sourceFeature.hasTag("rental", "boat") ||
              ( sourceFeature.hasTag("shop", "boat") &&  sourceFeature.hasTag("rental", "yes") )
      ) {
        kind = "boat_rental";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "harbourmaster") ) {
        kind = "harbourmaster";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("emergency", "defibrillator", "fire_hydrant", "phone") ) {
        kind = sourceFeature.getString("emergency");
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }

      // NOTE: services and rest areas often have toilets, and often they're
      // co-tagged on the same object. therefore, we have to try and match the
      // service / rest area first, otherwise it'll get assigned as a toilet.
      //
      // service area points are hard to tell from mistagged points, but we can do
      // an approximation by looking to see if the name matches one of several
      // common suffixes and using that to lift the min zoom.
      else if( sourceFeature.hasTag("highway", "services") &&
              sourceFeature.isPoint()
      ) {
        kind = "service_area";
        // feature_min_zoom = { call: { func: tz_looks_like_service_area, args: [ { col: name } ] } };
        // TODO
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      // for service area polygons, we can look at the area, which we'd expect to}
      // be large.
      else if( sourceFeature.hasTag("highway", "services") ) {
        kind = "service_area";
        // feature_min_zoom = see below
        // TODO
        theme_min_zoom = 13;
        theme_max_zoom = 15;

        //    min_zoom:
        //      lookup:
        //        key: { col: way_area }
        //        op: '>='
        //        table:
        //          // really big service area polygons tend to cover the parking lots and
        //          // all the buildings (i.e: they're landuse areas).
        //          - [ 11, 50000 ]
        //          - [ 12, 10000 ]
        //          // however, there are many smaller ones which appear to just cover a
        //          // single building. ideally, these would be re-drawn to cover the whole
        //          // area, but most seem to be >3000 square "meters".
        //          - [ 13,  3000 ]
        //          - [ 14,  1500 ]
        //          - [ 15,  1000 ]
        //        default: 17
      }
      // identify rest area points from their names.
      else if( sourceFeature.hasTag("highway", "rest_area") &&
              sourceFeature.isPoint()
      ) {
        kind = "rest_area";
        // feature_min_zoom = { call: { func: tz_looks_like_rest_area, args: [ { col: name } ] } };
        // TODO
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      // and rest area polygons from their size
      else if( sourceFeature.hasTag("highway", "rest_area") ) {
        kind = "rest_area";
        // feature_min_zoom = { call: { func: tz_looks_like_rest_area, args: [ { col: name } ] } };
        // TODO
        theme_min_zoom = 13;
        theme_max_zoom = 15;

        //    min_zoom:
        //      lookup:
        //        key: { col: way_area }
        //        op: '>='
        //        table:
        //          / really big rest area polygons tend to cover the parking lots and
        //          / all the buildings (i.e: they're landuse areas).
        //          - [ 11, 50000 ]
        //          - [ 12, 25000 ]
        //          / however, there are many smaller ones which appear to just cover a
        //          / single building. ideally, these would be re-drawn to cover the whole
        //          / area, but most seem to be >20,000 square "meters".
        //          - [ 13, 10000 ]
        //          - [ 14,  5000 ]
        //          - [ 15,  1000 ]
        //        default: 17
      }
      else if( sourceFeature.hasTag("amenity", "toilets") ) {
        kind = "toilets";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        // normalize disposal and allowlist
        if( sourceFeature.hasTag("toilets:disposal", "pit_latrine", "flush", "chemical", "pour_flush", "bucket") ) {
          kind_detail = sourceFeature.getString("toilets:disposal");
        }
        else if( sourceFeature.hasTag("toilets:disposal", "pitlatrine") ) {
          kind_detail = "pit_latrine";
        }
      }
      else if( sourceFeature.hasTag("barrier", "chain", "gate", "kissing_gate", "lift_gate", "stile", "swing_gate") ) {
        kind = "gate";
        // feature_min_zoom = { call: { func: mz_get_min_zoom_highway_level_gate, args: [ { col: fid }, { col: meta.ways } ] } };
        // TODO
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        kind_detail = sourceFeature.getString("barrier");
      }
      else if( sourceFeature.hasTag("shop", "funeral_directors") ) {
        kind = "funeral_directors";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // put border_control ahead of customs. many customs checks will be border controls,
      // but the reverse isn't necessarily true.
      else if( sourceFeature.hasTag("barrier", "border_control") ) {
        kind = "border_control";
        // feature_min_zoom = { clamp: { min: 14, max: 16, value: { sum: [ { col: zoom }, 2.7 ] } } };
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "customs") ) {
        kind = "customs";
        // feature_min_zoom = { clamp: { min: 14, max: 16, value: { sum: [ { col: zoom }, 2.7 ] } } };
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("barrier", "toll_booth") ) {
        kind = "toll_booth";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("highway", "turning_circle", "turning_loop") ) {
        kind = "{ col: highway }";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("highway", "mini_roundabout") ) {
        kind = "mini_roundabout";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("lock", "yes") ) {
        kind = "lock";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "power_wind") ) {
        kind = "power_wind";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("natural", "cave_entrance") ) {
        kind = "cave_entrance";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("waterway", "lock") ) {
        kind = "lock";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("aerialway", "station") ) {
        kind = "station";
        // feature_min_zoom = 13;
        theme_min_zoom = 12;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");

        state = sourceFeature.getString("state");
      }
      // also count a public_transport=station as a station, if it has
      // railway-ish tags.
      else if( sourceFeature.hasTag("public_transport", "station") &&
              ( sourceFeature.hasTag("rail") ||
                      sourceFeature.hasTag("light_rail") ||
                      sourceFeature.hasTag("railway")
              )
      ) {
        kind = "station";
        // feature_min_zoom = 10;
        theme_min_zoom = 9;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      // to work around overwriting when using the same key twice (because we
      // want to say "when historic is not present, or is present and is 'no".)
      else if( sourceFeature.hasTag("railway", "halt", "stop", "tram_stop") &&
              ( !sourceFeature.hasTag("historic") ||
                      sourceFeature.hasTag("historic", "no")
              )
      ) {
        kind = sourceFeature.getString("railway");
        // feature_min_zoom = 16;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("railway", "platform") ) {
        kind = "platform";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("highway", "platform", "bus_stop") ) {
        kind = "bus_stop";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("public_transport", "platform") &&
              sourceFeature.hasTag("rail", "yes")
      ) {
        kind = "platform";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("public_transport", "platform") &&
              sourceFeature.hasTag("light_rail", "yes")
      ) {
        kind = "platform";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("public_transport", "platform") &&
              sourceFeature.hasTag("bus", "yes")
      ) {
        kind = "bus_stop";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("public_transport", "platform") ) {
        kind = "platform";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("public_transport", "stop_area") ) {
        kind = "stop_area";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("site", "stop_area") ) {
        kind = "stop_area";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("tourism", "alpine_hut") ) {
        kind = "alpine_hut";
        // feature_min_zoom = 13;
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("aeroway", "gate") ) {
        kind = "aeroway_gate";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("aeroway", "helipad") ) {
        kind = "helipad";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "arts_centre") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "bus_station", "car_rental", "recycling", "shelter") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "car_sharing") &&
              ( sourceFeature.hasTag("name") && sourceFeature.getString("name") != null )
      ) {
        kind = "car_sharing";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "car_sharing") ) {
        kind = "car_sharing";
        // feature_min_zoom = 19;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("barrier", "block", "bollard", "cattle_grid") ) {
        kind = sourceFeature.getString("barrier");
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("highway", "ford") ) {
        kind = "ford";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("historic", "archaeological_site") ) {
        kind = "archaeological_site";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "communications_tower") ) {
        kind = "communications_tower";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "telescope") ) {
        kind = "telescope";
        // feature_min_zoom = { clamp: { min: 15, max: 16, value: { sum: [ { col: zoom }, 0.1 ] } } };
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "offshore_platform") ) {
        kind = "offshore_platform";
        // feature_min_zoom = 13;
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "water_tower") ) {
        kind = "water_tower";
        // feature_min_zoom = 15;
        theme_min_zoom = 14;
        theme_max_zoom = 15;

        // TODO
        //    min_zoom:
        //      lookup:
        //        key: { call: { func: mz_to_float_meters, args: [ { col: height } ] } }
        //        op: '>='
        //        table:
        //          - [ 15, 20 ]  // z15 if height >= 20m
        //          - [ 16, 10 ]  // z16 if height >= 10m
        //        default: 17
      }
      else if( sourceFeature.hasTag("natural", "tree") ) {
        kind = "tree";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "ranger_station") ) {
        kind = "ranger_station";
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("icn_ref") ) {
        kind = "bicycle_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("icn_ref");
        ref = sourceFeature.getString("icn_ref");
        bicycle_network = "icn";
      }
      else if( sourceFeature.hasTag("ncn_ref") ) {
        kind = "bicycle_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("ncn_ref");
        ref = sourceFeature.getString("ncn_ref");
        bicycle_network = "ncn";
      }
      else if( sourceFeature.hasTag("rcn_ref") ) {
        kind = "bicycle_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("rcn_ref");
        ref = sourceFeature.getString("rcn_ref");
        bicycle_network = "rcn";
      }
      else if( sourceFeature.hasTag("lcn_ref") ) {
        kind = "bicycle_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("lcn_ref");
        ref = sourceFeature.getString("lcn_ref");
        bicycle_network = "lcn";
      }
      else if( sourceFeature.hasTag("iwn_ref") ) {
        kind = "walking_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("iwn_ref");
        ref = sourceFeature.getString("iwn_ref");
        walking_network = "iwn";
      }
      else if( sourceFeature.hasTag("nwn_ref") ) {
        kind = "walking_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("nwn_ref");
        ref = sourceFeature.getString("nwn_ref");
        walking_network = "nwn";
      }
      else if( sourceFeature.hasTag("rwn_ref") ) {
        kind = "walking_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("rwn_ref");
        ref = sourceFeature.getString("rwn_ref");
        walking_network = "rwn";
      }
      else if( sourceFeature.hasTag("lwn_ref") ) {
        kind = "walking_junction";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        shield_text = sourceFeature.getString("lwn_ref");
        ref = sourceFeature.getString("lwn_ref");
        walking_network = "lwn";
      }
      else if( sourceFeature.hasTag("tourism", "camp_site") ) {
        kind = "camp_site";
        // tier = 5;
        // feature_min_zoom = { max: [ 13, *tier5_min_zoom ] };
        theme_min_zoom = 13;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("sanitary_dump_station", "yes", "customers", "public") ) {
          sanitary_dump_station = sourceFeature.getString("sanitary_dump_station");
        }
      }
      else if( sourceFeature.hasTag("tourism", "viewpoint") ) {
        kind = "viewpoint";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("tourism", "information") &&
              ( sourceFeature.hasTag("name") && sourceFeature.getString("name") != null )
      ) {
        kind = "information";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("tourism", "information") ) {
        kind = "information";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "bureau_de_change", "emergency_phone", "karaoke", "karaoke_box", "money_transfer") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "atm", "post_box", "telephone") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "drinking_water") ) {
        kind = "drinking_water";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("highway", "street_lamp") ) {
        kind = "street_lamp";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("highway", "traffic_signals") ) {
        kind = "traffic_signals";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // memorial plaques more specific than plain memorials
      else if( ( sourceFeature.hasTag("historic", "memorial") &&
              sourceFeature.hasTag("memorial", "plaque")
      ) ||
              sourceFeature.hasTag("historic", "memorial_plaque")

      ) {
        kind = "plaque";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // plain memorial
      else if( sourceFeature.hasTag("historic", "memorial") ) {
        kind = "memorial";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // slipway with mooring info
      else if( sourceFeature.hasTag("leisure", "slipway") &&
              sourceFeature.hasTag("mooring", "yes", "no")
      ) {
        kind = "slipway";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        mooring = sourceFeature.getString("mooring");
      }
      // plain slipway without mooring info
      else if( sourceFeature.hasTag("leisure", "slipway") ) {
        kind = "slipway";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "mast") ) {
        kind = "mast";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("emergency", "defibrillator") ) {
        kind = "defibrillator";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("office", "accountant", "administrative", "advertising_agency", "architect", "association", "company", "consulting", "educational_institution", "employment_agency", "estate_agent", "financial", "foundation", "government", "insurance", "it", "lawyer", "newspaper", "ngo", "notary", "physician", "political_party", "religion", "research", "tax_advisor", "telecommunication", "therapist", "travel_agent") ) {
        kind = sourceFeature.getString("office");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "bicycle") ) {
        kind = "bicycle";
        // feature_min_zoom = { clamp: { min: 15, max: 17, value: { sum: [ { col: zoom }, 3 ] } } };
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "bicycle_rental") &&
              ! sourceFeature.hasTag("operator")
      ) {
        kind = "bicycle_rental";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "bicycle_rental") &&
              sourceFeature.hasTag("operator")
      ) {
        kind = "bicycle_rental_station";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        // TODO these need to be added to general pattern at top, and checked for on setting at end
        // Extra props for bike rental stations
        network = sourceFeature.getString("network");
        operator_var = sourceFeature.getString("operator");
        capacity = sourceFeature.getString("capacity");
        ref = sourceFeature.getString("ref");
      }
      else if( sourceFeature.hasTag("amenity", "motorcycle_parking") ) {
        kind = "motorcycle_parking";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("access") ) {
          access = sourceFeature.getString("access");
        }
        if( sourceFeature.hasTag("operator") ) {
          operator_var = sourceFeature.getString("operator");
        }
        // TODO this should be an int
        if( sourceFeature.hasTag("capacity") ) {
          capacity = sourceFeature.getString("capacity");
        }
        if( sourceFeature.hasTag("covered", "yes") ) {
          covered = true;
        } else if( sourceFeature.hasTag("covered", "no") ) {
          covered = false;
        }
        if( sourceFeature.hasTag("fee") &&
                ! sourceFeature.hasTag("fee", "no", "Free", "free", "0", "No", "none")
        ) {
          fee = true;
        } else {
          fee = false;
        }
        if( sourceFeature.hasTag("cyclestreets_id") ) {
          cyclestreets_id = sourceFeature.getString("cyclestreets_id");
        }
        if( sourceFeature.hasTag("maxstay") ) {
          maxstay = sourceFeature.getString("maxstay");
        }
        if( sourceFeature.hasTag("surveillance") &&
                ! sourceFeature.hasTag("surveillance", "no", "none")
        ) {
          surveillance = true;
        }
      }
      else if( sourceFeature.hasTag("amenity", "bicycle_parking") &&
              ( sourceFeature.hasTag("name") && sourceFeature.getString("name") != null )
      ) {
        kind = "bicycle_parking";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("access") ) {
          access = sourceFeature.getString("access");
        }
        if( sourceFeature.hasTag("operator") ) {
          operator_var = sourceFeature.getString("operator");
        }
        // TODO this should be an int
        if( sourceFeature.hasTag("capacity") ) {
          capacity = sourceFeature.getString("capacity");
        }
        if( sourceFeature.hasTag("covered", "yes") ) {
          covered = true;
        } else if( sourceFeature.hasTag("covered", "no") ) {
          covered = false;
        }
        if( sourceFeature.hasTag("fee") &&
                ! sourceFeature.hasTag("fee", "no", "Free", "free", "0", "No", "none")
        ) {
          fee = true;
        } else {
          fee = false;
        }
        if( sourceFeature.hasTag("cyclestreets_id") ) {
          cyclestreets_id = sourceFeature.getString("cyclestreets_id");
        }
        if( sourceFeature.hasTag("maxstay") ) {
          maxstay = sourceFeature.getString("maxstay");
        }
        if( sourceFeature.hasTag("surveillance") &&
                ! sourceFeature.hasTag("surveillance", "no", "none")
        ) {
          surveillance = true;
        }
      }
      else if( sourceFeature.hasTag("amenity", "bicycle_parking") ) {
        kind = "bicycle_parking";
        // feature_min_zoom = 19;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("access") ) {
          access = sourceFeature.getString("access");
        }
        if( sourceFeature.hasTag("operator") ) {
          operator_var = sourceFeature.getString("operator");
        }
        // TODO this should be an int
        if( sourceFeature.hasTag("capacity") ) {
          capacity = sourceFeature.getString("capacity");
        }
        if( sourceFeature.hasTag("covered", "yes") ) {
          covered = true;
        } else if( sourceFeature.hasTag("covered", "no") ) {
          covered = false;
        }
        if( sourceFeature.hasTag("fee") &&
                ! sourceFeature.hasTag("fee", "no", "Free", "free", "0", "No", "none")
        ) {
          fee = true;
        } else {
          fee = false;
        }
        if( sourceFeature.hasTag("cyclestreets_id") ) {
          cyclestreets_id = sourceFeature.getString("cyclestreets_id");
        }
        if( sourceFeature.hasTag("maxstay") ) {
          maxstay = sourceFeature.getString("maxstay");
        }
        if( sourceFeature.hasTag("surveillance") &&
                ! sourceFeature.hasTag("surveillance", "no", "none")
        ) {
          surveillance = true;
        }
      }
      else if( sourceFeature.hasTag("amenity", "charging_station") ) {
        kind = "charging_station";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        // charging for what type of transport?
        if( sourceFeature.hasTag("bicycle", "yes") ) {
          bicycle = true;
        } else if( sourceFeature.hasTag("bicycle", "no") ) {
          bicycle = false;
        }
        if( sourceFeature.hasTag("car", "yes") ) {
          car = true;
        } else if( sourceFeature.hasTag("car", "no") ) {
          car = false;
        }
        if( sourceFeature.hasTag("truck", "yes") ) {
          truck = true;
        } else if( sourceFeature.hasTag("truck", "no") ) {
          truck = false;
        }
        if( sourceFeature.hasTag("scooter", "yes") ) {
          scooter = true;
        } else if( sourceFeature.hasTag("scooter", "no") ) {
          scooter = false;
        }
      }
      else if( sourceFeature.hasTag("barrier", "cycle_barrier") ) {
        kind = "cycle_barrier";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "art", "bakery", "beauty", "bookmaker", "books", "butcher", "car", "car_parts", "car_repair", "clothes", "computer", "convenience", "fashion", "florist", "garden_centre", "gift", "golf", "greengrocer", "grocery", "hairdresser", "hifi", "jewelry", "lottery", "mobile_phone", "newsagent", "optician", "perfumery", "ship_chandler", "stationery", "tobacco", "travel_agency") ) {
        kind = sourceFeature.getString("shop");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // These are lower priority than the shops above
      else if( sourceFeature.hasTag("amenity", "car_wash", "hunting_stand") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("tourism", "bed_and_breakfast", "chalet", "guest_house", "hostel") ) {
        kind = sourceFeature.getString("tourism");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // prefer elevator over general subway_entrance
      else if( sourceFeature.hasTag("highway", "elevator") ) {
        kind = "elevator";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("railway", "subway_entrance") ) {
        kind = "subway_entrance";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;
        theme_max_zoom = 15;

        // TODO
        // *transit_properties
        mz_transit_score = sourceFeature.getString("mz_transit_score");
        mz_transit_root_relation_id = sourceFeature.getString("mz_transit_root_relation_id");
        train_routes = sourceFeature.getString("train_routes");
        subway_routes = sourceFeature.getString("subway_routes");
        light_rail_routes = sourceFeature.getString("light_rail_routes");
        tram_routes = sourceFeature.getString("tram_routes");
      }
      else if( sourceFeature.hasTag("amenity", "bench", "waste_basket") ) {
        kind = sourceFeature.getString("amenity");
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "beacon", "cross", "mineshaft") ) {
        kind = sourceFeature.getString("man_made");
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "adit") ) {
        kind = "adit";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "water_well") ) {
        kind = "water_well";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        // This is for humanitarian map styles showing access to clean drinking water
        if( sourceFeature.hasTag("drinking_water", "yes") &&
                sourceFeature.hasTag("pump", "powered") ) {
          kind_detail = "drinkable_powered";
        }
        else if( sourceFeature.hasTag("drinking_water", "manual") &&
                sourceFeature.hasTag("pump", "powered") ) {
          kind_detail = "drinkable_manual";
        }
        else if( sourceFeature.hasTag("drinking_water", "manual") &&
                sourceFeature.hasTag("pump", "no") ) {
          kind_detail = "drinkable_no_pump";
        }
        else if( sourceFeature.hasTag("drinking_water", "yes") ) {
          kind_detail = "drinkable";
        }
        else if( sourceFeature.hasTag("drinking_water", "no") &&
                sourceFeature.hasTag("pump", "powered") ) {
          kind_detail = "not_drinkable_powered";
        }
        else if( sourceFeature.hasTag("drinking_water", "no") &&
                sourceFeature.hasTag("pump", "manual") ) {
          kind_detail = "not_drinkable_manual";
        }
        else if( sourceFeature.hasTag("drinking_water", "no") &&
                sourceFeature.hasTag("pump", "no") ) {
          kind_detail = "not_drinkable_no_pump";
        }
        else if( sourceFeature.hasTag("drinking_water", "no") ) {
          kind_detail = "not_drinkable";
        }
      }
      else if( sourceFeature.hasTag("natural", "saddle") ) {
        kind = "saddle";
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("natural", "dune", "sinkhole") ) {
        kind = sourceFeature.getString("natural");
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("natural", "rock", "stone") ) {
        kind = sourceFeature.getString("natural");
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("highway", "trailhead") ) {
        kind = "trailhead";
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("whitewater", "put_in;egress") ) {
        kind = "put_in_egress";
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("whitewater", "put_in", "egress") ) {
        kind = sourceFeature.getString("whitewater");
        // feature_min_zoom = 14;
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("whitewater", "hazard", "rapid") ) {
        kind = sourceFeature.getString("whitewater");
        // feature_min_zoom = 15;
        theme_min_zoom = 15;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("shop", "gas") ) {
        kind = "gas_canister";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("aerialway", "pylon") ) {
        kind = "pylon";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "bbq") ) {
        kind = "bbq";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "studio") ) {
        kind = "studio";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        // allowlist common studio values as kind_detail
        if( sourceFeature.hasTag("studio", "audio", "cinema", "photography", "radio", "television", "video") ) {
          kind_detail = sourceFeature.getString("studio");
        }
      }
      else if( sourceFeature.hasTag("amenity", "bicycle_repair_station") ) {
        kind = "bicycle_repair_station";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "life_ring") ) {
        kind = "life_ring";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "picnic_table") ) {
        kind = "picnic_table";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "love_hotel") ) {
        kind = "love_hotel";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "shower", "taxi") ) {
        kind = sourceFeature.getString("amenity}");
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "waste_disposal") ) {
        kind = "waste_disposal";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "watering_place") ) {
        kind = "watering_place";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "water_point") ) {
        kind = "water_point";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("emergency", "lifeguard_tower") ) {
        kind = "lifeguard_tower";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("power", "pole") ) {
        kind = "power_pole";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("power", "tower") ) {
        kind = "power_tower";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "petroleum_well") ) {
        kind = "petroleum_well";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // a boat lift is a more specific type of object than a crane, although it may
      // or may not be an instance of a crane.
      else if( sourceFeature.hasTag("waterway", "boat_lift") ) {
        kind = "boat_lift";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("man_made", "crane") ) {
        kind = "crane";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("crane:type", "portal_crane", "gantry_crane", "travel_lift", "floor-mounted_crane", "shiploader", "tower_crane") ) {
          kind_detail = sourceFeature.getString("crane:type");
        }
      }
      else if( sourceFeature.hasTag("leisure", "water_park") ) {
        kind = "water_park";
        // feature_min_zoom = { clamp: { min: 13, max: 15, value: { sum: [ { col: zoom }, 2.34 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "summer_camp") ) {
        kind = "summer_camp";
        // feature_min_zoom = { clamp: { min: 14, max: 15, value: { sum: [ { col: zoom }, 1.32 ] } } };
        theme_min_zoom = 14;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "boat_storage") ) {
        kind = "boat_storage";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("waterway", "dam") ) {
        kind = "dam";
        // feature_min_zoom = { clamp: { min: 12, max: 14, value: { sum: [ { col: zoom }, 1 ] } } };
        theme_min_zoom = 12;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "dog_park") ) {
        kind = "dog_park";
        // feature_min_zoom = { clamp: { min: 16, max: 17, value: { sum: [ { col: zoom }, 1 ] } } };
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "track") ) {
        kind = "recreation_track";
        // feature_min_zoom = { clamp: { min: 16, max: 17, value: { sum: [ { col: zoom }, 1 ] } } };
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "fishing") ) {
        kind = "fishing_area";
        // feature_min_zoom = { clamp: { min: 16, max: 17, value: { sum: [ { col: zoom }, 1.76 ] } } };
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "swimming_area") ) {
        kind = "swimming_area";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "firepit") ) {
        kind = "firepit";
        // feature_min_zoom = 18;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("leisure", "miniature_golf") ) {
        kind = "miniature_golf";
        // feature_min_zoom = { clamp: { min: 16, max: 17, value: { sum: [ { col: zoom }, 1 ] } } };
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("tourism", "caravan_site") ) {
        kind = "caravan_site";
        // feature_min_zoom = { clamp: { min: 14, max: 15, value: { col: zoom } } };
        theme_min_zoom = 14;
        theme_max_zoom = 15;

        if( sourceFeature.hasTag("sanitary_dump_station", "yes", "customers", "public") ) {
          sanitary_dump_station = sourceFeature.getString("sanitary_dump_station");
        }
      }
      else if( sourceFeature.hasTag("tourism", "picnic_site") ) {
        kind = "picnic_site";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("landuse", "quarry") ) {
        kind = "quarry";
        // feature_min_zoom = { clamp: { min: 13, max: 16, value: { sum: [ { col: zoom }, 3.5 ] } } };
        theme_min_zoom = 13;
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("industrial", "slaughterhouse") ) {
        kind = "slaughterhouse";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // also account for fairly common misspelling
      else if( sourceFeature.hasTag("leisure", "adult_gaming_centre", "adult_gaming_center") ) {
        kind = "adult_gaming_centre";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // this is often used on shop=bookmaker, shop=lottery or}
      // leisure=adult_gaming_centre, but seems more generic than any of those.
      else if( sourceFeature.hasTag("amenity", "gambling") ) {
        kind = "gambling";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("waterway", "boatyard") ) {
        kind = "boatyard";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "harbourmaster") ||
              sourceFeature.hasTag("harbour", "harbour_master") ||
              sourceFeature.hasTag("seamark:building:function", "harbour_master")
      ) {
        kind = "harbourmaster";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // moorings with more detail
      else if( sourceFeature.hasTag("mooring", "commercial", "cruise", "customers", "declaration", "ferry", "guest", "pile", "waiting", "yacht", "yachts") ) {
        kind = "mooring";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        kind_detail = sourceFeature.getString("mooring");
      }
      // moorings with access restrictions
      else if( sourceFeature.hasTag("mooring", "private", "public") ) {
        kind = "mooring";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;

        kind_detail = sourceFeature.getString("mooring");
        access = sourceFeature.getString("mooring");
      }
      // plain moorings, without detail
      else if( sourceFeature.hasTag("mooring", "true") ) {
        kind = "mooring";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("amenity", "sanitary_dump_station") ||
              sourceFeature.hasTag("waterway", "sanitary_dump_station")
      ) {
        kind = "sanitary_dump_station";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      else if( sourceFeature.hasTag("historic", "wayside_cross") ) {
        kind = "wayside_cross";
        // feature_min_zoom = 16;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }

      ////////////////////////////////////////////////////////////
      // TIER 6 EXTRA - ATTRACTION
      //
      // Because this is so generic, we want other tags to match
      // before this one.
      ////////////////////////////////////////////////////////////

      else if( sourceFeature.hasTag("tourism", "attraction") ) {
        // already clamped below polygon area min
        kind = "attraction";
        // tier = 6;
        // min_zoom: 17
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // if a more specific shop isn't found, then default to generic shop kind
      else if( sourceFeature.hasTag("shop", "true") ) {
        kind = "shop";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // if a more specific craft isn't found, then default to generic craft kind
      else if( sourceFeature.hasTag("craft", "true") ) {
        kind = "craft";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // if a more specific off isn't found, then default to a generic office.
      else if( sourceFeature.hasTag("office", "true") ) {
        kind = "office";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // if a more specific industrial isn't found, then default to generic industrial kind
      else if( sourceFeature.hasTag("industrial", "true") ) {
        kind = "industrial";
        // feature_min_zoom = 17;
        theme_min_zoom = 15;  // MAX_ZOOM
        theme_max_zoom = 15;
      }
      // END huge ordered if-else logic

      if (kind != "") {
        // try first for polygon -> point representations
        if (sourceFeature.canBePolygon()) {
          var poly_label_position = features.pointOnSurface(this.name())
                  // all POIs should receive their IDs at all zooms
                  // (there is no merging of POIs like with lines and polygons in other layers)
                  .setId(FeatureId.create(sourceFeature))
                  .setAttr("kind", kind)
                  // TODO set feature min_zoom instead
                  .setAttr("min_zoom", theme_min_zoom)
                  // always set names on POIs at all zooms
                  .setAttr("name", sourceFeature.getString("name"))
                  .setAttrWithMinzoom("protect_class", sourceFeature.getString("protect_class"), 13)
                  .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 13)
                  .setAttrWithMinzoom("sport", sourceFeature.getString("sport"), 13)
                  // TODO (v2) this could use allowlist sanity check (see config above)
                  .setAttrWithMinzoom("religion", sourceFeature.getString("religion"), 14)
                  .setAttrWithMinzoom("ref", sourceFeature.getString("ref"), 14)
                  .setAttrWithMinzoom("shield_text", shield_text, 14)
                  .setAttrWithMinzoom("attraction", sourceFeature.getString("attraction"), 14)
                  .setAttrWithMinzoom("zoo", sourceFeature.getString("zoo"), 14)
                  .setAttrWithMinzoom("exit_to", sourceFeature.getString("exit_to"), 14)
                  .setAttrWithMinzoom("wikidata_id", sourceFeature.getString("wikidata_id"), 14)
                  .setAttrWithMinzoom("direction", sourceFeature.getString("direction"), 14)
                  // TODO set feature min_zoom instead but floar'd as int
                  .setZoomRange(theme_min_zoom, theme_max_zoom)
                  .setAttr("source", "openstreetmap.org")
                  .setBufferPixels(128);

          // TODO
          // export "tier" only from 14+
          // export "area" (v2 drop this?)

          if( kind_detail != "") {
            poly_label_position.setAttrWithMinzoom("kind_detail", kind_detail, 14);
          }

          // for churches and cemetaries
          if( denomination != "") {
            poly_label_position.setAttrWithMinzoom("denomination", denomination, 14);
          }

          // for marina features
          if( mooring != "") {
            poly_label_position.setAttrWithMinzoom("mooring", mooring, 14);
          }
          if( sanitary_dump_station != "") {
            poly_label_position.setAttrWithMinzoom("sanitary_dump_station", sanitary_dump_station, 14);
          }

          if( elevation != "" ) {
            poly_label_position.setAttr("elevation", elevation);
          }
          // for waterfalls
          if( height != "" ) {
            poly_label_position.setAttr("height", height);
          }

          // transit_properties
          if( mz_transit_score != "" ) {
            poly_label_position.setAttr("mz_transit_score", mz_transit_score);
          }
          if( mz_transit_root_relation_id != "" ) {
            poly_label_position.setAttr("mz_transit_root_relation_id", mz_transit_root_relation_id);
          }
          if( train_routes != "" ) {
            poly_label_position.setAttr("train_routes", train_routes);
          }
          if( subway_routes != "" ) {
            poly_label_position.setAttr("subway_routes", subway_routes);
          }
          if( light_rail_routes != "" ) {
            poly_label_position.setAttr("light_rail_routes", light_rail_routes);
          }
          if( tram_routes != "" ) {
            poly_label_position.setAttr("tram_routes", tram_routes);
          }
          if( state != "" ) {
            poly_label_position.setAttr("state", state);
          }

          if( walking_network != "" ) {
            poly_label_position.setAttr("walking_network", walking_network);
          }

          // common properties for {bi|motor}cycle parking
          // - &cycle_parking_properties
          if( bicycle_network != "" ) {
            poly_label_position.setAttr("bicycle_network", bicycle_network);
          }
          if( access != "" ) {
            poly_label_position.setAttr("access", access);
          }
          if( operator != "" ) {
            poly_label_position.setAttr("operator", operator_var);
          }
          if( network != "" ) {
            poly_label_position.setAttr("network", network);
          }
          if( capacity != "" ) {
            poly_label_position.setAttr("capacity", capacity);
          }
          if( covered ) {
            poly_label_position.setAttr("covered", covered);
          }
          if( fee ) {
            poly_label_position.setAttr("fee", fee);
          }
          if( cyclestreets_id != "" ) {
            poly_label_position.setAttr("cyclestreets_id", cyclestreets_id);
          }
          if( cyclestreets_id != "" ) {
            poly_label_position.setAttr("cyclestreets_id", cyclestreets_id);
          }
          if( maxstay != "" ) {
            poly_label_position.setAttr("maxstay", maxstay);
          }
          if( surveillance ) {
            poly_label_position.setAttr("surveillance", surveillance);
          }

          // charging stations
          if( bicycle ) {
            poly_label_position.setAttr("bicycle", bicycle);
          }
          if( car ) {
            poly_label_position.setAttr("car", car);
          }
          if( truck ) {
            poly_label_position.setAttr("truck", truck);
          }
          if( scooter ) {
            poly_label_position.setAttr("scooter", scooter);
          }

          // TODO (nvkelso 2023-03-21)
          // What is a ghostFeature?!
          //if (ghostFeatures) {
          //  poly_label_position.setAttr("isGhostFeature", true);
          //}

          // POIs should always have names at all zooms
          OsmNames.setOsmNames(poly_label_position, sourceFeature, theme_min_zoom);

        } else if (sourceFeature.isPoint()) {
          var point = features.point(this.name())
                  // all POIs should receive their IDs at all zooms
                  // (there is no merging of POIs like with lines and polygons in other layers)
                  .setId(FeatureId.create(sourceFeature))
                  .setAttr("kind", kind)
                  // TODO set feature min_zoom instead
                  .setAttr("min_zoom", theme_min_zoom)
                  // always set names on POIs at all zooms
                  .setAttr("name", sourceFeature.getString("name"))
                  .setAttrWithMinzoom("protect_class", sourceFeature.getString("protect_class"), 13)
                  .setAttrWithMinzoom("operator", sourceFeature.getString("operator"), 13)
                  .setAttrWithMinzoom("sport", sourceFeature.getString("sport"), 13)
                  // TODO (v2) this could use allowlist sanity check (see config above)
                  .setAttrWithMinzoom("religion", sourceFeature.getString("religion"), 14)
                  .setAttrWithMinzoom("ref", sourceFeature.getString("ref"), 14)
                  .setAttrWithMinzoom("shield_text", shield_text, 14)
                  .setAttrWithMinzoom("attraction", sourceFeature.getString("attraction"), 14)
                  .setAttrWithMinzoom("zoo", sourceFeature.getString("zoo"), 14)
                  .setAttrWithMinzoom("exit_to", sourceFeature.getString("exit_to"), 14)
                  .setAttrWithMinzoom("wikidata_id", sourceFeature.getString("wikidata_id"), 14)
                  .setAttrWithMinzoom("direction", sourceFeature.getString("direction"), 14)
                  // TODO set feature min_zoom instead but floar'd as int
                  .setZoomRange(theme_min_zoom, theme_max_zoom)
                  .setAttr("source", "openstreetmap.org")
                  .setBufferPixels(128);

          if( kind_detail != "") {
            point.setAttrWithMinzoom("kind_detail", kind_detail, 14);
          }

          // for churches and cemetaries
          if( denomination != "") {
            point.setAttrWithMinzoom("denomination", denomination, 14);
          }

          // for marina features
          if( mooring != "") {
            point.setAttrWithMinzoom("mooring", mooring, 14);
          }
          if( sanitary_dump_station != "") {
            point.setAttrWithMinzoom("sanitary_dump_station", sanitary_dump_station, 14);
          }

          // for peaks and volcanos
          if( elevation != "" ) {
            point.setAttr("elevation", elevation);
          }
          // for waterfalls
          if( height != "" ) {
            point.setAttr("height", height);
          }

          // transit_properties
          if( mz_transit_score != "" ) {
            point.setAttr("mz_transit_score", mz_transit_score);
          }
          if( mz_transit_root_relation_id != "" ) {
            point.setAttr("mz_transit_root_relation_id", mz_transit_root_relation_id);
          }
          if( train_routes != "" ) {
            point.setAttr("train_routes", train_routes);
          }
          if( subway_routes != "" ) {
            point.setAttr("subway_routes", subway_routes);
          }
          if( light_rail_routes != "" ) {
            point.setAttr("light_rail_routes", light_rail_routes);
          }
          if( tram_routes != "" ) {
            point.setAttr("tram_routes", tram_routes);
          }
          if( state != "" ) {
            point.setAttr("state", state);
          }

          if( walking_network != "" ) {
            point.setAttr("walking_network", walking_network);
          }

          // common properties for {bi|motor}cycle parking
          // - &cycle_parking_properties
          if( bicycle_network != "" ) {
            point.setAttr("bicycle_network", bicycle_network);
          }
          if( access != "" ) {
            point.setAttr("access", access);
          }
          if( operator != "" ) {
            point.setAttr("operator", operator_var);
          }
          if( network != "" ) {
            point.setAttr("network", network);
          }
          if( capacity != "" ) {
            point.setAttr("capacity", capacity);
          }
          if( covered ) {
            point.setAttr("covered", covered);
          }
          if( fee ) {
            point.setAttr("fee", fee);
          }
          if( cyclestreets_id != "" ) {
            point.setAttr("cyclestreets_id", cyclestreets_id);
          }
          if( cyclestreets_id != "" ) {
            point.setAttr("cyclestreets_id", cyclestreets_id);
          }
          if( maxstay != "" ) {
            point.setAttr("maxstay", maxstay);
          }
          if( surveillance ) {
            point.setAttr("surveillance", surveillance);
          }

          // charging stations
          if( bicycle ) {
            point.setAttr("bicycle", bicycle);
          }
          if( car ) {
            point.setAttr("car", car);
          }
          if( truck ) {
            point.setAttr("truck", truck);
          }
          if( scooter ) {
            point.setAttr("scooter", scooter);
          }

          // POIs should always have names at all zooms
          OsmNames.setOsmNames(point, sourceFeature, theme_min_zoom);
        }
      }

    }
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
    // nvkelso (20230321)
    // https://github.com/tilezen/tilequeue/blob/33a4dd42e8f764bcc9817e89554ee21922f501a8/config.yaml.sample#L146-L155
    // TODO
    // This should be 64 px buffer

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


    return items;
  }
}
