package com.protomaps.basemap;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import com.protomaps.basemap.layers.Boundaries;
import com.protomaps.basemap.layers.Buildings;
import com.protomaps.basemap.layers.Earth;
import com.protomaps.basemap.layers.Landuse;
import com.protomaps.basemap.layers.Places;
import com.protomaps.basemap.layers.Pois;
import com.protomaps.basemap.layers.Roads;
import com.protomaps.basemap.layers.Transit;
import com.protomaps.basemap.layers.Water;
import java.nio.file.Path;
import java.util.List;


public class Basemap extends ForwardingProfile {

  public Basemap() {

	// Why isn't this var called "boundaries"?
    var boundaries = new Boundaries();
    registerHandler(boundaries);
    registerSourceHandler("ne", boundaries::processNe);
    registerSourceHandler("osm", boundaries);
    // TODO (nvkelso 2023-03-21)
    // This is a guess for maritime mask calulations
    // registerSourceHandler("buffered_land", boundaries::processNaturalEarth);

    var buildings = new Buildings();
    registerHandler(buildings);
    registerSourceHandler("osm", buildings);

    var landuse = new Landuse();
    registerHandler(landuse);
    // TODO (nvkelso 2023-03-21)
    // This is a guess for urban areas
    registerSourceHandler("ne", landuse::processNe);
    registerSourceHandler("osm", landuse);

    var places = new Places();
    registerHandler(places);
    registerSourceHandler("ne", places::processNe);
    registerSourceHandler("osm", places);
    // TODO (nvkelso 2023-03-21)
    // This is a guess for high zoom neighbourhoods
    // registerSourceHandler("wof", places::processWof);

    var pois = new Pois();
    registerHandler(pois);
    registerSourceHandler("osm", pois);

    var roads = new Roads();
    registerHandler(roads);
    registerSourceHandler("ne", roads::processNe);
    registerSourceHandler("osm", roads);

    var transit = new Transit();
    registerHandler(transit);
    registerSourceHandler("osm", transit);

    var water = new Water();
    registerHandler(water);
    registerSourceHandler("ne", water::processNe);
    //registerSourceHandler("ne", water::processNeLabels);
    registerSourceHandler("osm", water);
    //registerSourceHandler("osm", water::processLabels);
    registerSourceHandler("osm_water", water::processOsm);

    var earth = new Earth();
    registerHandler(earth);
    registerSourceHandler("ne", earth::processNe);
    registerSourceHandler("osm_land", earth::processOsm);
  }

  @Override
  public String name() {
    return "Basemap";
  }

  @Override
  public String description() {
    return "An example overlay showing bicycle routes";
  }

  @Override
  public boolean isOverlay() {
    return false;
  }

  @Override
  public String attribution() {
    return """
      <a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>
      """.trim();
  }

  public static void main(String[] args) throws Exception {
    run(Arguments.fromArgsOrConfigFile(args));
  }

  static void run(Arguments args) throws Exception {
    args = args.orElse(Arguments.of("maxzoom", 15));

    Path dataDir = Path.of("data");
    Path sourcesDir = dataDir.resolve("sources");

    String area = args.getString("area", "geofabrik area to download", "monaco");

    // TODO (nvkelso 2023-03-21)
    // 1.  Register Who's On First data here (512 px zoom 11) for neighbourhoods
    //     - vector-datasource/data/wof_snapshot.py
    //     - vector-datasource/data/schema.sql
    // 2.  Register buffered_land here (512 px zoom 7) for maritime boundaries -- not exported in tiles!
    // 3.  Register admin_areas here (512 px zoom 4) for road network calculations & etc -- not exported in tiles!
    // 4.  osm2pgsql.lua polygon handling
    // 5.  osm2pgsql.lua tag (feature) allowlist handling
    // 6.  osm2pgsql.lua key (property) blocklist handling
    // 7.  osm2pgsql.lua disputed, claimed, somaliland handling
    // 8.  osm2pgsql.lua z_oder_lookup and handling
    // 9.  osm2pgsql.lua as_boolean handling (eg yes and true and 1)
    // 10. osm2pgsql.lua recast various OSM features for Point-of-view / disputed feature handling (many)
    // 11. osm2pgsql.lua Strips disputed tags off of ways
    // 12. osm2pgsql.lua Redefine extra disputed admin ways as administrative to avoid them
    // 13. osm2pgsql.lua Adds suppress any ways involved with claims. The claim relation will render instead for everyone.
    // 14. osm2pgsql.lua Adds dispute tags to ways in disputed relations
    // 15. osm2pgsql.lua Turn off admin 4 ways within Somaliland
    // 16. osm2pgsql.lua Mark some disputes as unrecognized to hide them by default
    // 17. osm2pgsql.lua Adds tags from boundary=claim relation to its ways
    // 18. osm2pgsql.lua Adds tags from boundary=disputed relation to its ways
    // 19. osm2pgsql.lua Adds tags to redefine Taiwan admin levels.
    // 20. osm2pgsql.lua Adds tags to redefine Israel admin 4 boundaries for Palestine.
    // 21. osm2pgsql.lua Add tags to redefine Hong Kong and Macau as admin 2 except for China which is Admin 4
    // 22. osm2pgsql.lua Convert admin_level 5 boundaries in Northern Cyprus to 4
    // 23. osm2pgsql.lua Convert admin_level 5 boundaries in Cyprus to 4
    // 24. osm2pgsql.lua Turn off West Bank and Judea and Samaria relations
    // 25. osm2pgsql.lua Fix Kosovo dispute viewpoints
    // 26. osm2pgsql.lua Turn off admin 4 relations within Somaliland
    // 27. osm2pgsql.lua handle lcn, rcn, ncn, lwn & etc cycle and walking networks
    // 28. Unclear where to put integration tests to ensure that input tags = output features in a given tile
    // 29. Any sql processing post import, see:
    //     - create_disputed_areas_pbf.sh
    //     - patch_disputes_into_pbf.sh
    //     - any "apply" SQL in vector-datasource/data/ (like apply-highway_99_fixes.sql)
    //       - these are generally orchestrated from: perform-sql-updates.sh
    // 30. vector-datasource/data/wikidata_merge.py
    // 31. Do any large sources (like admin_areas or osmdata) need tiling?
    //     - vector-datasource/data/tile-shapefile.py
    // 32. vector-datasource/data/functions.sql for all the magic

    Planetiler.create(args)
      .setDefaultLanguages(LANGUAGES)
      .setProfile(new Basemap())
      .addOsmSource("osm", Path.of("data", "sources", area + ".osm.pbf"), "geofabrik:" + area)
      .addNaturalEarthSource("ne", sourcesDir.resolve("natural_earth_vector.sqlite.zip"),
        "https://naciscdn.org/naturalearth/packages/natural_earth_vector.sqlite.zip")
      .addShapefileSource("osm_water", sourcesDir.resolve("water-polygons-split-3857.zip"),
        "https://osmdata.openstreetmap.de/download/water-polygons-split-3857.zip")
      .addShapefileSource("osm_land", sourcesDir.resolve("land-polygons-split-3857.zip"),
        "https://osmdata.openstreetmap.de/download/land-polygons-split-3857.zip")
      .setOutput(Path.of(area + ".pmtiles"))
      .run();
  }
}
