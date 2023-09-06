package net.befriendme.entity.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Content {

    @Id
    private String id;

    @DBRef
    private User creator;
    private ContentType contentType;
    private String text;
    private String imageUrl;
    private String videoUrl;
    private String documentUrl;

}
