package com.tapinwallet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Map;

public final class IdentityRequestBuilder {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    private IdentityRequestBuilder() {
    }

    public static Map<String, Object> build(KeyPair keyPair, String scope) throws Exception {
        String pubKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        var data = Map.of(
                "scope", scope,
                "pubKey", pubKeyBase64
        );

        String bodyJson = mapper.writeValueAsString(data);
        String signature = CryptLite.sign(bodyJson, keyPair.getPrivate());
        
        return Map.of(
                "body", data,
                "signature", signature
        );
    }
}
