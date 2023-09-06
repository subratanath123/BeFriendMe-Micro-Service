package net.befriendme.api.rest.ralationship.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import net.befriendme.api.common.service.EventPublisherService;
import net.befriendme.api.common.utils.AuthUtils;
import net.befriendme.api.common.utils.EventUtils;
import net.befriendme.api.mongo.dao.MongoUserProfileDao;
import net.befriendme.entity.common.Status;
import net.befriendme.entity.user.RelationShip;
import net.befriendme.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.singletonList;
import static net.befriendme.entity.common.Status.ACCEPTED;
import static net.befriendme.entity.user.RelationType.FOLLOWER;
import static net.befriendme.entity.user.RelationType.FRIEND;
import static net.befriendme.event.Audience.TARGET_USER;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@RestController
@RequestMapping("/v1/api/follower")
@SecurityRequirement(name = "security_auth")
public class FollowController {

    @Autowired
    private MongoUserProfileDao mongoUserProfileDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EventPublisherService eventPublisherService;

    @GetMapping("/follow/{targetUserId}")
    public ResponseEntity<?> addFollower(@PathVariable String targetUserId) {
        String email = AuthUtils.getEmail();
        User user = mongoUserProfileDao.findByEmail(email);

        Query query = new Query(where("requesterId")
                .is(user.getId())
                .and("recipientId")
                .is(targetUserId)
                .and("relationType")
                .is(FOLLOWER));

        if (!mongoTemplate.exists(query, RelationShip.class)) {

            RelationShip relationShip = new RelationShip(FOLLOWER);
            relationShip.setRequesterId(user.getId());
            relationShip.setRecipientId(targetUserId);
            relationShip.setStatus(ACCEPTED);
            relationShip = mongoTemplate.insert(relationShip);

            eventPublisherService.publishEvent(
                    EventUtils.createEvent(user.getId(),
                            "user",
                            "befriendme",
                            user.getDisplayName() + " followed you",
                            null,
                            List.of(TARGET_USER),
                            singletonList(targetUserId))
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(relationShip);
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Failed");
        }
    }

    @GetMapping("/unfollow/{targetUserId}")
    public ResponseEntity<?> unfollow(@PathVariable String targetUserId) {
        String email = AuthUtils.getEmail();
        User user = mongoUserProfileDao.findByEmail(email);

        Query query = new Query(where("requesterId")
                .is(user.getId())
                .and("recipientId")
                .is(targetUserId)
                .and("relationType")
                .is(FOLLOWER)
                .and("status")
                .is(ACCEPTED));

        mongoTemplate.remove(query, RelationShip.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("success");
    }

}
