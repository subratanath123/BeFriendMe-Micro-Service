package net.befriendme.api.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.befriendme.api.common.utils.AuthUtils;
import net.befriendme.api.mongo.dao.MongoUserProfileDao;
import net.befriendme.entity.common.Status;
import net.befriendme.entity.user.RelationShip;
import net.befriendme.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

import static net.befriendme.entity.common.Status.ACCEPTED;
import static net.befriendme.entity.common.Status.PENDING;

@Component
public class FriendListFetcher implements DataFetcher<List<User>> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoUserProfileDao mongoUserProfileDao;

    @Override
    public List<User> get(DataFetchingEnvironment environment) throws Exception {
        String email = AuthUtils.getEmail();
        User user = mongoUserProfileDao.findByEmail(email);

        List<String> statusList = environment.getArgument("statusList");

        if (CollectionUtils.isEmpty(statusList)) {
            statusList = Arrays.asList(PENDING.name(), ACCEPTED.name());
        }

        List<Status> statusEnumList = statusList
                .stream()
                .map(Status::valueOf)
                .toList();

        Criteria statusFilterCriteria =  new Criteria()
                .orOperator(
                        Criteria.where("requesterId").is(user.getId()),
                        Criteria.where("recipientId").is(user.getId())
                ).and("status")
                .in(statusEnumList.toArray());

        Query query = new Query(statusFilterCriteria);

        List<RelationShip> relationShipList = mongoTemplate.find(query, RelationShip.class);

        Query userQuery = new Query(Criteria.where("id").in(
                relationShipList
                        .parallelStream()
                        .mapMulti((relationShip, consumer) -> {
                                    consumer.accept(relationShip.getRequesterId());
                                    consumer.accept(relationShip.getRecipientId());
                                }
                        )
                        .filter(id -> !id.equals(user.getId()))
                        .toList()
        ));

        return mongoTemplate.find(userQuery, User.class);
    }

}
