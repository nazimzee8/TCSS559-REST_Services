package com.tcss559;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class uploadClient {
	public static String serviceURL = "http://localhost:8080/Quiz2"; 
	public static StringBuilder SERVICE_URL = new StringBuilder(128); 
	public static void main(String[] args) {
		try { 
        
			// construct query string and set the character set to UTF-8 
			String charset = "UTF-8"; 
			String fileName = "data.json";
			double options = 0;
			String queryString = String.format("file=%s&options=%s", URLEncoder.encode(fileName, charset),                  
                                             URLEncoder.encode(String.valueOf(options), charset)); 
          
         // construct the service URL and the query string 
         URL serviceEndpoint = new URL(serviceURL + "?" + queryString); 
         // initialize the HTTP Request with the service endpoint URL 
         HttpURLConnection httpRequestCon = (HttpURLConnection) serviceEndpoint.openConnection(); 
         httpRequestCon.setRequestProperty("Accept-Charset", charset); 
         // specifies the request method 
         httpRequestCon.setRequestMethod("GET"); 
         // indicates the content that can be accepted or expected in the response message 
         httpRequestCon.setRequestProperty("Accept", "application/html"); 
         // check to see whether we get HTTP OK (status code 200) or not 
        
         if (httpRequestCon.getResponseCode() != 200) 
            throw new RuntimeException("HTTP Error code is: " + httpRequestCon.getResponseCode()); 
       
         // read the stream response and store into a buffered reader 
         BufferedReader httpResponse = new BufferedReader(new InputStreamReader((httpRequestCon.getInputStream()))); 
         System.out.println(("Status Codes: " + httpRequestCon.getResponseCode())); 
         System.out.println("App Version: " + httpRequestCon.getHeaderField("restApp-version")); 
         // retrieve output from the response object (HTTP Response) 
         String responseOutput; 
         StringBuffer responseMessage = new StringBuffer(); 
         while ((responseOutput = httpResponse.readLine()) != null) { 
        	 responseMessage.append(responseOutput); 
         } 
         System.out.println(responseMessage); 
         httpRequestCon.disconnect();              
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		} 
	}
}
