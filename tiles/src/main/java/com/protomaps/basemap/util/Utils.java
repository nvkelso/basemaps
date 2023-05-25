package com.protomaps.basemap.util;

import com.onthegomap.planetiler.util.Parse;

import java.util.Map;

/**
 * Common utilities for working with data and the OpenMapTiles schema in {@code layers} implementations.
 */
public class Utils {

    public static <T> T coalesce(T a, T b) {
        return a != null ? a : b;
    }

    public static <T> T coalesce(T a, T b, T c) {
        return a != null ? a : b != null ? b : c;
    }

    public static <T> T coalesce(T a, T b, T c, T d) {
        return a != null ? a : b != null ? b : c != null ? c : d;
    }

    public static <T> T coalesce(T a, T b, T c, T d, T e) {
        return a != null ? a : b != null ? b : c != null ? c : d != null ? d : e;
    }

    public static <T> T coalesce(T a, T b, T c, T d, T e, T f) {
        return a != null ? a : b != null ? b : c != null ? c : d != null ? d : e != null ? e : f;
    }

    /** Boxes {@code a} into an {@link Integer}, or {@code null} if {@code a} is {@code nullValue}. */
    public static Long nullIfLong(long a, long nullValue) {
        return a == nullValue ? null : a;
    }

    /** Boxes {@code a} into a {@link Long}, or {@code null} if {@code a} is {@code nullValue}. */
    public static Integer nullIfInt(int a, int nullValue) {
        return a == nullValue ? null : a;
    }

    /** Returns {@code a}, or null if {@code a} is "". */
    public static String nullIfEmpty(String a) {
        return (a == null || a.isEmpty()) ? null : a;
    }

    /** Returns true if {@code a} is null, or its {@link Object#toString()} value is "". */
    public static boolean nullOrEmpty(Object a) {
        return a == null || a.toString().isEmpty();
    }

//    def quantize_val(val, step):
//            # special case: if val is very small, we don't want it rounding to zero, so
//            # round the smallest values up to the first step.
//    if val < step:
//            return int(step)
//
//    result = int(step * round(val / float(step)))
//            return result
//
//
//    def quantize_height_round_nearest_5_meters(height):
//            return quantize_val(height, 5)
//
//
//    def quantize_height_round_nearest_10_meters(height):
//            return quantize_val(height, 10)
//
//
//    def quantize_height_round_nearest_20_meters(height):
//            return quantize_val(height, 20)
//
//
//    def quantize_height_round_nearest_meter(height):
//            return round(height)

//    def _to_float_meters(x):
//            if x is None:
//            return None
//
//            as_float = to_float(x)
//    if as_float is not None:
//            return as_float
//
//    # trim whitespace to simplify further matching
//    x = x.strip()
//
//            # try looking for a unit
//    unit_match = unit_pattern.match(x)
//            if unit_match is not None:
//    value = unit_match.group(1)
//    units = unit_match.group(2)
//    value_as_float = to_float(value)
//        if value_as_float is not None:
//            return value_as_float * unit_conversion_factor[units]
//
//            # try if it looks like an expression in feet via ' "
//    feet_match = feet_pattern.match(x)
//            if feet_match is not None:
//    feet = feet_match.group(1)
//    inches = feet_match.group(2)
//    feet_as_float = to_float(feet)
//    inches_as_float = to_float(inches)
//
//    total_inches = 0.0
//    parsed_feet_or_inches = False
//        if feet_as_float is not None:
//    total_inches = feet_as_float * 12.0
//    parsed_feet_or_inches = True
//        if inches_as_float is not None:
//    total_inches += inches_as_float
//            parsed_feet_or_inches = True
//        if parsed_feet_or_inches:
//            # international inch is exactly 25.4mm
//            meters = total_inches * 0.0254
//            return meters
//
//    # try and match the first number that can be parsed
//    for number_match in number_pattern.finditer(x):
//    potential_number = number_match.group(1)
//    as_float = to_float(potential_number)
//        if as_float is not None:
//            return as_float
//
//    return None

    /** Returns a map with {ele} (meters) and {ele_ft} attributes from an OpenStreetMap elevation in meters. */
    public static Map<String, Object> elevationTags(double meters) {
        return Map.of(
            "ele", (int) Math.round(meters),
            "ele_ft", (int) Math.round(meters * 3.2808399)
        );
    }

    /**
     * Returns an Integer with {ele} (meters) attributes from an elevation string in meters,
     * if {meters} can be parsed as a valid number and is in a reasonable range of values
     */
    public static Integer elevationTag(String meters) {
        Double ele = Parse.meters(meters);
        return ele == null ? Map.of() : elevationTags(ele);
    }

   /**
    * Returns an int with {height} (meters) from an height string in meters,
    * if {meters} can be parsed as a valid number (backfilled with {height_ft})
    * and is in a reasonable range of values
    */
   public static Integer heightTag(String meters) {
       Double height = Parse.meters(meters);
       return height == null ? Map.of() : heightTags(height);
   }

