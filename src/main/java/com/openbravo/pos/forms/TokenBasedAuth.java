/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.forms;

/**
 *
 * @author LENOVO
 */
import java.util.Base64;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.RandomStringUtils;

//import org.json.JSONObject;

public class TokenBasedAuth {   
    
    
//define("NETSUITE_URL", 'https://7131410.restlets.api.netsuite.com/app/site/hosting/restlet.nl');
//define("NETSUITE_SCRIPT_ID", '11');
//define("NETSUITE_DEPLOY_ID", '2');
//define("NETSUITE_ACCOUNT", '7131410');
//define("NETSUITE_CONSUMER_KEY", 'f173bb9a174fe9d259d7c1dcd260456cedb192a7d819ac17c5d9055dbaf2dd78');
//define("NETSUITE_CONSUMER_SECRET", '04d673de32bca8bca7e004dba0326ed03c2d4b4379c230ab637065870b9abc54');
//define("NETSUITE_TOKEN_ID", '61bfde99e22637154962606734227c1cafeb76508d9cfda255a63315dd5801f8');
//define("NETSUITE_TOKEN_SECRET", '86e79cb0ad1543cc7825aebefb2c1ca39c971b7e827c5e2022622bf5de07029c');
    
    private static final String TOKEN_ID = "61bfde99e22637154962606734227c1cafeb76508d9cfda255a63315dd5801f8";
    private static final String TOKEN_SECRET = "86e79cb0ad1543cc7825aebefb2c1ca39c971b7e827c5e2022622bf5de07029c";
    private static final String REST_URL = "https://7131410.restlets.api.netsuite.com/app/site/hosting/restlet.nl";
    private static final String CONSUMER_KEY = "f173bb9a174fe9d259d7c1dcd260456cedb192a7d819ac17c5d9055dbaf2dd78";
    private static final String CONSUMER_SECRET = "04d673de32bca8bca7e004dba0326ed03c2d4b4379c230ab637065870b9abc54";
    private static final String REALM = "7131410";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APP_JSON = "application/json";
    private static final String EMPTY_JSON_PAYLOAD = "{}";
    private static final String SCRIPT_ID = "11";
    private static final String DEPLOYMENT_ID = "2";

    

    public static void main(String[] args) {

            
    }
    
