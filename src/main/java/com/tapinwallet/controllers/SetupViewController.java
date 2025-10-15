package com.tapinwallet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tapinwallet.ConfigManager;
import com.tapinwallet.util.ApiResponse;
import com.tapinwallet.util.CryptLite;
import com.tapinwallet.util.IdentityRequestBuilder;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import javafx.fxml.FXML;

public class SetupViewController implements AppShellController.HasHost {

    String url = "http://localhost:8888/cp/identity/create";
    
    private static final ObjectMapper mapper = new ObjectMapper();
    private AppShellController host;

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
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
        var response = sendRequest(url, "POST", headers, requestBody);
        var jsonString = response.body();
        
        var artifact = response.headers().firstValue("x-bitcrumb-artifact");
        var identifier = response.headers().firstValue("x-bitcrumb-identifier");
        
        //System.out.println("X-BITCRUMB_ARTIFACT: "+artifact.get()+"\n");
        //System.out.println("X-BITCRUMB-IDENTIFIER: "+identifier.get()+"\n");

        ObjectMapper mapper = new ObjectMapper();

        // Deserialize JSON into the record
        ApiResponse data = mapper.readValue(jsonString, ApiResponse.class);

        System.out.println("Response:");
        
        // Pretty print
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)+"\n\n\n");
        
        // Access specific fields
        String did = data.body().get("did").toString();
        System.out.println("DID: "+did);
        
        Map<String,?> zkp = (Map)data.body().get("zkp");
        Map<String,String> params = (Map)zkp.get("params");
        String hash = zkp.get("hash").toString();

        BigInteger a = new BigInteger(params.get("a"));
        BigInteger b = new BigInteger(params.get("b"));
        BigInteger c = new BigInteger(params.get("c"));
        BigInteger expiry = new BigInteger(((Map)data.body().get("zkp")).get("expiry").toString());
        
        BigInteger d = new BigInteger(params.get("d"));
        
        BigInteger commitment = a.add(b).add(c);
        BigInteger answer = commitment.subtract(expiry);
        
        System.out.println("ZKP Valid: "+answer.equals(d));
        System.out.println("Hash: "+hash);
        System.out.println("Status: "+data.status());
        System.out.println("Signature: " + data.signature());
        System.out.println("Body keys: " + data.body().keySet());
        System.out.println("Identifier: "+ identifier.get());
        System.out.println("Artifact: "+artifact.get());
        
        // This is not how it will work, there will be a database
        // to store information to get things moving
        ConfigManager.setWalletConfigured(true);
        
        if (host != null) {
            host.swapBody("HomeView.fxml");
        }
    }
    
    public static HttpResponse<String> sendRequest(String url, String method,
            Map<String, String> headers, Object body) throws IOException, InterruptedException {
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

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
    
}
