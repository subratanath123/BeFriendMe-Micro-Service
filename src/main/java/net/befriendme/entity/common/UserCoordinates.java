package net.befriendme.entity.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserCoordinates implements Serializable {

    public Double latitude;
    public Double longitude;

}