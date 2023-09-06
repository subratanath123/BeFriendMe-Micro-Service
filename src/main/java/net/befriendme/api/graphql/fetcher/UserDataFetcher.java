package net.befriendme.api.graphql.fetcher;

import net.befriendme.api.common.utils.AuthUtils;
import net.befriendme.api.mongo.dao.MongoUserProfileDao;
import net.befriendme.entity.user.RelationShip;
import net.befriendme.entity.user.User;
import net.befriendme.entity.user.Visibility;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.befriendme.entity.common.Status.ACCEPTED;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class UserDataFetcher implements DataFetcher<User> {

    @Autowired
    private MongoUserProfileDao mongoUserProfileDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User get(DataFetchingEnvironment environment) throws Exception {
        String email = AuthUtils.getEmail();
        User user = mongoUserProfileDao.findByEmail(email);

        String targetUserId = environment.getArgument("id");

        Criteria criteriaDefinition = where("requesterId")
                .is(user.getId())
                .and("recipientId")
                .is(targetUserId)
                .and("status")
                .is(ACCEPTED.name());

        Query query = new Query(criteriaDefinition);

        List<RelationShip> relationShipList = mongoTemplate.find(query, RelationShip.class);

        if (!relationShipList.isEmpty()) {
            return mongoUserProfileDao.findById(relationShipList.get(0).getRecipientId())
                    .orElse(null);
        }

        return mongoUserProfileDao.findByIdAndVisibilityIs(targetUserId, Visibility.PUBLIC);
    }
}
