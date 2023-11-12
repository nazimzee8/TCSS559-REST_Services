package controllers;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

@Path("/")
public class Entropy {
	public List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	Connection connection = null;
	
	@GET
	@Path("entropy")
	@Produces("text/html")
	public Response getEntropy() throws JsonMappingException, JsonProcessingException, JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		List<Double> entropies = getWeights();
		String message = getEntropyOutput(entropies);
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
		List<ArrayList<Double>> matrix = retrieveMatrix();
		for (int i = 0; i < matrix.get(i).size(); i++) {
			double sum = 0;
			for (int j = 0; j < alternatives.length; j++) {
				sum += matrix.get(j).get(i);
			}
			for (int j = 0; j < alternatives.length; j++) {
				matrix.get(j).set(i, (matrix.get(j).get(i)/sum));
			}
		}
		return matrix;
	}
	
	public List<Double> computeEntropy(List<ArrayList<Double>> matrix) throws ClassNotFoundException, SQLException {
		//String[] alternatives = retrieveAlternatives();
		List<Double> entropies = new ArrayList<Double>();
		double h = -1 * (1 / Math.log(matrix.size()));
		for (int i = 0; i < matrix.get(0).size(); i++) {
			double sum = 0;
			for (int j = 0; j < matrix.size(); j++) {
				sum += (matrix.get(j).get(i) * Math.log(matrix.get(j).get(i)));
			}
			sum *= h;
			entropies.add(sum);
		}
		return entropies;
	}
	
	public List<Double> getWeights() throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		List<ArrayList<Double>> matrix = normalizeMatrix();
		List<Double> entropies = computeEntropy(matrix);
		double sum = 0;
		for (int i = 0; i < entropies.size(); i++) {
			sum += (1 - entropies.get(i));
		}
		for (int i = 0; i < entropies.size(); i++) {
			entropies.set(i, (1- entropies.get(i))/sum);
		}
		return entropies;
	}
	
	public String getEntropyOutput(List<Double> entropies) throws JsonMappingException, JsonProcessingException, ClassNotFoundException, SQLException {
		String[] attributes = getAttributes();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder entropy = Json.createObjectBuilder();;
		for (int i = 0; i < attributes.length; i++) {
			entropy.add(attributes[i], "" + entropies.get(i));
		}
		builder.add("Weights using Entropy", entropy);

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
	
	/*public static void main(String[] args) throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException, JsonMappingException, JsonProcessingException {
		Entropy e = new Entropy();
		List<Double> weights = e.getWeights();
		System.out.println(e.getEntropyOutput(weights));
	}*/
	
}
