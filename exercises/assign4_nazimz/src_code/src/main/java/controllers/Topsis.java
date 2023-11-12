package controllers;

import javax.ws.rs.Path; 
import javax.ws.rs.core.Response; 
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import java.sql.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST; 
import javax.ws.rs.Produces; 

@SuppressWarnings("unused")
@Path("/")
public class Topsis {
	List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	List<Double> perfScores;
	List<Double> bestEuclidVals = new ArrayList<Double>();
	List<Double> worstEuclidVals = new ArrayList<Double>();
	List<Double> idealBest = new ArrayList<Double>();
	List<Double> idealWorst = new ArrayList<Double>();
	public static String[] classifiers;
	
	public Topsis() {
		//classifiers = getClassifications();
	}
	
	Gson gson = new Gson();
	File file;
	private Connection connection = null;
	PreparedStatement preparedStatement = null;
	StringBuilder output = new StringBuilder(128);
	
	@GET
	@Path("topsis")
	@Produces("text/html")
	public Response getProcess() throws ClassNotFoundException, SQLException, JsonIOException, JsonSyntaxException, FileNotFoundException, JsonMappingException, JsonProcessingException {
		String message = convertJsonToString(doScoring());
		return Response.ok().entity(message).build();
	}
	
	@GET
	@Path("topsis/entropy") 
	@Produces("text/html")
	public Response getEntropy() throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException, JsonMappingException, JsonProcessingException {
		String message = convertJsonToString((javax.json.JsonObject) doScoring().get("TOPSIS with Entropy"));
		return Response.ok().entity(message).build();
	}
	
