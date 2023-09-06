package net.befriendme.api.common.utils;

import net.befriendme.event.Audience;
import net.befriendme.event.Event;

import java.util.List;
import java.util.Map;

public class EventUtils {

    public static Event createEvent(String resourceId,
                                    String domain, String eventSource,
                                    String message,
                                    Map<String, String> payload,
                                    List<Audience> audienceList,
                                    List<String> targetUserList) {

        return new Event(resourceId, domain, eventSource, message, payload, audienceList, targetUserList);
    }

    public static Event createEvent(String resourceId,
                                    String domain, String eventSource,
                                    Map<String, String> payload,
                                    List<Audience> audienceList) {

        return new Event(resourceId, domain, eventSource, null, payload, audienceList, null);
    }

    public static Event createEvent(String resourceId,
                                    String domain, String eventSource,
                                    Map<String, String> payload,
                                    List<Audience> audienceList,
                                    List<String> targetUserList) {

        return new Event(resourceId, domain, eventSource, null, payload, audienceList, targetUserList);
    }

}
