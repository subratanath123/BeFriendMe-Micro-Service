package net.befriendme.api.common.service;

import net.befriendme.api.mongo.dao.MongoUserProfileDao;
import net.befriendme.api.redis.dao.RedisBaseEntityDao;
import net.befriendme.entity.user.User;
import net.befriendme.entity.user.redis.RedisBaseEntity;
import net.befriendme.entity.user.redis.RedisUserMetaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    @Autowired
    private MongoUserProfileDao mongoUserProfileDao;

    @Autowired
    private RedisBaseEntityDao redisBaseEntityDao;

    public String getUserId(String email) {

        if (redisBaseEntityDao.existsById(email)) {
            return redisBaseEntityDao.findById(email).map(RedisBaseEntity::getMongoId).orElse(null);

        } else {
            User user = mongoUserProfileDao.findByEmail(email);

            RedisUserMetaInfo redisUserMetaInfo = RedisUserMetaInfo.getRedisUserMetaInfoInstance(user);
            redisBaseEntityDao.save(redisUserMetaInfo);

            return user.getId();
        }
    }
}
