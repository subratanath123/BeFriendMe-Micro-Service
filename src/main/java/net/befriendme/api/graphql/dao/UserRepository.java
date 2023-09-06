package net.befriendme.api.graphql.dao;

import net.befriendme.entity.user.User;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, ObjectId> {

}
