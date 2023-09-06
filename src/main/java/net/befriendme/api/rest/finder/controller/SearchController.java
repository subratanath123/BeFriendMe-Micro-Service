package net.befriendme.api.rest.finder.controller;

import net.befriendme.api.redis.dao.CustomRedisBaseEntityDao;
import net.befriendme.entity.common.SearchParameters;
import net.befriendme.entity.user.redis.RedisBaseEntity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.data.redis.domain.geo.Metrics.KILOMETERS;

@RestController
@RequestMapping("/v1/api/")
@SecurityRequirement(name = "security_auth")
public class SearchController {

    @Autowired
    private CustomRedisBaseEntityDao customRedisBaseEntityDao;

    @PostMapping(value = "/search/all")
    public List<RedisBaseEntity> getSearch(@RequestBody SearchParameters searchParameters) {
        Point point = new Point(searchParameters.getLatitude(), searchParameters.getLongitude());
        return customRedisBaseEntityDao
                .findByLocationNear(
                        point,
                        new Distance(searchParameters.getRadius(), KILOMETERS),
                        PageRequest.of(searchParameters.getPageNumber(), searchParameters.getPageSize())
                );
    }

}
