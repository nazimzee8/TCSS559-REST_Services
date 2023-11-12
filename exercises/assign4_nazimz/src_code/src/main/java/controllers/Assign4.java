package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@SuppressWarnings("unused")
@Path("/")
public class Assign4 {
	private static final String UPLOAD_DIR = "uploads";
	public static String absolutePath = "C:" + File.separator + "Users" + File.separator + "nazimz" + File.separator + "eclipse-workspace" + File.separator + "Assign4F" + File.separator + UPLOAD_DIR;
	public static StringBuilder uploadFilePath = new StringBuilder(128);
	
	public static String IP = "34.173.166.232";
	public static String username = "nazimz";
	public static String password = "UWTacoma";
	public static final String CONNECT_STR = "jdbc:mysql://" + IP + ":3306/Car?user="
			+ username + "&password=" + password;
	private Connection connection = null;
	PreparedStatement preparedStatement = null;
	public static Gson gson = new Gson();
	Topsis topsis = new Topsis();
	static File file;
	
	
    @Path("test") 
    @GET 
    @Produces("text/html") 
    public Response GET() { 
        // create a simple string that contains HTML text 
		try {
			String[] attributes = getAttributes();
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(CONNECT_STR);
			Statement sqlStatement = connection.createStatement();
			ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM CAR");
			StringBuilder message = new StringBuilder(128);
			while (resultSet.next()) {
				org.json.JSONObject jsonObject = new JSONObject();
				jsonObject.put("Alternative", resultSet.getString("Alternative"));
				for (int i = 0; i < attributes.length; i++) {
					jsonObject.put(attributes[i], resultSet.getString(attributes[i]));
				}
				message.append(jsonObject.toString());
			}
			connection.close();
	        return Response
					.status(Response.Status.OK)
					.header("table", "CAR")
					.entity(message.toString())
					.build();
		} 
		catch (Exception e) {
			System.out.println(e);
			return null;
		}
    }
    
