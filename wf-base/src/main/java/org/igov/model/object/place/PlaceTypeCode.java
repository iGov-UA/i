package org.igov.model.object.place;

/**
 * User: goodg_000
 * Date: 22.10.2015
 * Time: 23:56
 */
public enum PlaceTypeCode {

    OBLAST(1),  // область
    AREA(2),    // район
    CITY(3),    // город
    UTS(4),     // an urban-type settlement - поселок городского типа
    VILLAGE(5); // село

    private long id;

    PlaceTypeCode(long id) {
        this.id = id;
    }

    public static PlaceTypeCode getById(Long id) {
        PlaceTypeCode res = null;

        if (id == null) {
            return null;
        }

        for (PlaceTypeCode code : values()) {
            if (code.id == id) {
                res = code;
                break;
            }
        }

        return res;
    }
}
