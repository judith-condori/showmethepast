package com.tesis.yudith.showmethepast.configuration;

import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;

import java.util.Locale;

public class LanguageManager {

    public static final String ENGLISH_ISO3_LANGUAGE = "eng";
    public static final String SPANISH_ISO3_LANGUAGE = "spa";

    public static final String ENGLISH_SUB_FIELD_NAME = "english";
    public static final String SPANISH_SUB_FIELD_NAME = "spanish";

    public static String translate(MultiLanguageString multiLanguageString) {
        String language = Locale.getDefault().getISO3Language();
        if (language.equals(SPANISH_ISO3_LANGUAGE)) {
            return multiLanguageString.getSpanish();
        }
        return multiLanguageString.getEnglish();
    }

    public static String adaptFieldName(String fieldName) {
        String language = Locale.getDefault().getISO3Language();
        String result;

        if (language.equals(ENGLISH_ISO3_LANGUAGE)) {
            result = String.format("%s.%s", fieldName, ENGLISH_SUB_FIELD_NAME);
        } else {
            result = String.format("%s.%s", fieldName, SPANISH_SUB_FIELD_NAME);
        }

        return result;
    }
}
