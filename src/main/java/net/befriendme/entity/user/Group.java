package net.befriendme.entity.user;

import net.befriendme.entity.common.Status;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class Group {

    @Id
    private String id;
    private String name;
    private Status status;

    @DBRef
    private List<User> tagList;

    @DBRef
    private List<Post> postList;

    @DBRef
    private List<User> memberList;

}
