package net.befriendme.api.common.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CustomHeaderRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public CustomHeaderRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        // Check if the header is in the custom headers
        if (customHeaders.containsKey(name)) {
            return customHeaders.get(name);
        }
        // If not found in custom headers, delegate to the original request
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // Create an enumeration of header names that includes custom headers and the original request headers
        Enumeration<String> originalHeaders = super.getHeaderNames();
        return new CustomHeaderEnumeration(originalHeaders, customHeaders.keySet());
    }
}
