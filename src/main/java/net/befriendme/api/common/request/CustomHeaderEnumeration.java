package net.befriendme.api.common.request;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

public class CustomHeaderEnumeration implements Enumeration<String> {

    private final Enumeration<String> originalEnumeration;
    private final Iterator<String> customHeadersIterator;

    public CustomHeaderEnumeration(Enumeration<String> originalEnumeration, Set<String> customHeaders) {
        this.originalEnumeration = originalEnumeration;
        this.customHeadersIterator = customHeaders.iterator();
    }

    @Override
    public boolean hasMoreElements() {
        return originalEnumeration.hasMoreElements() || customHeadersIterator.hasNext();
    }

    @Override
    public String nextElement() {
        if (originalEnumeration.hasMoreElements()) {
            return originalEnumeration.nextElement();
        }
        return customHeadersIterator.next();
    }
}
