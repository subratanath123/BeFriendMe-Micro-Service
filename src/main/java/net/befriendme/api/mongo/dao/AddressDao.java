package net.befriendme.api.mongo.dao;

import net.befriendme.entity.user.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressDao extends MongoRepository<Address, String> {
}