    @Path("upload")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response POSTHandler(@FormDataParam("file") InputStream uploadedInputStream,
    		@FormParam("file") FormDataContentDisposition fileDetail) throws IOException {
        // gets absolute path of the web application
        //String applicationPath = request.getServletContext().getRealPath("");
		String applicationPath = absolutePath;
        // constructs path of the directory to save uploaded file
        //uploadFilePath.append(applicationPath + File.separator + UPLOAD_DIR);
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(applicationPath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());
        String fileName = fileDetail.getFileName();
        //absolutePath.append(fileSaveDir.getAbsolutePath() + File.separator + fileName);
        applicationPath += File.separator + fileName;
        this.writeToFile(uploadedInputStream, applicationPath);
        //file = new File(absolutePath.toString());
        String output = "File successfully uploaded to : " + applicationPath;  
        return Response.status(200).entity(output).build();  
    } 
	
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {
			 
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			 
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JsonObject convertFile(File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		JsonObject jsonObject = new JsonObject();
		JsonElement element = JsonParser.parseReader(new FileReader(file));
		jsonObject = element.getAsJsonObject();
		return jsonObject;
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
	
	public void createTable(JsonObject jsonObject) throws ClassNotFoundException, SQLException {
		//Create the Table/Database in the Cloud
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(CONNECT_STR);
		String[] attributes = gson.fromJson(jsonObject.get("Attributes"), String[].class);
		StringBuilder sql = new StringBuilder(128);
		sql.append("CREATE TABLE CAR ");
		sql.append("(Alternative VARCHAR(30) NOT NULL, ");
		for (int i = 0; i < attributes.length; i++) {
			sql.append(attributes[i] + " double DEFAULT '0', ");
		}
		sql.append("PRIMARY KEY (Alternative))");
		preparedStatement = connection.prepareStatement(sql.toString());
		preparedStatement.executeUpdate();
	}
	
	@Path("default")
	@GET
	@Produces("text/html")
	public Response GETDefaultHelper() throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		//applicationPath = new File(" ").getAbsolutePath();
		String applicationPath = absolutePath.toString();
		File directory = new File(applicationPath);
		//String applicationPath = uploadFilePath.toString();
		String[] file_list = directory.list();
		JsonObject selected_file = new JsonObject();
		if (file_list.length == 0) {
			throw new RuntimeException("No input file present in directory");
		}
		else {
			List<JsonObject> jsonFiles = new ArrayList<JsonObject>(); 
			for (int i = 0; i < file_list.length; i++) {
				String file = file_list[i];
				if (file.endsWith(".json")) {
					jsonFiles.add(convertFile(new File(applicationPath + File.separator + file)));
				}
			}
			if (jsonFiles.size() == 0) throw new RuntimeException("No json data available.");
			int max_attr = 0;
			selected_file = jsonFiles.get(0);
			for (int i = 0 ; i < jsonFiles.size(); i++) {
				JsonObject jsonObject = jsonFiles.get(i);
				String[] alternatives = gson.fromJson(jsonObject.get("Alternatives"), String[].class);
				String[] attributes = gson.fromJson(jsonObject.get("Attributes"), String[].class);
				String[] selected_attr = gson.fromJson(selected_file.get("Attributes"), String[].class);
				if (max_attr < attributes.length) selected_file = jsonObject;
				else if (max_attr == attributes.length) {
					if (selected_attr.length < attributes.length) selected_file = jsonObject;  
				}
				max_attr = Math.max(max_attr, attributes.length);
			}
		}
		if (!selected_file.equals(new JsonObject())) return this.parseJSON(selected_file);
		else return Response.status(Status.METHOD_NOT_ALLOWED).entity("No json data available.").build();
	}
	
	@Path("{filename}")
	@GET
	@Produces("text/html")
	public Response GETHandler(@PathParam("filename") String filename) throws JsonIOException, JsonSyntaxException, ClassNotFoundException, FileNotFoundException, SQLException {
		//JsonObject paramFile = convertFile(new File(uploadFilePath.toString() + File.separator + filename));
		//String applicationPath = new File("").getAbsolutePath();
		String applicationPath = absolutePath.toString();
		file = new File(applicationPath + File.separator + filename);
		JsonObject paramFile = convertFile(file);
		JsonObject selected_file = new JsonObject();
		return this.parseJSON(paramFile);
	}
	
	@Path("process")
	@GET
	@Produces("text/html")
	public Response getProcess() throws JsonMappingException, JsonIOException, JsonSyntaxException, JsonProcessingException, ClassNotFoundException, FileNotFoundException, SQLException {
		javax.json.JsonObject output = topsis.doScoring();
		String message = topsis.convertJsonToString(output);
		return Response.ok().entity(message).build();
	}
	
	@Path("process/critic")
	@GET
	@Produces("text/html") 
	public Response getCritic() throws JsonMappingException, JsonIOException, JsonSyntaxException, JsonProcessingException, ClassNotFoundException, FileNotFoundException, SQLException {
		javax.json.JsonObject output = (javax.json.JsonObject) topsis.doScoring().get("TOPSIS with Critic");
		String message = topsis.convertJsonToString(output);
		return Response.ok().entity(message).build();
	}
	
	@Path("process/entropy")
	@GET
	@Produces("text/html") 
	public Response getEntropy() throws JsonMappingException, JsonIOException, JsonSyntaxException, JsonProcessingException, ClassNotFoundException, FileNotFoundException, SQLException {
		javax.json.JsonObject output = (javax.json.JsonObject) topsis.doScoring().get("TOPSIS with Entropy");
		String message = topsis.convertJsonToString(output);
		return Response.ok().entity(message).build();
	}
	
	@Path("process/correlation")
	@GET
	@Produces("text/html") 
	public Response getCorrelation() throws JsonMappingException, JsonIOException, JsonSyntaxException, JsonProcessingException, ClassNotFoundException, FileNotFoundException, SQLException {
		javax.json.JsonObject output = (javax.json.JsonObject) topsis.doScoring().get("Correlation Coefficient");
		String message = topsis.convertJsonToString(output);
		return Response.ok().entity(message).build();
	}
	
	
	public Response parseJSON(JsonObject jsonObject) throws ClassNotFoundException, SQLException, JsonIOException, JsonSyntaxException, FileNotFoundException {
		this.createTable(jsonObject);
		//Parse Json and upload the data to the Database
		String[] alternatives = gson.fromJson(jsonObject.get("Alternatives"), String[].class);
		String[] attributes = gson.fromJson(jsonObject.get("Attributes"), String[].class);
		double[][] entries = gson.fromJson(jsonObject.get("Entries"), double[][].class);
		for (int i = 0; i < alternatives.length; i++) {
			String alternative = alternatives[i];
			StringBuilder insert = new StringBuilder(128);
			insert.append("INSERT INTO CAR (Alternative, ");
			for (int j = 0; j < attributes.length; j++) {
				insert.append(attributes[j] + ", ");
			}
			insert.deleteCharAt(insert.length()-1);insert.deleteCharAt(insert.length()-1);
			insert.append(") VALUES (");
			insert.append("\"" + alternatives[i] + "\", ");
			for (int j = 0; j < entries[i].length; j++) {
				insert.append(entries[i][j] + ", ");
			}
			insert.deleteCharAt(insert.length()-1);insert.deleteCharAt(insert.length()-1);
			insert.append(")");
			preparedStatement = connection.prepareStatement(insert.toString());
			preparedStatement.executeUpdate();
		}
		String output = "Database successfully uploaded to: " + CONNECT_STR;  
		connection.close();
        return Response.status(200).entity(output).build();  
	}
	
	@Path("add")
	@POST
	public Response insertAlternative(@HeaderParam("alternative") String alternative, @HeaderParam("price") @DefaultValue("-1") double price,
			@HeaderParam("gas") @DefaultValue("-1") double gas, @HeaderParam("efficiency") @DefaultValue("-1") double efficiency,
			@HeaderParam("aesthetic") @DefaultValue("-1") double aesthetic, @HeaderParam("miles") @DefaultValue("-1") double miles, 
			@HeaderParam("top_speed") @DefaultValue("-1") double top_speed, @HeaderParam("space") @DefaultValue("-1") double space) {
		try {
			String[] attributes = getAttributes();
			//for (String str : attributes) System.out.println(str);
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(CONNECT_STR);
			StringBuilder sql = new StringBuilder(128);
			sql.append("INSERT INTO CAR( Alternative, ");
			for (int i = 0; i < attributes.length; i++) {
				sql.append(attributes[i] + ", ");
			}
			sql.deleteCharAt(sql.length()-1);sql.deleteCharAt(sql.length()-1);
			sql.append(") VALUES (");
			sql.append("\"" + alternative + "\", ");
			List<Double> columns = new ArrayList<Double>(Arrays.asList(price, gas, efficiency, aesthetic, miles, top_speed, space));
			for (int i = 0; i < columns.size(); i++) {
				if (columns.get(i) > 0) {
					sql.append(columns.get(i) + ", ");
				}
			}
			sql.deleteCharAt(sql.length()-1);sql.deleteCharAt(sql.length()-1);
			sql.append(")");
			preparedStatement = connection.prepareStatement(sql.toString());
			preparedStatement.executeUpdate();
			String message="Record added successfully.";
			connection.close();
			return Response
					.status(Response.Status.OK)
					.header("table", "CAR")
					.entity(message)
					.build();
		}
		catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	@Path("delete/{alternative_name}")
	@DELETE
	public Response deleteAlternative(@PathParam("alternative_name") String alternative) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(CONNECT_STR);
			StringBuilder sql = new StringBuilder(128);
			sql.append("DELETE FROM CAR WHERE Alternative = " + "\"" +  alternative + "\"");
			preparedStatement = connection.prepareStatement(sql.toString());
			preparedStatement.executeUpdate();
			String message = "Record deleted successfully.";
			connection.close();
			return Response
					.status(Response.Status.OK)
					.header("table", "CAR")
					.header("Alternative", alternative)
					.entity(message)
					.build();
		}
		catch (Exception e) {
			System.out.println(e);
			System.out.println("DELETE FROM CAR WHERE Alternative = " + "\"" + alternative + "\"");
			return null;
		}
	}
	
