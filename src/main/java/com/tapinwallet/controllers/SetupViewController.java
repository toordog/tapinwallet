package com.tapinwallet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.ApiResponse;
import com.tapinwallet.util.CryptLite;
import com.tapinwallet.util.IdentityCreateResponse;
import com.tapinwallet.util.IdentityRequestBuilder;
import com.tapinwallet.util.PropertyUtil;
import com.tapinwallet.util.tinydb.Database;
import com.tapinwallet.util.tinydb.DynamicEntity;
import com.tapinwallet.util.tinydb.TinyDB;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;

public class SetupViewController extends BaseController implements AppShellController.HasHost {

    String url = "http://10.25.1.198:8888/cp/identity/create";

    private AppShellController host;

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

    @Override
    public void onAppContextAvailable() {
        
    }
    
    @FXML
    private void handleCreateWallet() throws Exception {

        // Generate an EC key pair for testing
        var keyPair = CryptLite.generateKeyPair();

        // Build the request body using your lightweight helper
        var requestBody = IdentityRequestBuilder.build(keyPair, "tapin");

        // Standard JSON headers
        var headers = Map.of("Content-Type", "application/json");

        // Send
        ApiResponse<IdentityCreateResponse> response
                = sendRequest(url, "POST", headers, requestBody, IdentityCreateResponse.class);

        var artifact = response.headers().get("x-bitcrumb-artifact").getFirst();
        var identifier = response.headers().get("x-bitcrumb-identifier").getFirst();

        System.out.println("X-BITCRUMB_ARTIFACT: " + artifact + "\n");
        System.out.println("X-BITCRUMB-IDENTIFIER: " + identifier + "\n");

        // Deserialize JSON into the record
//        System.out.println("Response:");

//        // Access specific fields
//        System.out.println("DID: " + response.body().did());

        IdentityCreateResponse icr = response.body();
//        Map<String, String> params = (Map) icr.zkp().params();

//        System.out.println("ZKP Valid: " + icr.zkp().isValid());
//        System.out.println("Hash: " + hash);
//        System.out.println("Status: " + response.status());
//        System.out.println("Signature: " + response.signature());
//        System.out.println("Identifier: " + identifier);

        DynamicEntity id = ctx.context.create("Identity");
        id.set("did", response.body().did());
        id.set("name", "Michael Marquez");
        id.set("zkp", icr.zkp());
        id.set("artifact", artifact);
        id.set("identifier", identifier);
        
        id.persist();
        
        PropertyUtil.set("default", id.getId());
        ctx.id = id.getId();
        
        DynamicEntity proofmod = ctx.context.create("AppMod");
        proofmod.set("name", "Proof Manager");
        proofmod.set("hash", CryptLite.sha512Random());
        
        proofmod.persist();
        
        if (host != null) {
            host.goToHome();
        }
    }

    private final static ObjectMapper mapper = new ObjectMapper();

    public static <T> ApiResponse<T> sendRequest(String url, String method, Map<String, String> headers, Object body, Class<T> bodyType
    ) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30));

        if (headers != null) {
            headers.forEach(builder::header);
        }

        String json = (body != null) ? mapper.writeValueAsString(body) : "";

        switch (method.toUpperCase()) {
            case "POST" ->
                builder.POST(HttpRequest.BodyPublishers.ofString(json));
            case "PUT" ->
                builder.PUT(HttpRequest.BodyPublishers.ofString(json));
            case "PATCH" ->
                builder.method("PATCH", HttpRequest.BodyPublishers.ofString(json));
            case "DELETE" -> {
                if (!json.isEmpty()) {
                    builder.method("DELETE", HttpRequest.BodyPublishers.ofString(json));
                } else {
                    builder.DELETE();
                }
            }
            default ->
                builder.GET();
        }

        // Send the request and get the raw string response
        HttpResponse<String> rawResponse = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        // Deserialize the body into T
        ApiResponse<T> deserializedBody = mapper.readValue(
                rawResponse.body(),
                mapper.getTypeFactory().constructParametricType(ApiResponse.class, bodyType)
        );

        return new ApiResponse<>(
                deserializedBody.status(),
                deserializedBody.message(),
                deserializedBody.body(),
                rawResponse.headers().map(),
                deserializedBody.signature()
        );
    }

}