    /** Returns a map with {ele} (meters) and {ele_ft} attributes from an OpenStreetMap elevation in meters. */
    public static Map<String, Object> heightTags(double meters) {
        return Map.of(
                "height", (int) Math.round(meters),
                "height_ft", (int) Math.round(meters * 3.2808399)
        );
    }


    //props['height'] = _to_float_meters(height)

//    def elevation_to_meters(shape, props, fid, zoom):
//            """
//    If the properties has an "elevation" entry, then convert that to meters.
//    """
//
//    elevation = props.get('elevation')
//            if not elevation:
//            return shape, props, fid
//
//    props['elevation'] = _to_float_meters(elevation)
//    return shape, props, fid

//    def _building_calc_levels(levels):
//    levels = max(levels, 1)
//    levels = (levels * 3) + 2
//            return levels
//
//
//    def _building_calc_min_levels(min_levels):
//    min_levels = max(min_levels, 0)
//    min_levels = min_levels * 3
//            return min_levels
//
//
//# slightly bigger than the tallest structure in the world. at the time
//# of writing, the Burj Khalifa at 829.8m. this is used as a check to make
//# sure that nonsense values (e.g: buildings a million meters tall) don't
//            # make it into the data.
//            TALLEST_STRUCTURE_METERS = 1000.0
//
//
//    def _building_calc_height(height_val, levels_val, levels_calc_fn):
//    height = _to_float_meters(height_val)
//    if height is not None and 0 <= height <= TALLEST_STRUCTURE_METERS:
//            return height
//            levels = _to_float_meters(levels_val)
//    if levels is None:
//            return None
//            levels = levels_calc_fn(levels)
//    if 0 <= levels <= TALLEST_STRUCTURE_METERS:
//            return levels
//    return None

//    def building_height(shape, properties, fid, zoom):
//    height = _building_calc_height(
//            properties.get('height'), properties.get('building_levels'),
//    _building_calc_levels)
//            if height is not None:
//    properties['height'] = height
//    else:
//            properties.pop('height', None)
//            return shape, properties, fid
//
//
//    def building_min_height(shape, properties, fid, zoom):
//    min_height = _building_calc_height(
//            properties.get('min_height'), properties.get('building_min_levels'),
//    _building_calc_min_levels)
//            if min_height is not None:
//    properties['min_height'] = min_height
//    else:
//            properties.pop('min_height', None)
//            return shape, properties, fid
//
//
//    def synthesize_volume(shape, props, fid, zoom):
//    area = props.get('area')
//    height = props.get('height')
//            if area is not None and height is not None:
//    props['volume'] = int(area * height)
//    return shape, props, fid
//
//
//    def building_trim_properties(shape, properties, fid, zoom):
//    properties = _remove_properties(
//            properties,
//        'building', 'building_part',
//                'building_levels', 'building_min_levels')
//    return shape, properties, fid
//
//
//    def road_classifier(shape, properties, fid, zoom):
//    source = properties.get('source')
//            assert source, 'Missing source in road query'
//            if source == 'naturalearthdata.com':
//            return shape, properties, fid
//
//    properties.pop('is_link', None)
//            properties.pop('is_tunnel', None)
//            properties.pop('is_bridge', None)
//
//    kind_detail = properties.get('kind_detail', '')
//    tunnel = properties.get('tunnel', '')
//    bridge = properties.get('bridge', '')
//
//            if kind_detail.endswith('_link'):
//    properties['is_link'] = True
//    if tunnel in ('yes', 'true'):
//    properties['is_tunnel'] = True
//    if bridge and bridge != 'no':
//    properties['is_bridge'] = True
//
//    return shape, properties, fid

//    def place_population_int(shape, properties, fid, zoom):
//    population_str = properties.pop('population', None)
//    population = to_float(population_str)
//    if population is not None:
//    properties['population'] = int(population)
//    return shape, properties, fid
//
//
//    def _calculate_population_rank(population):
//    population = to_float(population)
//    if population is None:
//    population = 0
//            else:
//    population = int(population)
//    pop_breaks = [
//            1000000000,
//            100000000,
//            50000000,
//            20000000,
//            10000000,
//            5000000,
//            1000000,
//            500000,
//            200000,
//            100000,
//            50000,
//            20000,
//            10000,
//            5000,
//            2000,
//            1000,
//            200,
//            0,
//            ]
//            for i, pop_break in enumerate(pop_breaks):
//            if population >= pop_break:
//    rank = len(pop_breaks) - i
//            break
//                    else:
//    rank = 0
//            return rank
//
//
//    def population_rank(shape, properties, fid, zoom):
//    population = properties.get('population')
//    properties['population_rank'] = _calculate_population_rank(population)
//    return (shape, properties, fid)

}
