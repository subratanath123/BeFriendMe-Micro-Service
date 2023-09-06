package net.befriendme.entity.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class Page {

    @Id
    private String id;
    private String name;

    @DBRef
    private List<User> tagList;

    @DBRef
    private List<Post> postList;

    @DBRef
    private List<User> followerList;

}
