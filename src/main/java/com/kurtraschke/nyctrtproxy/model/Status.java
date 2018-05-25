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
    MULTI_MATCH;

    public boolean isMatch() {
        switch (this) {
            case BAD_TRIP_ID:
            case NO_TRIP_WITH_START_DATE:
            case NO_MATCH:
            case MERGED:
                return false;
            case LOOSE_MATCH_ON_OTHER_SERVICE_DATE:
            case LOOSE_MATCH_COERCION:
            case LOOSE_MATCH:
            case STRICT_MATCH:
            case MULTI_MATCH:
                return true;
        }
        return false;
    }
}