    protected String generateSignature(String baseString, String keyString, String algorithm) throws Exception
    {  

        if (!algorithm.equals("HmacSHA256") && !algorithm.equals("HmacSHA1")) algorithm = "HmacSHA1";

        {
           byte[] bytes = keyString.getBytes();
           SecretKeySpec mySigningKey = new SecretKeySpec(bytes, algorithm);

           Mac messageAuthenticationCode = Mac.getInstance(algorithm);

           messageAuthenticationCode.init(mySigningKey);

           byte[] hash = messageAuthenticationCode.doFinal(baseString.getBytes());

           String result = Base64.getEncoder().encodeToString(hash);

           return result;
        }
    
    }

    
    public String getMasterCategory() throws IOException{
        String url=REST_URL+"?script=11&deploy=2&record_type=get_categories";
        URL myURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();

        String oauth_nonce = RandomStringUtils.randomAlphanumeric(20);
        Long timestamp = System.currentTimeMillis() / 1000L;

        String oauth_timestamp=timestamp.toString();
        String oauth_signature_method="HMAC-SHA256";
        String oauth_version="1.0";
        
        String base_string =
        "GET&" + URLEncoder.encode(REST_URL) +"&"+
        URLEncoder.encode(
            "deploy=" + DEPLOYMENT_ID
                + "&oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + oauth_nonce
                + "&oauth_signature_method=" +oauth_signature_method
                + "&oauth_timestamp=" + oauth_timestamp
                + "&oauth_token=" + TOKEN_ID
                + "&oauth_version=" + oauth_version
                + "&record_type=get_categories"
                + "&script=" + SCRIPT_ID
        );
        String sig_string=URLEncoder.encode(CONSUMER_SECRET)+"&"+URLEncoder.encode(TOKEN_SECRET);
   
        String signature="";
        try {
            signature=generateSignature(base_string,sig_string,"HmacSHA256");
        } catch (Exception ex) {
            Logger.getLogger(TokenBasedAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String header="OAuth realm=\""+ REALM +"\","
            +"oauth_consumer_key=\""+CONSUMER_KEY +"\","
            +"oauth_token=\""+TOKEN_ID+"\","
            +"oauth_signature_method=\""+oauth_signature_method+"\","
            +"oauth_timestamp=\""+oauth_timestamp+"\","
            +"oauth_nonce=\""+oauth_nonce+"\","
            +"oauth_version=\""+oauth_version+"\","
            +"oauth_signature=\""+URLEncoder.encode(signature)+"\"";
        
        conn.setReadTimeout(60 * 1000);
        conn.setConnectTimeout(60 * 1000);
        conn.setRequestProperty("Authorization", header);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", APP_JSON);
        
//        int responseCode=conn.getResponseCode();
        String responseMsg=conn.getResponseMessage();
        
        
        if("OK".equals(responseMsg)){
            String response = "";
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while(scanner.hasNextLine()){
                    response += scanner.nextLine();
                    response += "\n";
                }
                
            }
            
            return response;
        }
        
        return null;  
    }
    
    public String getMasterProducts() throws IOException{
        String url=REST_URL+"?script=11&deploy=2&record_type=get_products";
        URL myURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();

        String oauth_nonce = RandomStringUtils.randomAlphanumeric(20);
        Long timestamp = System.currentTimeMillis() / 1000L;

        String oauth_timestamp=timestamp.toString();
        String oauth_signature_method="HMAC-SHA256";
        String oauth_version="1.0";
        
        String base_string =
        "GET&" + URLEncoder.encode(REST_URL) +"&"+
        URLEncoder.encode(
            "deploy=" + DEPLOYMENT_ID
                + "&oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + oauth_nonce
                + "&oauth_signature_method=" +oauth_signature_method
                + "&oauth_timestamp=" + oauth_timestamp
                + "&oauth_token=" + TOKEN_ID
                + "&oauth_version=" + oauth_version
                + "&record_type=get_products"
                + "&script=" + SCRIPT_ID
        );
        String sig_string=URLEncoder.encode(CONSUMER_SECRET)+"&"+URLEncoder.encode(TOKEN_SECRET);
   
        String signature="";
        try {
            signature=generateSignature(base_string,sig_string,"HmacSHA256");
        } catch (Exception ex) {
            Logger.getLogger(TokenBasedAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String header="OAuth realm=\""+ REALM +"\","
            +"oauth_consumer_key=\""+CONSUMER_KEY +"\","
            +"oauth_token=\""+TOKEN_ID+"\","
            +"oauth_signature_method=\""+oauth_signature_method+"\","
            +"oauth_timestamp=\""+oauth_timestamp+"\","
            +"oauth_nonce=\""+oauth_nonce+"\","
            +"oauth_version=\""+oauth_version+"\","
            +"oauth_signature=\""+URLEncoder.encode(signature)+"\"";
        
        conn.setReadTimeout(60 * 1000);
        conn.setConnectTimeout(60 * 1000);
        conn.setRequestProperty("Authorization", header);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", APP_JSON);
        
//        int responseCode=conn.getResponseCode();
        String responseMsg=conn.getResponseMessage();
        
        
        if("OK".equals(responseMsg)){
            String response = "";
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while(scanner.hasNextLine()){
                    response += scanner.nextLine();
                    response += "\n";
                }
                
            }
            
            return response;
        }
        
        return null;  
    }
    
    public String getMasterCustomers() throws IOException{
        String url=REST_URL+"?script=11&deploy=2&record_type=get_all_customer";
        URL myURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();

        String oauth_nonce = RandomStringUtils.randomAlphanumeric(20);
        Long timestamp = System.currentTimeMillis() / 1000L;

        String oauth_timestamp=timestamp.toString();
        String oauth_signature_method="HMAC-SHA256";
        String oauth_version="1.0";
        
        String base_string =
        "GET&" + URLEncoder.encode(REST_URL) +"&"+
        URLEncoder.encode(
            "deploy=" + DEPLOYMENT_ID
                + "&oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + oauth_nonce
                + "&oauth_signature_method=" +oauth_signature_method
                + "&oauth_timestamp=" + oauth_timestamp
                + "&oauth_token=" + TOKEN_ID
                + "&oauth_version=" + oauth_version
                + "&record_type=get_all_customer"
                + "&script=" + SCRIPT_ID
        );
        String sig_string=URLEncoder.encode(CONSUMER_SECRET)+"&"+URLEncoder.encode(TOKEN_SECRET);
   
        String signature="";
        try {
            signature=generateSignature(base_string,sig_string,"HmacSHA256");
        } catch (Exception ex) {
            Logger.getLogger(TokenBasedAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String header="OAuth realm=\""+ REALM +"\","
            +"oauth_consumer_key=\""+CONSUMER_KEY +"\","
            +"oauth_token=\""+TOKEN_ID+"\","
            +"oauth_signature_method=\""+oauth_signature_method+"\","
            +"oauth_timestamp=\""+oauth_timestamp+"\","
            +"oauth_nonce=\""+oauth_nonce+"\","
            +"oauth_version=\""+oauth_version+"\","
            +"oauth_signature=\""+URLEncoder.encode(signature)+"\"";
        
        conn.setReadTimeout(60 * 1000);
        conn.setConnectTimeout(60 * 1000);
        conn.setRequestProperty("Authorization", header);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", APP_JSON);
        
//        int responseCode=conn.getResponseCode();
        String responseMsg=conn.getResponseMessage();
        
        
        if("OK".equals(responseMsg)){
            String response = "";
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while(scanner.hasNextLine()){
                    response += scanner.nextLine();
                    response += "\n";
                }
                
            }
            
            return response;
        }
        
        return null;  
    }
    
    
}
                    