# Protomaps Basemaps

This repository has two core parts:

* `tiles/`: A [Planetiler](https://github.com/onthegomap/planetiler) build profile that generates `planet.pmtiles` from OpenStreetMap and Natural Earth in 2-3 hours on a modest computer.
* `base/`: A TypeScript package that generates [MapLibre GL](http://github.com/maplibre) styles, in multiple color themes, that can be used via `npm` or exported as JSON.

# Usage

You will need [Maven](https://maven.apache.org/install.html) installed, which is available in most package managers.

Generate and inspect a basemap PMTiles of any named area:

1. Clone this repository.

```sh
git clone git@github.com:protomaps/basemaps.git
```

2. change to the `tiles` directory in the project, download dependencies and compile the JAR:

```sh
cd basemaps/tiles
mvn clean package
```

3. Download and generate `monaco.pmtiles` in the tiles directory:

Full command:

```
java -jar target/*-with-deps.jar --download --force --area=monaco
```

Or use Makefile targets for preset areas, including Monaco:

```sh
make monaco
```

6. Serve tiles as MVT for ease of development

Requires the [Go lang binary](https://github.com/protomaps/go-pmtiles/releases) to be installed, and served with ``--cors` headers which allow local host.

```sh
pmtiles serve --cors=* .
```

NOTE: Your path to pmtiles may need to be updated.

7. Switch to the `compare/` directory to run Stamne's [maperture](https://github.com/stamen/maperture) tool to view the tiles rendered in a style and compare with other basemaps

```sh
cd ../compare
npm run serve
```

## License

[BSD 3-clause](/LICENSE.md). The organization of layers and features used by these map styles, as well as the "look and feel" of the resulting maps, are licensed [CC0](https://creativecommons.org/publicdomain/zero/1.0/). However, maps using the [Protomaps web map service](https://protomaps.com) or another OpenStreetMap-based service will be subject to the terms of the [Open Database License](https://www.openstreetmap.org/copyright).
