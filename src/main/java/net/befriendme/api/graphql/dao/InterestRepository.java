package net.befriendme.api.graphql.dao;

import net.befriendme.entity.user.Interest;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InterestRepository extends PagingAndSortingRepository<Interest, ObjectId> {

}
