package net.befriendme.entity.user;

import lombok.NoArgsConstructor;
import net.befriendme.entity.common.Status;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static net.befriendme.entity.user.RelationShip.COLLECTION_NAME;

@Document(COLLECTION_NAME)
@Data
@NoArgsConstructor
public class RelationShip {

    public static final String COLLECTION_NAME = "relationShip";

    @Id
    private String id;

    private String requesterId;

    private RelationType relationType;

    private String recipientId;

    private Status status;

    public RelationShip(RelationType relationType) {
        this.relationType = relationType;
    }
}
