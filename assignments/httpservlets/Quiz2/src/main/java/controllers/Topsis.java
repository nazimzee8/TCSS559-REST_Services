package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.tcss559.UploadServlet;


/**
 * Servlet implementation class Topsis
 */
@WebServlet("/Topsis")
public class Topsis extends HttpServlet implements MCDA {
	private static final long serialVersionUID = 1L;
	List<Double> weights;
	List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	List<Double> perfScores;
	List<Double> bestEuclidVals = new ArrayList<Double>();
	List<Double> worstEuclidVals = new ArrayList<Double>();
	List<Double> idealBest = new ArrayList<Double>();
	List<Double> idealWorst = new ArrayList<Double>();
	Gson gson = new Gson();
	File file;
	StringBuilder output = new StringBuilder(128);

    /**
     * Default constructor. 
     */
    public Topsis() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws NullPointerException, ServletException, IOException {
		response.setContentType("text/html");           // set content-type to HTML 
        response.setHeader("restApp-version", "1.0");   // create custom header attribute for version 1.0 
	    output.append(this.doScoring(request, response));
	    PrintWriter outputHTML = response.getWriter();
        
        /** Preliminary Testing 
        outputHTML.print("<h1> Testing Topsis.java </h1>");
        outputHTML.print("<h2> Weights: " + this.weights + "</h2>");
        outputHTML.print("<h2> Matrix: " + this.matrix + "</h2>");
        outputHTML.print("<h2> Values: " + this.retrieveTypes(data) + "</h2>");
        //outputHTML.print("<h2> Classifiers: " + this.getClassification(data).length + "</h2>");
        outputHTML.print("<h2> Worst Values: " + this.idealWorst + "</h2>");
        outputHTML.print("<h2> Best Values: " + this.idealBest + "</h2>"); **/
	    if (request.getRequestURL().toString().contains("Topsis")) outputHTML.print("<h2> " + output.toString() + " </h2>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");           // set content-type to HTML 
        response.setHeader("restApp-version", "1.0");   // create custom header attribute for version 1.0  
        // Data Computation
        String output = this.doScoring(request, response);
        PrintWriter outputHTML = response.getWriter();
        outputHTML.print("<h2> " + output + " </h2>");
	}
	
	public String doScoring(HttpServletRequest request, HttpServletResponse response) throws JsonIOException, JsonSyntaxException, FileNotFoundException, JsonMappingException, JsonProcessingException {
        if (!UploadServlet.getFileUpload().equals(null)) setFile(new File(UploadServlet.getFileUpload()));
        else if (request.getParameter("file") != null) setFile(new File("C:/Users/nazimz/Documents/TCSS559/apache-tomcat-9.0.62/wtpwebapps/Quiz2/uploads/" + request.getParameter("file")));
        else setFile(new File("C:/Users/nazimz/Documents/TCSS559/apache-tomcat-9.0.62/wtpwebapps/Quiz2/uploads/" + "data.json"));
		JsonObject data = this.parseJSON(file);
        this.getWeights(data);
        this.retrieveMatrix(data);
        this.normalizeMatrix();
        this.getIdealValues(data);
        this.WeightedValues(data);
        
        // Retrieve Performance Scores for Ranking
        perfScores = this.getPerformance(bestEuclidVals, worstEuclidVals);
        String[] alternatives = this.retrieveAlternatives(data);
        this.BubbleSort(perfScores, alternatives);
        
        StringBuilder output = new StringBuilder(128);
        if (request.getParameter("options") != null) {
			output.append(this.getJSONOutput(alternatives, perfScores, request.getParameter("options")));
		}
		else {
			output.append(this.getJSONOutput(alternatives, perfScores));
		}
        return output.toString();
        
	}
	
	@Override
	public String retrieveOutput() {
		return this.output.toString();
	}
	
    public void getWeights(JsonObject data) {
    	double[] array = gson.fromJson(data.get("Weights"), double[].class);
    	weights = new ArrayList<Double>();
    	double sum = 0;
    	for (double w : array) { 
    		if (w < 0) throw new RuntimeException("Please enter a positie value for weight.");
    		weights.add(w);
    		sum += w;
    	}
    	if (sum > Math.abs(1)) {
    		throw new RuntimeException("Sum of weights must be between 0 and 1!");
    	}
    }
    
    public void normalizeMatrix() {
    	for (int i = 0; i < weights.size(); i++) {
			double sum = 0;
			for (int j = 0; j < matrix.size(); j++) {
				sum += Math.pow(matrix.get(j).get(i), 2);
			}
			for (int j = 0; j < matrix.size(); j++) {
				double value = matrix.get(j).get(i);
				matrix.get(j).set(i, weights.get(i) * (value / Math.sqrt(sum)));
			}
		}
    }

	@Override
	public void retrieveMatrix(JsonObject data) {
		double[][] entries = gson.fromJson(data.get("Entries"), double[][].class);
		for (int i = 0 ; i < entries.length; i++) {
			matrix.add(new ArrayList<Double>());
			for (int j = 0; j < entries[i].length; j++) {
				matrix.get(i).add(entries[i][j]);
			}
		}
	}
	
