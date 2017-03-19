package org.igov.model.subject;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 13:29
 */
public enum SubjectHumanIdType {
    INN,            // 0
    Passport,       // 1
    Email,          // 2
    Phone;          // 3

    public static SubjectHumanIdType fromId(int id) {
        if (id < 0 || id > values().length - 1) {
            throw new IllegalArgumentException(String.format("'%s' is not Valid SubjectHumanIdType", id));
        }

        return values()[id];
    }

    public int getId() {
        return ordinal();
    }
}
