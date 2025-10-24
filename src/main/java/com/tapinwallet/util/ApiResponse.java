package com.tapinwallet.util;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic API Response Record.
 * Designed to handle varied response shapes while keeping strong typing for known fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    String status,
    String message,
    T body,
    String signature
) {

    // Optional: handle unknown top-level fields automatically
    private static final Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public static void handleUnknown(String key, Object value) {
        extra.put(key, value);
    }

    public Map<String, Object> extraFields() {
        return extra;
    }
    
}