	public void getIdealValues(JsonObject data) {
		String[] classifiers = this.getClassification(data);
		for (int i = 0; i < weights.size(); i++) {
			double max = 0;
			double min = 1.01;
			for (int j = 0; j < matrix.size(); j++) {
				max = Math.max(max, matrix.get(j).get(i));
				min = Math.min(min, matrix.get(j).get(i));
			}
			if (classifiers[i].equals("Maximum")) {
				idealBest.add(max);
				idealWorst.add(min);
			}
			else {
				idealWorst.add(max);
				idealBest.add(min);
			}
		}
	}

	@Override
	public void WeightedValues(JsonObject data) {
		double[] values = this.retrieveTypes(data);
		for (int i = 0; i < matrix.size(); i++) {
			double bestSum = 0;
			double worstSum = 0;
			for (int j = 0 ; j < weights.size(); j++) {
				bestSum += values[j] * (Math.pow(matrix.get(i).get(j)-idealBest.get(j), 2));
				worstSum += values[j] * (Math.pow(matrix.get(i).get(j)-idealWorst.get(j), 2));
			}
			bestEuclidVals.add(Math.sqrt(bestSum));
			worstEuclidVals.add(Math.sqrt(worstSum));
		}	
	}
	
	public List<Double> getPerformance(List<Double> bestEuclidVals, List<Double> worstEuclidVals) {
		List<Double> perfScores = new ArrayList<Double>();
		for (int i = 0; i < bestEuclidVals.size(); i++) {
			if (bestEuclidVals.get(i) != 0 || worstEuclidVals.get(i) != 0) perfScores.add(worstEuclidVals.get(i) / (bestEuclidVals.get(i) + worstEuclidVals.get(i)));
			else perfScores.add(0.0);
		}
		return perfScores;
	}
	
	public List<Double> getPerfScores() {
		return perfScores;
	}

	@Override
	public void BubbleSort(List<Double> perfScores, String[] alternatives) {
		for (int i = 0; i < perfScores.size(); i++) {
            for (int j = 0; j < perfScores.size(); j++) {
                Double temp = perfScores.get(j);
                String tempAlt = alternatives[j];
                if (j > 0 && temp > perfScores.get(j - 1)) {
                    perfScores.set(j, perfScores.get(j-1));
                    alternatives[j] = alternatives[j-1];
                    perfScores.set(j-1, temp);
                    alternatives[j-1] = tempAlt;
                }
            }
        }
	}
	
	@Override
	public JsonObject parseJSON(File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		JsonObject jsonObject = new JsonObject();
		JsonElement element = JsonParser.parseReader(new FileReader(file));
		jsonObject = element.getAsJsonObject();
		return jsonObject;
	}
	
	public String getJSONOutput(String[] alternatives, List<Double> perfScores) throws JsonMappingException, JsonProcessingException {
		return getJSONOutput(alternatives, perfScores, "topsis");
	}

	@Override
	public String getJSONOutput(String[] alternatives, List<Double> perfScores, String options) throws JsonMappingException, JsonProcessingException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (options.equals("basic")) {
			JsonObjectBuilder temp = Json.createObjectBuilder();
			for (int i = 0; i < perfScores.size(); i++) {
				temp.add(String.valueOf(i+1), perfScores.get(i));
			}
			builder.add("Topsis Performance Scores", temp);
		}
		else if (options.equals("topsis") || options.equals("both")){
			int i = 0;
			double avg = 0;
			for (double score : perfScores) avg += score;
			avg = avg / perfScores.size();
			JsonObjectBuilder temp = Json.createObjectBuilder();
			for (String alternative : alternatives) {
				String str = "Ranking: " + (i+1) + " Performance Score: " + perfScores.get(i++);
				temp.add(alternative, str);
			}
			temp.add("Average Performance Score", " " + avg);
			temp.add("Standard Deviation", " " + this.getStdDeviation(perfScores));
			builder.add("Topsis Performance Scores", temp);
		}
		else {
			return "";
		}
		javax.json.JsonObject json = builder.build();
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		Object jsonObject = mapper.readValue(json.toString(), Object.class);
		String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		return result;
	}

	@Override
	public double getStdDeviation(List<Double> scores) {
		double avg = 0;
		double sum = 0;
		for (double score : scores) avg += score;
		avg = avg / scores.size();
		for (double score : scores) {
			sum += Math.pow(score-avg, 2);
		}
		return Math.sqrt(sum/scores.size());
	}

	@Override
	public double[] retrieveTypes(JsonObject data) {
		String[] array = gson.fromJson(data.get("Type"), String[].class);
		double[] values = new double[array.length];
		int i = 0;
		for (String res : array) {
			if (res.equals("True")) values[i++] = 1;
			else values[i++] = 0;
		}
		return values;
	}
	
	public String[] getClassification(JsonObject data) {
		return gson.fromJson(data.get("Classification"), String[].class);
	}

	@Override
	public String[] retrieveAlternatives(JsonObject data) {
		return gson.fromJson(data.get("Alternatives"), String[].class);
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}
}
