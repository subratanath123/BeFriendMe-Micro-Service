package net.befriendme.api.mongo.dao;

import net.befriendme.entity.business.Business;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionPackageDao extends MongoRepository<Business, String> {

}