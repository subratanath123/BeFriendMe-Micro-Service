package net.befriendme.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Document
@Data
public class Event implements Serializable {

    @Id
    private String eventId;

    private String resourceId;

    private String message;

    private String externalUrl;

    private String eventType;

    private String domain;

    @NotEmpty
    @NotNull
    private String eventSource;

    @NotEmpty
    @NotNull
    private String version;

    @NotEmpty
    @NotNull
    private Map<String, String> payload;
    private Map<String, String> metadata;

    @NotEmpty
    private List<Audience> audienceList;
    private List<String> targetUserList;

    public Event(String resourceId, String domain, String eventSource,
                 List<Audience> audienceList,
                 List<String> targetUserList) {

        this.resourceId = resourceId;
        this.domain = domain;
        this.eventSource = eventSource;
        this.audienceList = audienceList;
        this.targetUserList = targetUserList;
    }

    public Event(String resourceId, String domain, String eventSource,
                 String message,
                 Map<String, String> payload,
                 List<Audience> audienceList,
                 List<String> targetUserList) {

        this.resourceId = resourceId;
        this.domain = domain;
        this.message = message;
        this.eventSource = eventSource;
        this.payload = payload;
        this.audienceList = audienceList;
        this.targetUserList = targetUserList;
    }

    public Event() {
        this.eventId = UUID.randomUUID().toString();
    }
}
