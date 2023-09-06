package net.befriendme.entity.enums;

import net.befriendme.entity.business.Business;
import net.befriendme.entity.user.User;

public enum DbCollectionCategory {

    USER("User", User.class),
    BUSINESS("Business", Business.class);

    private String name;
    private Class clazz;

    DbCollectionCategory(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class getClazz() {
        return clazz;
    }
}
