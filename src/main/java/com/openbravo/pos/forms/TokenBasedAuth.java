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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openbravo.basic.BasicException;
import java.io.File;
import java.util.Base64;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.RandomStringUtils;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.inventory.ProductsEditor;

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
    
    private static final String TOKEN_ID = "ad5a7b874b3b3f83613152e0ff1235a29500cd5a46d9e8244df050d38a50cf7a";
    private static final String TOKEN_SECRET = "4adc68eec587aaa3b73b5ff690e24cea81827a964dde5eae6b8f7a19914e20e2";
    private static final String REST_URL = "https://7131410.restlets.api.netsuite.com/app/site/hosting/restlet.nl";
    private static final String CONSUMER_KEY = "8ce6f98fbb115b67ae34134671bca711eed87b39128f893c507f89fb97d0ad21";
    private static final String CONSUMER_SECRET = "069fd315a5ac34b90b1df3db58bd29828cbff37f3769f2ec84a76bfb854e59c1";
    private static final String REALM = "7131410";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APP_JSON = "application/json";
    private static final String EMPTY_JSON_PAYLOAD = "{}";
    private static final String SCRIPT_ID = "11";
    private static final String DEPLOYMENT_ID = "2";

    
    private final DataLogicSales dlSales;
    
    public TokenBasedAuth(AppView app){
        dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
    }
    
    
    public static void main(String[] args) {

        
    }

    TokenBasedAuth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getHost() {
      AppConfig m_config_host =  new AppConfig(new File((System.getProperty("user.home")),
              AppLocal.APP_ID + ".properties"));        
      m_config_host.load();
      String machineHostname =(m_config_host.getProperty("machine.hostname"));
      m_config_host = null;
      return machineHostname;
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
        String HostName=getHost();
        System.out.println("HostName ==>"+HostName);
        String url=REST_URL+"?script=11&deploy=2&record_type=get_categories&hostname="+HostName;
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
                + "&hostname="+HostName
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
    
    public String getPosConfiguration() throws IOException{
        String HostName=getHost();
        System.out.println("HostName ==>"+HostName);
        String url=REST_URL+"?script=11&deploy=2&record_type=get_pos_config&hostname="+HostName;
        System.out.println("URL :"+url);
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
                + "&hostname="+HostName
                + "&oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + oauth_nonce
                + "&oauth_signature_method=" +oauth_signature_method
                + "&oauth_timestamp=" + oauth_timestamp
                + "&oauth_token=" + TOKEN_ID
                + "&oauth_version=" + oauth_version
                + "&record_type=get_pos_config"
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
        System.out.println("Response Msg :"+responseMsg);
        
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
    
    public String getDikerjakanOleh() throws IOException{
        String url=REST_URL+"?script=11&deploy=2&record_type=get_employee&vendorClean=true";
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
                + "&record_type=get_employee"
                + "&script=" + SCRIPT_ID
                + "&vendorClean=true"
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
    
    public String getListVoucher(String productId) throws IOException{
        String url=REST_URL+"?script=11&deploy=2&record_type=get_voucher_list&id_voc="+productId;
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
                + "&id_voc="+productId
                + "&oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + oauth_nonce
                + "&oauth_signature_method=" +oauth_signature_method
                + "&oauth_timestamp=" + oauth_timestamp
                + "&oauth_token=" + TOKEN_ID
                + "&oauth_version=" + oauth_version
                + "&record_type=get_voucher_list"
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
    
    public String postToNetsuite(JsonObject data) throws IOException{
        
        
        String url=REST_URL+"?script=11&deploy=2&realm="+REALM;
        URL myURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();

        String oauth_nonce = RandomStringUtils.randomAlphanumeric(20);
        Long timestamp = System.currentTimeMillis() / 1000L;
        String oauth_timestamp=timestamp.toString();
        String oauth_signature_method="HMAC-SHA256";
        String oauth_version="1.0";
        
        String base_string =
        "POST&" + URLEncoder.encode(REST_URL) +"&"+
        URLEncoder.encode(
            "deploy=" + DEPLOYMENT_ID
                + "&oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + oauth_nonce
                + "&oauth_signature_method=" +oauth_signature_method
                + "&oauth_timestamp=" + oauth_timestamp
                + "&oauth_token=" + TOKEN_ID
                + "&oauth_version=" + oauth_version
                + "&realm=" + REALM
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
        
        String jsonInputString=data.toString();
        System.out.println(jsonInputString);
        conn.setReadTimeout(60 * 1000);
        conn.setConnectTimeout(60 * 1000);
        conn.setRequestProperty("Authorization", header);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", APP_JSON);
        conn.setDoOutput(true);
        try(OutputStream os=conn.getOutputStream()){
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);   
        }
        
        int responseCode=conn.getResponseCode();
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
    
    
    public void categoryInit() throws IOException{
      try {
//          TokenBasedAuth tokenBasedAuth=new com.openbravo.pos.forms.TokenBasedAuth();
          String getHost = getHost();
          System.out.println("GET HOST: "+ getHost);

          String responseString=getMasterCategory();
          System.out.println("Response Cat Token Base: "+ responseString);
          JsonElement je = new JsonParser().parse(responseString);
          int jsonLength=je.getAsJsonArray().size();
          
          //delete All Categories
          dlSales.updateAllCategory();
          //insert data from netsuite to databse unicenta
          for(int i=0;i<jsonLength;i++){
              try {
                  System.out.println("Masuk loop");

                  Object[] newcat = new Object[6];
                  newcat[0] = je.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString();
                  newcat[1] = je.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                  newcat[2] = true;
                  newcat[3] = je.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString();
                  newcat[4] = je.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                  newcat[5] = true;
                  System.out.println(je.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString());
                  dlSales.createCategory(newcat);
              } catch (BasicException ex) {
                  Logger.getLogger(TokenBasedAuth.class.getName()).log(Level.SEVERE, null, ex);
              }
          }
      } catch (BasicException ex) {
          Logger.getLogger(TokenBasedAuth.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
    
     public void productsInit() throws IOException{
//      TokenBasedAuth tokenBasedAuth=new com.openbravo.pos.forms.TokenBasedAuth(app);
      String responseString=getMasterProducts();
      System.out.println("ressponse Product"+responseString);
      JsonElement je = new JsonParser().parse(responseString);
      int jsonLength=je.getAsJsonArray().size();
    
        for(int i=0;i<jsonLength;i++){
                String id=je.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString();
                String category=je.getAsJsonArray().get(i).getAsJsonObject().get("category").getAsString();
                String priceIncludedTax=je.getAsJsonArray().get(i).getAsJsonObject().get("pricesincludetax").getAsString();
                Object[] newcat = new Object[27];
                newcat[0] = id;
                newcat[1] = !"".equals(je.getAsJsonArray().get(i).getAsJsonObject().get("reference").getAsString()) ? je.getAsJsonArray().get(i).getAsJsonObject().get("reference").getAsString() :id;
                newcat[2] = !"".equals(je.getAsJsonArray().get(i).getAsJsonObject().get("barcode").getAsString()) ? je.getAsJsonArray().get(i).getAsJsonObject().get("barcode").getAsString() : id;
                newcat[3] = category;
                newcat[4] = je.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                newcat[5] = "T".equals(priceIncludedTax) ? "001" : "000";
                newcat[6] = je.getAsJsonArray().get(i).getAsJsonObject().get("buyprice").getAsDouble();
                newcat[7] = je.getAsJsonArray().get(i).getAsJsonObject().get("sellprice").getAsDouble();
                newcat[8]="0";
                newcat[9]=je.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                newcat[10]=je.getAsJsonArray().get(i).getAsJsonObject().get("satuan").getAsString() != null ? je.getAsJsonArray().get(i).getAsJsonObject().get("satuan").getAsString():"PCS";
                newcat[11] = je.getAsJsonArray().get(i).getAsJsonObject().get("expressPrice").getAsDouble();
                newcat[12] = je.getAsJsonArray().get(i).getAsJsonObject().get("regulerPrice").getAsDouble();
                newcat[13] = je.getAsJsonArray().get(i).getAsJsonObject().get("isvoucher").getAsBoolean();
                //newcat[8] = je.getAsJsonArray().get(i).getAsJsonObject().get("uom").getAsString();
                newcat[14] = !"".equals(je.getAsJsonArray().get(i).getAsJsonObject().get("reference").getAsString()) ? je.getAsJsonArray().get(i).getAsJsonObject().get("reference").getAsString() :id;
                newcat[15] = !"".equals(je.getAsJsonArray().get(i).getAsJsonObject().get("barcode").getAsString()) ? je.getAsJsonArray().get(i).getAsJsonObject().get("barcode").getAsString() : id;
                newcat[16] = category;
                newcat[17] = je.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                newcat[18] = "T".equals(priceIncludedTax) ? "001" : "000";
                newcat[19] = je.getAsJsonArray().get(i).getAsJsonObject().get("buyprice").getAsDouble();
                newcat[20] = je.getAsJsonArray().get(i).getAsJsonObject().get("sellprice").getAsDouble();
                newcat[21]="0";
                newcat[22]=je.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                newcat[23]=je.getAsJsonArray().get(i).getAsJsonObject().get("satuan").getAsString();
                newcat[24] = je.getAsJsonArray().get(i).getAsJsonObject().get("expressPrice").getAsDouble();
                newcat[25] = je.getAsJsonArray().get(i).getAsJsonObject().get("regulerPrice").getAsDouble();
                newcat[26] = je.getAsJsonArray().get(i).getAsJsonObject().get("isvoucher").getAsBoolean();
                if(!"".equals(category)){
                    try {
                        JsonElement lokasi=je.getAsJsonArray().get(i).getAsJsonObject().get("locations").getAsJsonArray();
                        Double qtyOnHand;
                        if(lokasi.getAsJsonArray().size()>0){
                            qtyOnHand= je.getAsJsonArray().get(i).getAsJsonObject().get("locations").getAsJsonArray().get(0).getAsJsonObject().get("qtyOnHand").getAsDouble();
                        }else{
                            qtyOnHand=0.0;
                        }
                        
                        System.out.println(qtyOnHand);
                        dlSales.createProducts(newcat);
                        Object[] newProductsCat = new Object[2];
                        newProductsCat[0] = id;
                        newProductsCat[1] = id;
                        dlSales.createProductsCat(newProductsCat);
                        Object[] newStockCurrent=new Object[4];
                        newStockCurrent[0]=id;
                        newStockCurrent[1]=qtyOnHand;
                        newStockCurrent[2]=id;
                        newStockCurrent[3]=qtyOnHand;
                        dlSales.createStockCurrent(newStockCurrent);
                    } catch (BasicException ex) {
                        Logger.getLogger(TokenBasedAuth.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }
  }
    
    
}
                    