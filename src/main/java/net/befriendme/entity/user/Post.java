package net.befriendme.entity.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class Post {

    @Id
    private String id;

    @DBRef
    private User creator;

    @DBRef
    private Group group;

    @DBRef
    private Page page;

    @DBRef
    private List<Content> contentList;

    private Visibility privacy;

}
