package net.befriendme.api.rest.business;

import com.mongodb.client.result.UpdateResult;
import net.befriendme.api.common.service.CommonService;
import net.befriendme.api.common.utils.AuthUtils;
import net.befriendme.api.mongo.dao.BusinessDao;
import net.befriendme.entity.business.Business;
import net.befriendme.entity.user.Address;
import net.befriendme.entity.user.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/business")
@SecurityRequirement(name = "security_auth")
public class BusinessController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BusinessDao businessDao;

    @Autowired
    private CommonService commonService;

    @GetMapping
    public Business getUser() {
        String email = AuthUtils.getEmail();
        String userId = commonService.getUserId(email);

        return businessDao.findByOwnerId(userId);
    }

    @PostMapping("/create")
    public Business createBusinessProfile(@Valid @RequestBody Business business) {
        String email = AuthUtils.getEmail();

        String userId = commonService.getUserId(email);
        business.setOwner(new User(userId));

        return businessDao.save(business);
    }

    @PutMapping("/basic-information")
    public UpdateResult updateBasicInformation(@Valid @RequestBody Business business) {
        String email = AuthUtils.getEmail();
        String userId = commonService.getUserId(email);

        Query query = new Query(Criteria.where("owner._id")
                .is(userId));

        Update update = new Update()
                .set("businessLogoUrl", business.getBusinessLogoUrl())
                .set("coverPhotoUrl", business.getCoverPhotoUrl())
                .set("businessName", business.getBusinessName())
                .set("businessStartingYear", business.getBusinessStartingYear())
                .set("businessCategoryId", business.getBusinessCategoryId())
                .set("businessEmail", business.getBusinessEmail())
                .set("businessWebsite", business.getBusinessWebsite())
                .set("businessType", business.getBusinessType())
                .set("businessNumber", business.getBusinessNumber())
                .set("aboutBusiness", business.getAboutBusiness())
                .set("businessLicenseNumber", business.getBusinessLicenseNumber())
                .set("dialCode", business.getDialCode())
                .set("isoCode", business.getIsoCode())
                .set("address", business.getAddress());

        return mongoTemplate.updateFirst(query, update, Business.class);
    }

    @PostMapping("/address")
    public Business updateUSerAddress(@Valid @RequestBody Address address) {
        String email = AuthUtils.getEmail();
        String userId = commonService.getUserId(email);

        Query query = new Query(
                Criteria.where("owner._id")
                        .is(userId)
        );

        Update update = new Update()
                .set("address.$.address1", address.getAddress1())
                .set("address.$.city", address.getCity())
                .set("address.$.state", address.getState())
                .set("address.$.zip", address.getZip())
                .set("address.$.country", address.getCountry())
                .set("address.$.location", address.getLocation());

        mongoTemplate.updateFirst(query, update, User.class);

        return businessDao.findByOwnerId(userId);
    }

}
