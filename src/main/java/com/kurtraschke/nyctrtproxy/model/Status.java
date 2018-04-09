package com.kurtraschke.nyctrtproxy.model;

/**
 * Created by lcaraballo on 4/9/18.
 */
public enum Status {
    BAD_TRIP_ID,
    NO_TRIP_WITH_START_DATE,
    NO_MATCH,
    MERGED,
    LOOSE_MATCH_ON_OTHER_SERVICE_DATE,
    LOOSE_MATCH_COERCION,
    LOOSE_MATCH,
    STRICT_MATCH,
    MULTI_MATCH
};