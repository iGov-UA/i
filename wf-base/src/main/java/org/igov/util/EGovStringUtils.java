package org.igov.util;

/**
 * User: goodg_000
 * Date: 01.11.2015
 * Time: 14:53
 */
public final class EGovStringUtils {
    private EGovStringUtils() {
    }

    /**
     * @param obj object to convert to string
     * @return if obj == null then returns null, otherwise obj.toString
     */
    public static String toStringWithBlankIfNull(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }
}
