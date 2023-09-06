package net.befriendme.api.graphql.dao;

import net.befriendme.entity.user.Address;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AddressRepository extends PagingAndSortingRepository<Address, ObjectId> {

}
