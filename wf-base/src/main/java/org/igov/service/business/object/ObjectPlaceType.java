package org.igov.service.business.object;

public enum ObjectPlaceType {
    COUNTRY(1,"страна"),
    REGION(2,"область"),
    AREA_DISTRICT(3,"район области"),
    LOCALITY(4,"населенный пункт"),
    TERRITORIAL_ASSOCIATION(5,"территориальное объединение"),
    RESIDENTIAL_AREA(7,"жилой массив/микрорайон"),
    STREET(8,"улица"),
    HOUSE(9,"дом"),
    HOUSING(10,"корпус"),
    APARTMENT(11,"квартира"),
    EXCLUSION(13,"адрес-исключение");

    private String descr;
    private Integer id;
    
    private ObjectPlaceType(int id, String descr) {
        this.id = id;
        this.descr = descr;
    }
    
    public static ObjectPlaceType getById(int id) {
	ObjectPlaceType res = null;
        for (ObjectPlaceType code : values()) {
            if (code.id == id) {
                res = code;
                break;
            }
        }
        return res;
    }

    public int getId() {
        return this.id;
    }
    
    public String getIdString() {
        return this.id.toString();
    }

    public String getDescription() {
        return this.descr;
    }
}
