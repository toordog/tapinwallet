/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.tapinwallet.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigInteger;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IdentityCreateResponse(
        String did,
        Zkp zkp,
        Long ts,
        String pubKey
        ) {

    public record Zkp(
            String hash,
            Map<String, Object> params,
            Long expiry
            ) {

        @JsonIgnore
        public boolean isValid() {

            BigInteger a = new BigInteger(params.get("a").toString());
            BigInteger b = new BigInteger(params.get("b").toString());
            BigInteger c = new BigInteger(params.get("c").toString());
            BigInteger d = new BigInteger(params.get("d").toString());
            BigInteger expiry = new BigInteger(String.valueOf(expiry()));

            BigInteger commitment = a.add(b).add(c);
            BigInteger answer = commitment.subtract(expiry);
            
            return answer.equals(d);
        }

    }
}