	@GET
	@Path("topsis/critic")
	@Produces("text/html")
	public Response getCritic() throws JsonMappingException, JsonProcessingException, JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		String message = convertJsonToString((javax.json.JsonObject) doScoring().get("TOPSIS with Critic"));
		return Response.ok().entity(message).build();
	}
	
	@GET
	@Path("topsis/correlation") 
	@Produces("text/html")
	public Response getCorrelation() throws JsonMappingException, JsonProcessingException, JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		String message = convertJsonToString((javax.json.JsonObject) doScoring().get("Correlation Coefficient"));
		return Response.ok().entity(message).build();
	}
	
	public javax.json.JsonObject doScoring() throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException, JsonMappingException, JsonProcessingException {
		Critic critic = new Critic();
		Entropy entropy = new Entropy();
		//Topsis with criteria
		List<Double> criteria = critic.getWeights();
		List<ArrayList<Double>> critMatrix = retrieveMatrix();
		List<ArrayList<Double>> entropyMatrix = critMatrix;
		critMatrix = normalizeMatrix(critMatrix, criteria);
		List<Double> criticScores = this.getPerformance(critMatrix, criteria, classifiers);
		String[] criticAlts = retrieveAlternatives();
		String[] entropyAlts = criticAlts;
		this.BubbleSort(criticScores, criticAlts);
		
		//Topsis with entropy
		List<Double> entropies = entropy.getWeights();
		entropyMatrix = normalizeMatrix(entropyMatrix, entropies);
		List<Double> entropyScores = this.getPerformance(entropyMatrix, entropies, classifiers);
		this.BubbleSort(entropyScores, entropyAlts);
		
		//Get JsonOutput
		javax.json.JsonObject output = this.getJSONOutput(criticScores, entropyScores, criticAlts, entropyAlts);
		//String output = this.convertJsonToString(jsonOutput);
		return output;
	}

	public List<ArrayList<Double>> retrieveMatrix() throws SQLException, ClassNotFoundException {
		List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		String[] attributes = getAttributes();
		StringBuilder sql = new StringBuilder(128);
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(Assign4.CONNECT_STR); 
		Statement sqlStatement = connection.createStatement();
		sql.append("SELECT ");
		for (int i = 0; i < attributes.length; i++) {
			sql.append(attributes[i] + ", ");

		}
		sql.deleteCharAt(sql.length()-1);sql.deleteCharAt(sql.length()-1);
		sql.append(" FROM CAR");
		ResultSet resultSet = sqlStatement.executeQuery(sql.toString());
		int count = 0;
		while (resultSet.next()) {
			matrix.add(new ArrayList<Double>());
			for (int i = 0; i < attributes.length; i++) {
				matrix.get(count).add(Double.valueOf(resultSet.getString(attributes[i])));
			}
			count++;
		}
		connection.close();
		return matrix;
	}
	
    public List<ArrayList<Double>> normalizeMatrix(List<ArrayList<Double>> matrix, List<Double> weights) {
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
    	return matrix;
    }
    
    public void getIdealValues(List<ArrayList<Double>> matrix, String[] classifiers) throws JsonIOException, JsonSyntaxException, FileNotFoundException, ClassNotFoundException, SQLException {
		idealBest = new ArrayList<Double>();
		idealWorst = new ArrayList<Double>();
		for (int i = 0; i < classifiers.length; i++) {
			double max = 0;
			double min = Double.POSITIVE_INFINITY;
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
	
	/*public boolean validateClassifier(String[] classifiers) {
		for (int i = 0; i < classifiers.length; i++) {
			if (!classifiers[i].equals("Maximum") && !classifiers[i].equals("Minimum")) {
				return false;
			}
		}
		return true;
	}*/
	
	public void WeightedValues(List<ArrayList<Double>> matrix, List<Double> weights) {
		bestEuclidVals = new ArrayList<Double>();
		worstEuclidVals = new ArrayList<Double>();
		for (int i = 0; i < matrix.size(); i++) {
			double bestSum = 0;
			double worstSum = 0;
			for (int j = 0 ; j < weights.size(); j++) {
				bestSum += (Math.pow(matrix.get(i).get(j)-idealBest.get(j), 2));
				worstSum += (Math.pow(matrix.get(i).get(j)-idealWorst.get(j), 2));
			}
			bestEuclidVals.add(Math.sqrt(bestSum));
			worstEuclidVals.add(Math.sqrt(worstSum));
		}	
	}
	
	public List<Double> getPerformance(List<ArrayList<Double>> matrix, List<Double> weights, String[] classifiers) throws JsonIOException, JsonSyntaxException, FileNotFoundException, ClassNotFoundException, SQLException {
		this.getIdealValues(matrix, classifiers);
		this.WeightedValues(matrix, weights);
		List<Double> perfScores = new ArrayList<Double>();
		for (int i = 0; i < bestEuclidVals.size(); i++) {
			if (bestEuclidVals.get(i) != 0 || worstEuclidVals.get(i) != 0) perfScores.add(worstEuclidVals.get(i) / (bestEuclidVals.get(i) + worstEuclidVals.get(i)));
			else perfScores.add(0.0);
		}
		return perfScores;
	}
	
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
	
	public double getCorrelationCoefficient(List<Double> criticScores, List<Double> entropyScores) throws ClassNotFoundException, SQLException {
		double Sums1 = 0;
		double Sums2 = 0;
		double product_Sums = 0;
		double Sq_Sums1 = 0;
		double Sq_Sums2 = 0;
		for (int i = 0; i < criticScores.size(); i++) {
			Sums1 += criticScores.get(i);
			Sums2 += entropyScores.get(i);
			product_Sums += criticScores.get(i) * entropyScores.get(i);
			Sq_Sums1 += Math.pow(criticScores.get(i), 2);
			Sq_Sums2 += Math.pow(entropyScores.get(i), 2);
		}
		double numerator = (criticScores.size() * product_Sums) - (Sums1 * Sums2);
		double factor1 = (criticScores.size() * Sq_Sums1) - (Math.pow(Sums1, 2));
		double factor2 = (entropyScores.size() * Sq_Sums2) - (Math.pow(Sums2, 2));
		double denominator = Math.sqrt(factor1 * factor2);
		return numerator/denominator;
	}
	
	public javax.json.JsonObject getJSONOutput(List<Double> criticScores, List<Double> entropyScores, String[] criticAlts, String[] entropyAlts) throws JsonMappingException, JsonProcessingException, JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder entropy = Json.createObjectBuilder();
		JsonObjectBuilder critic = Json.createObjectBuilder();
		JsonObjectBuilder correlation = Json.createObjectBuilder();
		correlation.add("Correlation between Critic and Entropy using Topsis", String.valueOf(this.getCorrelationCoefficient(criticScores, entropyScores)));
		for (int i = 0; i < criticAlts.length; i++) {
			String str = "Ranking: " + (i+1) + " Score: " + criticScores.get(i);
			critic.add(criticAlts[i], str);
		}
		for (int i = 0; i < entropyAlts.length; i++) {
			String str = "Ranking: " + (i+1) + " Score: " + entropyScores.get(i);
			entropy.add(entropyAlts[i], str);
		}
		builder.add("TOPSIS with Critic", critic);
		builder.add("TOPSIS with Entropy", entropy);
		builder.add("Correlation Coefficient", correlation);
		javax.json.JsonObject result = builder.build();
		return result;

		/*javax.json.JsonObject json = builder.build();
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		Object jsonObject = mapper.readValue(json.toString(), Object.class);
		String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		return result;*/
	}
	
	public String convertJsonToString(javax.json.JsonObject output) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		Object jsonObject = mapper.readValue(output.toString(), Object.class);
		String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		return result;
	}
	
	public String[] retrieveAlternatives() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = DriverManager.getConnection(Assign4.CONNECT_STR); 
		Statement sqlStatement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = sqlStatement.executeQuery("SELECT Alternative FROM CAR");
		int size = 0;
		if (resultSet != null) {
			resultSet.last();
			size = resultSet.getRow();
		}
		size = resultSet.getRow();
		String[] alternatives = new String[size];
		resultSet.beforeFirst();
		int count = 0;
		while (resultSet.next()) {
			alternatives[count] = resultSet.getString("Alternative");
			count++;
		}
		connection.close();
		return alternatives;
	}
	
	public String[] getAttributes() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(Assign4.CONNECT_STR); 
		Statement sqlStatement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = sqlStatement.executeQuery("SHOW COLUMNS FROM CAR");
		int size = 0;
		if (resultSet != null) {
			resultSet.last();
			size = resultSet.getRow();
		}
		size = resultSet.getRow();
		String[] attributes = new String[size-1];
		resultSet.first();
		int count = 0;
		while (resultSet.next()) {
			attributes[count++] = resultSet.getString(1);
			if (count == size-1) break;
		}
		connection.close();
		return attributes;
	}
	
	
	public String[] getClassifications() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter classifier as specified in select input file. (Separate by comma then space)");
		String line = scan.nextLine();
		classifiers = line.split(", ");
		scan.close();
		if (!validateClassifier(classifiers)) throw new RuntimeException("Refer to the API for proper user input.");
		return classifiers;
	}
	
	public boolean validateClassifier(String[] classifiers) {
		for (int i = 0; i < classifiers.length; i++) {
			if (!classifiers[i].equals("Maximum") && !classifiers[i].equals("Minimum")) {
				return false;
			}
		}
		return true;
	}
	
	/*public static void main(String[] args) throws ClassNotFoundException, SQLException, JsonIOException, JsonSyntaxException, FileNotFoundException, JsonMappingException, JsonProcessingException  {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(Assign4.CONNECT_STR); 
			Statement sqlStatement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = sqlStatement.executeQuery("SELECT Alternative FROM CAR");
			int size = 0;
			if (resultSet != null) {
				resultSet.last();
				size = resultSet.getRow();
			}
			size = resultSet.getRow();
			String[] alternatives = new String[size];
			resultSet.beforeFirst();
			int count = 0;
			//System.out.println(resultSet.next());
			while (resultSet.next()) {
				alternatives[count] = resultSet.getString("Alternative");
				count++;
			}
			connection.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
		Topsis t = new Topsis();
		Critic c = new Critic();
		//System.out.println(t.retrieveAlternatives());
		//for (String alt: c.retrieveAlternatives()) System.out.println(alt);
		//System.out.println(c.normalizeMatrix());
		//for (String attr: t.getAttributes()) System.out.println(attr);
		//System.out.println(c.normalizeMatrix());
	}*/
}
