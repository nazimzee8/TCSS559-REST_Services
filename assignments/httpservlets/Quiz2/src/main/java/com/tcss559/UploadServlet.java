package com.tcss559;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;

import controllers.Topsis;
import controllers.WSM;

/**
 * Servlet implementation class restAppClient
 */
@WebServlet("/")
@MultipartConfig(fileSizeThreshold=1024*1024*10, 	//  10 MB 
maxFileSize=1024*1024*50,      						//  50 MB
maxRequestSize=1024*1024*100)   					// 100 MB
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIR = "uploads";
	public static final StringBuilder FILE_UPLOAD = new StringBuilder(128);
	public static StringBuilder uploadFilePath = new StringBuilder(128);
	public static String OPTIONS = "both";
	Gson gson = new Gson();
	
	 public UploadServlet() {
	        super();
	    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");           // set content-type to HTML 
        response.setHeader("restApp-version", "1.0");
        if (request.getAttribute("options") != null) OPTIONS = request.getParameter("options");
        PrintWriter outputHTML = response.getWriter();
        Topsis topsis = new Topsis();
    	WSM wsm = new WSM();
    	
    	topsis.doGet(request, response);
    	wsm.doGet(request, response);
        String output_topsis = topsis.retrieveOutput();
        String output_wsm = wsm.retrieveOutput();
        List<Double> perfScores = topsis.getPerfScores();
        List<Double> weightedSums = wsm.getWeightedSums();
        String output_upload = this.getJSONOutput(output_topsis, output_wsm, perfScores, weightedSums);
        if (!request.getRequestURL().toString().contains("Topsis") && !request.getRequestURL().toString().contains("Topsis") && !request.getRequestURL().toString().contains("WSM")) {
	        outputHTML.print("<h2> " + output_upload + " </h2>");
	        outputHTML.print("<h2> " + output_topsis + " </h2>");
	        outputHTML.print("<h2> " + output_wsm + " </h2>");
        }
	}
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        uploadFilePath.append(applicationPath + File.separator + UPLOAD_DIR);
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(uploadFilePath.toString());
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());
        
        String fileName = null;
        //Get all the parts from request and write it to the file on server
        for (Part part : request.getParts()) {
            fileName = getFileName(part);
            part.write(uploadFilePath.toString() + File.separator + fileName);
        }
        FILE_UPLOAD.append(uploadFilePath.toString() + File.separator + fileName);
        PrintWriter outputHTML = response.getWriter();
    	Topsis topsis = new Topsis();
    	WSM wsm = new WSM();
        topsis.doPost(request, response);
        outputHTML.print("\n");
        wsm.doPost(request, response);
        
    }
 
    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
    
    public static String getFilePath() {
    	return uploadFilePath.toString();
    }
    
    public static String getFileUpload() {
    	return FILE_UPLOAD.toString();
    }
    
    public double getCorrelationCoefficient(List<Double> perfScores, List<Double> weightedSums) {
    	double perf_Sums = 0;
    	double weighted_Sums = 0;
    	double product_Sums = 0;
    	double perfSq_Sums = 0;
    	double weightedSq_Sums = 0;
    	for (int i = 0; i < perfScores.size(); i++) {
    		perf_Sums += perfScores.get(i);
    		weighted_Sums += weightedSums.get(i);
    		product_Sums += perfScores.get(i) * weightedSums.get(i);
    		perfSq_Sums += Math.pow(perfScores.get(i), 2);
    		weightedSq_Sums += Math.pow(weightedSums.get(i), 2);
    	}
    	double numerator = (perfScores.size() * product_Sums) - (perf_Sums * weighted_Sums);
    	double factor1 = (perfScores.size() * perfSq_Sums) - (Math.pow(perf_Sums, 2));
    	double factor2 = (perfScores.size() * weightedSq_Sums) - (Math.pow(weighted_Sums, 2));
    	double denominator = Math.sqrt(factor1 * factor2);
    	return numerator/denominator;
    }
    
    public String getJSONOutput(String output_topsis, String output_wsm, List<Double> perfScores, List<Double> weightedSums) throws JsonMappingException, JsonProcessingException {
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	StringBuilder correlation = new StringBuilder(128);
    	double r = this.getCorrelationCoefficient(perfScores, weightedSums);
    	if (r < Math.abs(0.5)) correlation.append("The outputs from Topsis and WSM have a weak linear relationship.");
    	else correlation.append("The outputs from Topsis and WSM have a strong linear relationship.");
    	builder.add("Correlation Coefficient", r);
    	builder.add("Strength of Correlation", correlation.toString());
		javax.json.JsonObject json = builder.build();
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		Object jsonObject = mapper.readValue(json.toString(), Object.class);
		String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		return result;
    }

}
