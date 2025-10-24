/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.tapinwallet.util;

import com.fasterxml.jackson.annotation.JsonInclude;
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
            long expiry
            ) {

    }
}
