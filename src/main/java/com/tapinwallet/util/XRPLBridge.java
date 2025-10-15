package com.tapinwallet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedInteger;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import netscape.javascript.JSObject;
import org.xrpl.xrpl4j.client.JsonRpcClient;
import org.xrpl.xrpl4j.model.jackson.modules.Xrpl4jModule;

public class XRPLBridge {

    private XrplClient client;
    private KeyPair keyPair;
    private Address address;

    private SignatureService signatureService;

    private SignatureService signatureService() {
        if (signatureService == null) {
            signatureService = new BcSignatureService();
        }
        return signatureService;
    }

    public void log(String data) {
        System.out.println(data);
    }

    /**
     * Connect to network
     */
    public void connect(String url, JSObject cb) {

        String result;

        try {
            client = new XrplClient(HttpUrl.get(url));
//            currentNetwork = url;
            result = "Connected to " + url;

        } catch (Exception e) {
            result = "Error connecting: " + e.getMessage();
        }

        if (cb != null) {
            cb.call("call", null, new Object[]{result});
        }

    }

    /**
     * Create new wallet (keypair + address)
     */
    public void createWallet(JSObject cb) {

        Seed seed = Seed.ed25519Seed();            // random seed
        keyPair = seed.deriveKeyPair();            // derive keypair
        address = keyPair.publicKey().deriveAddress(); // xrpl4j 5.0.0 specific

        if (cb != null) {
            cb.call("call", null, new Object[]{address.value()});
        }
    }

    /**
     * Get wallet address
     */
    public String getAddress() {
        return address != null ? address.value() : "No wallet created";
    }

    /**
     * Get balance
     */
    public void getBalance(JSObject cb) {

        String result = "No Wallet";

        try {
            System.out.println("BEFORE ####################### : " + address);

            AccountInfoRequestParams params = AccountInfoRequestParams.of(address);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Xrpl4jModule());
            mapper.getSerializerProviderInstance().findValueSerializer(org.xrpl.xrpl4j.model.client.common.LedgerSpecifier.class);
            
            System.out.println("DEBUG JSON = " + mapper.writeValueAsString(params));

            System.out.println("AFTER ####################### : " + params.account());

            AccountInfoResult info = client.accountInfo(params); //BREAKING HERE
            System.out.println("######################## : " + info.toString());
            result = info.accountData().balance().toXrp() + " XRP";

        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        }

        if (cb != null) {
            cb.call("call", new Object[]{null, result});
        }
    }

    /**
     * Send XRP
     */
    public String send(String toAddress, String amountXrp) {
        try {
            if (address == null || keyPair == null) {
                return "No wallet";
            }

            Address destination = Address.of(toAddress);
            XrpCurrencyAmount amount = XrpCurrencyAmount.ofXrp(new BigDecimal(amountXrp));

            FeeResult feeResult = client.fee();
            UnsignedInteger seq = client.accountInfo(AccountInfoRequestParams.of(address))
                    .accountData()
                    .sequence();

            Payment payment = Payment.builder()
                    .account(address)
                    .amount(amount)
                    .destination(destination)
                    .fee(feeResult.drops().openLedgerFee())
                    .sequence(seq)
                    .signingPublicKey(keyPair.publicKey())
                    .build();

            SingleSignedTransaction<Payment> signed
                    = signatureService().sign(keyPair.privateKey(), payment);

            SubmitResult<?> result = client.submit(signed);
            return result.engineResult();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

//    public String fundTestnet() {
//        if (address == null) {
//            return "No wallet created.";
//        }
//        try {
//            HttpClient http = HttpClient.newHttpClient();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://faucet.altnet.rippletest.net/accounts"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(
//                            "{ \"destination\": \"" + address.value() + "\" }"
//                    ))
//                    .build();
//
//            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
//            return response.body();
//        } catch (Exception e) {
//            return "Error funding wallet: " + e.getMessage();
//        }
//    }
    public void fundTestnet(JSObject cb) {

        String result = "No wallet created.";

        try {
            HttpClient http = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://faucet.altnet.rippletest.net/accounts"))
                    //                    .uri(URI.create(currentNetwork).create("https://faucet.altnet.rippletest.net/accounts"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{ \"destination\": \"" + address.value() + "\" }"
                    ))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();
        } catch (Exception e) {
            e.printStackTrace();
            result = "Error funding wallet: " + e.getMessage();
        }

        if (cb != null) {
            cb.call("call", null, new Object[]{result});
        }
    }
}
