package net.befriendme.api.rest.resource.controller;

import net.befriendme.entity.user.Interest;
import net.befriendme.entity.user.SecurityQuestion;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "security_auth")
public class CommonResourceController {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static Map<String, Class> availableAttributeToClassMap = new HashMap<>();

    static {
        availableAttributeToClassMap.put("interest", Interest.class);
        availableAttributeToClassMap.put("securityQuestion", SecurityQuestion.class);
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/available/{attribute}")
    public <T> List<T> getAvailableAttributes(@PathVariable String attribute) {
        return mongoTemplate.findAll(availableAttributeToClassMap.get(attribute));
    }

}
