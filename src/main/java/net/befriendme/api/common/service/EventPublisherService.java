package net.befriendme.api.common.service;

import net.befriendme.event.Event;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import static net.befriendme.api.common.utils.SerializationUtils.getSerializedJson;

@Component
public class EventPublisherService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void publishEvent(Event event) {
        event = mongoTemplate.insert(event);

        ObjectRecord<String, String> record = StreamRecords.newRecord()
                .ofObject(getSerializedJson(event))
                .withStreamKey("event:push:befriendme");

        redisTemplate.opsForStream().add(record);
    }
    
}
