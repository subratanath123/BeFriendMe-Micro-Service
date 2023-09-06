package net.befriendme.api.redis.dao;

import net.befriendme.entity.user.redis.RedisBaseEntity;
import net.befriendme.entity.user.redis.RedisUserMetaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomRedisBaseEntityDao {

    @Autowired
    private RedisBaseEntityDao redisBaseEntityDao;

    public List<RedisBaseEntity> findByLocationNear(Point point, Distance distance, Pageable pageable) {
        List<RedisBaseEntity> allRedisBaseEntityList = new ArrayList<>();

        List<RedisUserMetaInfo> redisUserMetaInfos = redisBaseEntityDao.findByLocationNear(point, distance, pageable);
        allRedisBaseEntityList.addAll(redisUserMetaInfos);

        return allRedisBaseEntityList;
    }

}
