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
import com.tcss559.uploadClient;

/**
 * Servlet implementation class WSM
 */
@WebServlet("/WSM")
public class WSM extends HttpServlet implements MCDA {
	private static final long serialVersionUID = 1L;
	List<Double> weights;
	List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	List<Double> weightedSums = new ArrayList<Double>();
	File file;
    Gson gson = new Gson();
	StringBuilder output = new StringBuilder(128);
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WSM() {
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
        if (request.getRequestURL().toString().contains("WSM")) outputHTML.print("<h2> " + output + " </h2>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");           // set content-type to HTML 
        response.setHeader("restApp-version", "1.0");   // create custom header attribute for version 1.0 
	    String output = this.doScoring(request, response);
        PrintWriter outputHTML = response.getWriter();
        outputHTML.print("<h2> " + output + " </h2>");
	}
	
	@Override
	public String doScoring(HttpServletRequest request, HttpServletResponse response) throws JsonIOException, JsonSyntaxException, FileNotFoundException, JsonMappingException, JsonProcessingException {
        if (!UploadServlet.getFileUpload().equals(null)) setFile(new File(UploadServlet.getFileUpload()));
        else if (request.getParameter("file") != null) setFile(new File("C:/Users/nazimz/Documents/TCSS559/apache-tomcat-9.0.62/wtpwebapps/Quiz2/uploads/" + request.getParameter("file")));
        else setFile(new File("C:/Users/nazimz/Documents/TCSS559/apache-tomcat-9.0.62/wtpwebapps/Quiz2/uploads/" + "data.json"));
		JsonObject data = this.parseJSON(file);
        this.getWeights(data);
        this.retrieveMatrix(data);
        this.normalizeMatrix();
        this.WeightedValues(data);
        
        // Retrieve Performance Ranking
        String[] alternatives = this.retrieveAlternatives(data);
        this.BubbleSort(weightedSums, alternatives);
        
        StringBuilder output = new StringBuilder(128);
        if (request.getParameter("options") != null) {
			output.append(this.getJSONOutput(alternatives, weightedSums, request.getParameter("options")));
		}
		else {
			output.append(this.getJSONOutput(alternatives, weightedSums));
		}
        return output.toString();
	}
	
	@Override
	public String retrieveOutput() {
		return this.output.toString();
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

	@Override
	public void normalizeMatrix() {
		for (int i = 0; i < weights.size(); i++) {
			double sum = 0;
			for (int j = 0; j < matrix.size(); j++) {
				sum += matrix.get(j).get(i);
			}
			for (int j = 0; j < matrix.size(); j++) {
				double value = matrix.get(j).get(i);
				matrix.get(j).set(i, value / sum);
			}
		}
	}

	@Override
	public void getWeights(JsonObject data) {
		double[] array = gson.fromJson(data.get("Weights"), double[].class);
    	weights = new ArrayList<Double>();
    	double sum = 0;
    	for (double w : array) { 
    		if (w < 0) throw new RuntimeException("Please enter a positive value for weight.");
    		weights.add(w);
    		sum += w;
    	}
    	if (sum > Math.abs(1)) {
    		throw new RuntimeException("Sum of weights must be between 0 and 1!");
    	}
	}

	@Override
	public void WeightedValues(JsonObject data) {
		double[] values = this.retrieveTypes(data);
		weightedSums = new ArrayList<Double>();
		for (int i = 0; i < matrix.size(); i++) {
			double sum = 0;
			for (int j = 0; j < weights.size(); j++) { 
				sum += (values[j] * weights.get(j) * matrix.get(i).get(j));
			}
			weightedSums.add(sum);
		}
	}
	
	public List<Double> getWeightedSums() {
		return weightedSums;
	}

	@Override
	public void BubbleSort(List<Double> weightedSums, String[] alternatives) {
		for (int i = 0; i < weightedSums.size(); i++) {
            for (int j = 0; j < weightedSums.size(); j++) {
                Double temp = weightedSums.get(j);
                String tempAlt = alternatives[j];
                if (j > 0 && temp > weightedSums.get(j - 1)) {
                    weightedSums.set(j, weightedSums.get(j-1));
                    alternatives[j] = alternatives[j-1];
                    weightedSums.set(j-1, temp);
                    alternatives[j-1] = tempAlt;
                }
            }
        }
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

	@Override
	public JsonObject parseJSON(File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		JsonObject jsonObject = new JsonObject();
		JsonElement element = JsonParser.parseReader(new FileReader(file));
		jsonObject = element.getAsJsonObject();
		return jsonObject;
	}

	@Override
	public String[] retrieveAlternatives(JsonObject data) {
		return gson.fromJson(data.get("Alternatives"), String[].class);
	}
	
	public String getJSONOutput(String[] alternatives, List<Double> weightedSums) throws JsonMappingException, JsonProcessingException {
		return getJSONOutput(alternatives, weightedSums, "wsm");
	}

	@Override
	public String getJSONOutput(String[] alternatives, List<Double> weightedSums, String options) throws JsonMappingException, JsonProcessingException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (options.equals("basic")) {
			JsonObjectBuilder temp = Json.createObjectBuilder();
			for (int i = 0; i < weightedSums.size(); i++) {
				temp.add(String.valueOf(i+1), weightedSums.get(i));
			}
			builder.add("Weighted Sum Model Performance Scores", temp);
		}
		else if (options.equals("wsm") || options.equals("both")){
			int i = 0;
			double avg = 0;
			for (double score : weightedSums) avg += score;
			avg = avg / weightedSums.size();
			JsonObjectBuilder temp = Json.createObjectBuilder();
			for (String alternative : alternatives) {
				String str = "Ranking: " + (i+1) + " Performance Score: " + weightedSums.get(i++);
				temp.add(alternative, str);
			}
			temp.add("Average Performance Score", " " + avg);
			temp.add("Standard Deviation", " " + this.getStdDeviation(weightedSums));
			builder.add("Weighted Sum Model Performance Scores", temp);
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
	public void setFile(File file) {
		this.file = file;
	}

}