	@Path("update/{alternative_name}")
	@PUT
	public Response updateAlternative(@PathParam("alternative_name") String alternative, 
			@HeaderParam("price") double price, @HeaderParam("miles") double miles) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(CONNECT_STR);
			StringBuilder sql = new StringBuilder(128);
			sql.append("UPDATE CAR SET Price = " + price + ", Miles = " + miles);
			sql.append("WHERE Alternative = " + "\"" + alternative + "\"");
			preparedStatement = connection.prepareStatement(sql.toString());
			preparedStatement.executeUpdate();
			String message = "Record updated successfully.";
			return Response
					.status(Response.Status.OK)
					.header("table", "CAR")
					.header("Alternative", alternative)
					.entity(message)
					.build();
		}
		catch (Exception e) {
			StringBuilder sql = new StringBuilder(128);
			sql.append("UPDATE CAR SET Price = " + price + ", Miles = " + miles);
			sql.append("WHERE Alternative = " + "\"" + alternative + "\"");
			System.out.println(e);
			System.out.println(sql);
			return null;
		}
	}
	
	
	@Path("delete")
	@DELETE
	public Response deleteTable() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(CONNECT_STR);
			StringBuilder sql = new StringBuilder(128);
			sql.append("DROP TABLE CAR");
			preparedStatement = connection.prepareStatement(sql.toString());
			preparedStatement.executeUpdate();
			String message = "All records deleted.";
			connection.close();
			return Response
					.status(Response.Status.OK)
					.header("table", "CAR")
					.entity(message)
					.build();
		}
		catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
