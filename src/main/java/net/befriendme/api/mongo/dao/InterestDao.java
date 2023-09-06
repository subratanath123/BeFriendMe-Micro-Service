package net.befriendme.api.mongo.dao;

import net.befriendme.entity.user.Interest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InterestDao extends MongoRepository<Interest, String> {

}