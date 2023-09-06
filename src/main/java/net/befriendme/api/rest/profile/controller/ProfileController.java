package net.befriendme.api.rest.profile.controller;

import com.mongodb.client.result.UpdateResult;
import net.befriendme.api.common.utils.AuthUtils;
import net.befriendme.api.mongo.dao.MongoUserProfileDao;
import net.befriendme.api.redis.dao.RedisBaseEntityDao;
import net.befriendme.entity.common.UserCoordinates;
import net.befriendme.entity.user.Address;
import net.befriendme.entity.user.Interest;
import net.befriendme.entity.user.SecurityQuestion;
import net.befriendme.entity.user.User;
import net.befriendme.entity.user.redis.RedisBaseEntity;
import net.befriendme.entity.user.redis.RedisUserMetaInfo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static net.befriendme.entity.user.redis.RedisUserMetaInfo.getRedisUserMetaInfoInstance;

@RestController
@RequestMapping("/v1/api/profile")
@SecurityRequirement(name = "security_auth")
public class ProfileController {

    @Autowired
    private MongoUserProfileDao mongoUserProfileDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, RedisBaseEntity> redisTemplate;

    @Autowired
    private RedisBaseEntityDao redisBaseEntityDao;

    private static Map<String, Class> availableAttributeToClassMap = new HashMap<>();

    static {

        availableAttributeToClassMap.put("interest", Interest.class);
        availableAttributeToClassMap.put("securityQuestion", SecurityQuestion.class);
    }

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public User getUser() {
        String email = AuthUtils.getEmail();
        User user = mongoUserProfileDao.findByEmail(email);

        if (!redisBaseEntityDao.existsById(email)) {
            RedisUserMetaInfo redisUserMetaInfo = getRedisUserMetaInfoInstance(user);

            redisBaseEntityDao.save(redisUserMetaInfo);
        }

        return user;
    }

    @PutMapping("/basic-information")
    public UpdateResult saveBasicUserProfile(@RequestBody User user) {
        String email = AuthUtils.getEmail();

        Query query = new Query(Criteria.where("email")
                .is(email));

        Update update = new Update()
                .set("displayName", user.getDisplayName())
                .set("aboutUser", user.getAboutUser())
                .set("addressList", user.getAddressList())
                .set("organization", user.getOrganization())
                .set("designation", user.getDesignation())
                .set("gender", user.getGender())
                .set("age", user.getAge());

        return mongoTemplate.updateFirst(query, update, User.class);
    }

    @PostMapping("/address/add")
    public User updateUSerAddress(@Valid @RequestBody Address address) {
        String email = AuthUtils.getEmail();

        Query query = new Query(Criteria.where("email")
                .is(email)
                .and("addressList.addressType")
                .is(address.getAddressType())
        );

        Update update = new Update()
                .set("addressList.$.address1", address.getAddress1())
                .set("addressList.$.city", address.getCity())
                .set("addressList.$.state", address.getState())
                .set("addressList.$.zip", address.getZip())
                .set("addressList.$.country", address.getCountry())
                .set("addressList.$.location", address.getLocation());

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);

        if (updateResult.getModifiedCount() == 0) {
            mongoTemplate.updateFirst(
                    new Query(Criteria.where("email").is(email)),
                    new Update().addToSet("addressList", address),
                    User.class
            );
        }

        return mongoUserProfileDao.findByEmail(email);
    }


    @PostMapping("/interestList/add")
    public UpdateResult updateUSerAddress(@Valid @RequestBody Interest interest) {
        String email = AuthUtils.getEmail();

        if (interest.getId() == null || interest.getId().length() == 0) {
            interest.setId(null); //use trimmer
            mongoTemplate.insert(interest);
        }

        Query query = new Query(Criteria.where("email")
                .is(email)
                .and("interestList")
                .nin(interest));

        Update update = new Update().addToSet("interestList", interest);

        return mongoTemplate.updateFirst(query, update, User.class);
    }

    @PutMapping(value = "/update/coordinates", consumes = "application/json")
    public ResponseEntity<String> updateUserLocation(@RequestBody UserCoordinates userCoordinates) {
        Point point = new Point(userCoordinates.getLongitude(), userCoordinates.getLatitude());

        String email = AuthUtils.getEmail();

        User user = mongoUserProfileDao.findByEmail(email);
        user.setCurrentLocation(point);
        mongoUserProfileDao.save(user);

        RedisUserMetaInfo redisBaseEntity = RedisUserMetaInfo.getRedisUserMetaInfoInstance(user);

        redisTemplate.opsForHash().put(
                User.COLLECTION_NAME,
                user.getEmail(),
                redisBaseEntity);

        return new ResponseEntity<>("Location Updated", HttpStatus.OK);
    }

}
