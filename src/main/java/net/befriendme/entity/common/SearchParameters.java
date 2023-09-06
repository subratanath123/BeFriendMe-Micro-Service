package net.befriendme.entity.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchParameters implements Serializable {

    public Double latitude;
    public Double longitude;
    public Double radius;
    public String collectionName;
    public int pageNumber;
    public int pageSize;

}
