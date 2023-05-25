package com.protomaps.basemap.util;

import com.onthegomap.planetiler.util.Translations;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.onthegomap.planetiler.util.LanguageUtils.*;
import static com.onthegomap.planetiler.util.LanguageUtils.VALID_NAME_TAGS;

/**
 * Utilities to extract common name fields (name, name_en, name_de, name:latin, name:nonlatin, name_int) that the
 * Tilezen schema uses across any map element with a name.
 */
public class LanguageUtils {
    /**
     * Returns a map with default name attributes (name, name_en, name_de, name:latin, name:nonlatin, name_int) that every
     * element should have, derived from name, int_name, name:en, and name:de tags on the input element.
     *
     * <ul>
     * <li>name is the original name value from the element</li>
     * <li>name_en is the original name:en value from the element, or name if missing</li>
     * <li>name_de is the original name:de value from the element, or name/ name_en if missing</li>
     * <li>name:latin is the first of name, int_name, or any name: attribute that contains only latin characters</li>
     * <li>name:nonlatin is any nonlatin part of name if present</li>
     * <li>name_int is the first of int_name name:en name:latin name</li>
     * </ul>
     */
    public static Map<String, Object> getNamesWithoutTranslations(Map<String, Object> tags) {
        return getNames(tags, null);
    }

    // While Tilezen can technically support any and all arbitrary languages, in production that can result in very
    // large tile sizes, especially at zooms 0-8 when country and locality labels can have hundreds of translations.
    // To work around this, either add language filtering at edge before CDN, &/or limit which languages are exported
    // in tiles during the build (default method here).
    //
    // (nvkelso) 2023-04-11
    //           TODO: Enable boolean project config for limiting names, default true
    //
    // To make it easier to localize map styles on the web and on mobile, we prefer "2-character" language codes where,
    // possible – with notable exceptions for Chinese simplified and traditional.
    //
    // Data sources like Who’s On First which uses Library of Congress style language indications for names to specify a
    // 3-character code for the following locales as name_{locale} properties (so name_eng for English).
    //
    // Tilezen's list of core languages (below). Arabic, Chinese, English, French, Russian and Spanish are used by the
    // United Nations for meetings and official documents. The other languages listed are either proposed as official
    // language of the United Nations (Bengali, Hindi, Portugese, and Turkish) or frequently used in OpenStreetMap,
    // Natural Earth, Who's On First, or Wikipedia.
    //
    //  field_name   | 3-char | 2-char  | Language              | Native script
    //  ------------ | ------ | ------- | --------------------- | -----------------
    //  name_ar      | ara    | ar      | Arabic                | العربية
    //  name_bn      | ben    | bn      | Bengali               | বাংলা
    //  name_de      | deu    | de      | German                | Deutsch
    //  name_en      | eng    | en      | English               | English
    //  name_el      | ell    | el      | Greek (modern)        | ελληνικά
    //  name_fa      | fas    | fa      | Farsi                 | فارسی
    //  name_fr      | fra    | fr      | French                | français
    //  name_he      | heb    | he      | Hebrew                | עִבְרִית
    //  name_hi      | hin    | hi      | Hindi                 | हिन्दी
    //  name_hu      | hun    | hu      | Hungarian             | magyar
    //  name_id      | ind    | id      | Indonesian            | Bahasa Indonesia
    //  name_it      | ita    | it      | Italian               | italiano
    //  name_ja      | jpn    | ja      | Japanese              | 日本語
    //  name_ko      | kor    | ko      | Korean                | 한국어
    //  name_nl      | nld    | nl      | Dutch                 | Nederlands
    //  name_pl      | pol    | pl      | Polish                | Polski
    //  name_pt      | por    | pt      | Portuguese            | Português
    //  name_ru      | rus    | ru      | Russian               | Русский
    //  name_es      | spa    | es      | Spanish               | español
    //  name_sv      | swe    | sv      | Swedish               | Svenska
    //  name_tr      | tur    | tr      | Turkish               | Türkçe
    //  name_uk      | ukr    | uk      | Ukrainian             | українська
    //  name_ur      | urd    | ur      | Urdu                  | اردو
    //  name_vi      | vie    | vi      | Vietnamese            | Tiếng Việt
    //  name_zh      | zho    | zh      | Chinese (simplified)  | 中文
    //  name_zh-Hans | zho    | zh-Hans | Chinese (simplified)  | 中文
    //  name_zh-Hant | zho    | zh-Hant | Chinese (traditional) | 中文
    //
    //  NOTE:  Chinese prioritize indicated simplified or traditional but may be backfilled visa-versa.

    public static final List<String> LANGUAGES = List.of("ar", "bn", "de", "en", "el", "fa", "fr", "he",
            "hi", "hu", "id", "it", "ja", "ko", "nl", "pl", "pt", "ru", "es", "sv", "tr", "uk", "ur", "vi",
            "zh", "zh-Hans", "zh-Hant");


    /**
     * Returns a map with default name attributes that {@link #getNamesWithoutTranslations(Map)} adds, but also
     * translations for every language that {@code translations} is configured to handle.
     */
    public static Map<String, Object> getNames(Map<String, Object> tags, Translations translations) {
        Map<String, Object> result = new HashMap<>();

        String name = string(tags.get("name"));
        String intName = string(tags.get("int_name"));
        String nameEn = string(tags.get("name:en"));
        String nameDe = string(tags.get("name:de"));

        boolean isLatin = containsOnlyLatinCharacters(name);
        String latin = isLatin ? name :
                Stream
                        .concat(Stream.of(nameEn, intName, nameDe), getAllNameTranslationsBesidesEnglishAndGerman(tags))
                        .filter(com.onthegomap.planetiler.util.LanguageUtils::containsOnlyLatinCharacters)
                        .findFirst().orElse(null);
        if (latin == null && translations != null && translations.getShouldTransliterate()) {
            latin = transliteratedName(tags);
        }
        String nonLatin = isLatin ? null : removeLatinCharacters(name);
        if (coalesce(nonLatin, "").equals(latin)) {
            nonLatin = null;
        }

        putIfNotEmpty(result, "name", name);
        putIfNotEmpty(result, "name_en", coalesce(nameEn, name));
        putIfNotEmpty(result, "name_de", coalesce(nameDe, name, nameEn));
        putIfNotEmpty(result, "name:latin", latin);
        putIfNotEmpty(result, "name:nonlatin", nonLatin);
        putIfNotEmpty(result, "name_int", coalesce(
                intName,
                nameEn,
                latin,
                name
        ));

        if (translations != null) {
            translations.addTranslations(result, tags);
        }

        return result;
    }

    private static Stream<String> getAllNameTranslationsBesidesEnglishAndGerman(Map<String, Object> tags) {
        return tags.entrySet().stream()
                .filter(e -> !EN_DE_NAME_KEYS.contains(e.getKey()) && VALID_NAME_TAGS.test(e.getKey()))
                .map(Map.Entry::getValue)
                .map(com.onthegomap.planetiler.util.LanguageUtils::string);
    }
}
