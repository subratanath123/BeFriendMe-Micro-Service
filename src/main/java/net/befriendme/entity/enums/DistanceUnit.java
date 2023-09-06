package net.befriendme.entity.enums;

public enum DistanceUnit {

    KILOMETER("km"),
    CENTIMETER("cm"),
    MILE("mi"),
    FEET("ft");

    private String name;

    DistanceUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
