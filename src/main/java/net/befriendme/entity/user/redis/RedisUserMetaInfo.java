package net.befriendme.entity.user.redis;

import net.befriendme.entity.token.TokenResponse;
import net.befriendme.entity.user.User;
import lombok.Data;
import net.befriendme.entity.enums.DbCollectionCategory;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@RedisHash(User.COLLECTION_NAME)
public class RedisUserMetaInfo extends RedisBaseEntity implements Serializable {

    private String email;
    private List<TokenResponse> tokenResponseList;

    public RedisUserMetaInfo() {
    }

    public RedisUserMetaInfo(String email, String mongoId, String name) {
        super(mongoId, email, null, name, DbCollectionCategory.USER.name(), User.COLLECTION_NAME);
        this.email = email;
    }

    public RedisUserMetaInfo(String email, String mongoId, Point location, String name, String category, String type) {
        super(mongoId, email, location, name, category, type);
        this.email = email;
    }

    public static RedisUserMetaInfo getRedisUserMetaInfoInstance(User user) {
        return new RedisUserMetaInfo(user.getEmail(), user.getId(), user.getCurrentLocation(), user.getDisplayName(), DbCollectionCategory.USER.name(), User.COLLECTION_NAME);
    }

}
