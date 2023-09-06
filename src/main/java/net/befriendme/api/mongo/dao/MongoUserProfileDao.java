package net.befriendme.api.mongo.dao;

import net.befriendme.entity.user.User;
import net.befriendme.entity.user.Visibility;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoUserProfileDao extends MongoRepository<User, String> {

    @Query(value = "{email: {$eq: ?0}}")
    User findByEmail(String email);

    User findByIdAndVisibilityIs(String id, Visibility visibility);
    List<User> findByIdInAndVisibilityIs(List<String> idList, Visibility visibility);

}
