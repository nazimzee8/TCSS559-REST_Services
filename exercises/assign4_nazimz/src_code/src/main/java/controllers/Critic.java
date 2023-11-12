package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

@SuppressWarnings("unused")
@Path("/")
public class Critic {
	
	public List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	List<Double> idealBest = new ArrayList<Double>();
	List<Double> idealWorst = new ArrayList<Double>();
	List<Double> stdDeviations = new ArrayList<Double>();
	Connection connection = null;
	
	@GET
	@Path("critic")
	@Produces("text/html")
	public Response getCriteria() throws JsonMappingException, JsonProcessingException, JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		List<Double> criteria = getWeights();
		String message = getCriticOutput(criteria);
		return Response.ok().entity(message).build();
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
	
	public List<ArrayList<Double>> normalizeMatrix() throws ClassNotFoundException, SQLException, JsonIOException, JsonSyntaxException, FileNotFoundException {
		String[] alternatives = retrieveAlternatives();
		String[] attributes = getAttributes();
		String[] classifiers = getClassifications();
		Topsis.classifiers = classifiers;
		List<ArrayList<Double>> matrix = retrieveMatrix();
		getIdealValues(matrix, classifiers);
		for (int i = 0; i < idealWorst.size(); i++) {
			for (int j = 0; j < alternatives.length; j++) {
				if (idealWorst.get(i) == idealBest.get(i)) matrix.get(j).set(i, (double) 0);
				else if (((matrix.get(j).get(i) - idealWorst.get(i)) / (idealBest.get(i) - idealWorst.get(i))) == 0) matrix.get(j).set(i, (double) 0);
				else matrix.get(j).set(i, (double)( (matrix.get(j).get(i) - idealWorst.get(i)) / (double) (idealBest.get(i) - idealWorst.get(i))) );
			}
		}
		return matrix;
	}
	
	public List<ArrayList<Double>> invertMatrix(List<ArrayList<Double>> matrix) {
		List<ArrayList<Double>> invertedMatrix = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < matrix.get(0).size(); i++) {
			invertedMatrix.add(new ArrayList<Double>());
			for (int j = 0; j < matrix.size(); j++) {
				invertedMatrix.get(i).add(matrix.get(j).get(i));
			}
		}
		return invertedMatrix;
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
	
	public List<Double> getStdDeviation(List<ArrayList<Double>> matrix) throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		String[] alternatives = retrieveAlternatives();
		List<Double> stdDeviations = new ArrayList<Double>();
		for (int i = 0; i < matrix.get(0).size(); i++) {
			double avg = 0;
			double sum = 0;
			for (int j = 0; j < alternatives.length; j++) {
				avg += matrix.get(j).get(i);
			}
			avg = avg / alternatives.length;
			for (int j = 0; j < alternatives.length; j++) {
				sum += Math.pow(matrix.get(j).get(i)-avg, 2);
			}
			stdDeviations.add(Math.sqrt(sum/alternatives.length));
		}
		return stdDeviations;
	}
	
	// Compute linear correlations
	public List<ArrayList<Double>> getSymMatrix(List<ArrayList<Double>> matrix) throws ClassNotFoundException, SQLException, JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<ArrayList<Double>> newMatrix = new ArrayList<ArrayList<Double>>();
		List<ArrayList<Double>> invertedMatrix = invertMatrix(matrix);
		for (int i = 0; i < invertedMatrix.size(); i++) {
			newMatrix.add(new ArrayList<Double>());
		}
		for (int i = 0; i < invertedMatrix.size(); i++) {
			//newMatrix.add(new ArrayList<Double>());
			List<Double> attribute1 = new ArrayList<Double>();
			List<Double> attribute2 = new ArrayList<Double>();
			for (int j = 0; j < invertedMatrix.size(); j++) {
				attribute1 = invertedMatrix.get(i);
				attribute2 = invertedMatrix.get(j);
				double corr = getCorrelationCoefficient(attribute1, attribute2);
				newMatrix.get(j).add(corr);
			}
		}
		return newMatrix;
	}
	
	public List<Double> getWeights() throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		List<ArrayList<Double>> matrix = normalizeMatrix();
		//System.out.println(matrix);
		List<Double> stdDeviations = getStdDeviation(matrix);
		//System.out.println(stdDeviations);
		List<ArrayList<Double>> newMatrix = getSymMatrix(matrix);
		//System.out.println(newMatrix);
		List<Double> criteria = new ArrayList<Double>();
		for (int i = 0; i < newMatrix.size(); i++) {
			double sum = 0;
			double result = 0;
			for (int j = 0; j < newMatrix.get(0).size(); j++) {
				result = 1 - newMatrix.get(i).get(j);
				//newMatrix.get(i).set(j, result);
				sum += result;
			}
			criteria.add(stdDeviations.get(i) * sum);
		}
		double sum = 0;
		for (int i = 0; i < criteria.size(); i++) {
			sum += criteria.get(i);
		}
		for (int i = 0; i < criteria.size(); i++) {
			criteria.set(i, criteria.get(i)/sum);
		}
		return criteria;
	}
	
	public double getCorrelationCoefficient(List<Double> attribute1, List<Double> attribute2) throws ClassNotFoundException, SQLException {
		String[] alternatives = retrieveAlternatives();
		double Sums1 = 0;
		double Sums2 = 0;
		double product_Sums = 0;
		double Sq_Sums1 = 0;
		double Sq_Sums2 = 0;
		for (int i = 0; i < alternatives.length; i++) {
			Sums1 += attribute1.get(i);
			Sums2 += attribute2.get(i);
			product_Sums += attribute1.get(i) * attribute2.get(i);
			Sq_Sums1 += Math.pow(attribute1.get(i), 2);
			Sq_Sums2 += Math.pow(attribute2.get(i), 2);
		}
		double numerator = (attribute1.size() * product_Sums) - (Sums1 * Sums2);
		double factor1 = (attribute1.size() * Sq_Sums1) - (Math.pow(Sums1, 2));
		double factor2 = (attribute2.size() * Sq_Sums2) - (Math.pow(Sums2, 2));
		double denominator = Math.sqrt(factor1 * factor2);
		return numerator/denominator;
	}
	   
	public String getCriticOutput(List<Double> criteria) throws JsonMappingException, JsonProcessingException, JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		String[] attributes = getAttributes();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder critic = Json.createObjectBuilder();;
		for (int i = 0; i < attributes.length; i++) {
			critic.add(attributes[i], "" + criteria.get(i));
		}
		builder.add("Weights from Critic", critic);

		javax.json.JsonObject json = builder.build();
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		Object jsonObject = mapper.readValue(json.toString(), Object.class);
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
		String[] classifiers = line.split(", ");
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
	
	/*public static void main(String[] args) throws ClassNotFoundException, SQLException, JsonIOException, JsonSyntaxException, FileNotFoundException, JsonMappingException, JsonProcessingException {
		Critic c = new Critic();
		List<Double> weights = c.getWeights();
		System.out.println(weights);
		System.out.println(c.getCriticOutput(weights));
	}*/
	
}
