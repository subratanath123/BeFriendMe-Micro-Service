package net.befriendme.api.mongo.dao;

import net.befriendme.entity.business.Business;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusinessDao extends MongoRepository<Business, String> {

    Business findByOwnerId(String ownerId);

}