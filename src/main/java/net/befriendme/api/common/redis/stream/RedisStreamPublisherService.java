package net.befriendme.api.common.redis.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RedisStreamPublisherService {

    private Logger log = LoggerFactory.getLogger(RedisStreamPublisherService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public RecordId produce(String streamKey, Object data) {
        ObjectRecord<String, Object> record = StreamRecords.newRecord()
                .ofObject(data)
                .withStreamKey(streamKey);

        RecordId recordId = this.redisTemplate.opsForStream()
                .add(record);

        if (Objects.isNull(recordId)) {
            log.info("error sending event for streamKey: {}", streamKey);
            return null;
        }

        log.info("publishing new event for streamKey: {}", streamKey);

        return recordId;
    }

}
