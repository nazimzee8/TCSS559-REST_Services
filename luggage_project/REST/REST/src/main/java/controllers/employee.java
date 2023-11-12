package controllers;

import java.sql.*;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/employee")
public class employee {
	public String mysql_ip = "34.136.203.36";
	public String username = "wli6";
	public String password = "lwq19941219";
    // do not change the connectStr, keep it as is!
	public String connectStr ="jdbc:mysql://" + mysql_ip + ":3306/company?user=" + username + "&password=" + password ;

	@Path("")
	@GET
	@Produces("application/json")
	public Response SelectRecord ()  {
	        try {
	            
	        	Class.forName("com.mysql.cj.jdbc.Driver");
	        	Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		ResultSet resultSet = sqlStatement.executeQuery("Select * from EMPLOYEE;");
	            JSONObject emplJSON = new JSONObject();
	            JSONArray emplArray = new JSONArray();
	            while (resultSet.next() ) {
		            JSONObject emplObject = new JSONObject();
	            	emplObject.put("SSN", resultSet.getString("SSN"));
	            	emplObject.put("firstName", resultSet.getString("Fname"));
	            	emplObject.put("lastName", resultSet.getString("Lname"));
	            	emplArray.put(emplObject);
	            }
	            
	            emplJSON.put("employees", emplArray);
	            return Response
	            	      .status(Response.Status.OK)
	            	      .header("table", "EMPLOYEE")
	            	      .entity(emplJSON.toString())
	            	      .build();
	        }
	        catch(Exception e)
	        {
	            System.out.println(e);
	            return null;
	        }
	    }
	
	@Path("{ssn}")
	@GET
	@Produces("application/json")
	public Response SelectRecord (@PathParam("ssn") String ssn)  {
	        try {
	            
	        	Class.forName("com.mysql.cj.jdbc.Driver");
	        	Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		ResultSet resultSet = sqlStatement.executeQuery("Select * from EMPLOYEE WHERE SSN = " + ssn );
	            String message="no matching results found";

	            while (resultSet.next() ) {
	            	JSONObject emplObject = new JSONObject();
	            	emplObject.put("SSN", resultSet.getString("SSN"));
	            	emplObject.put("firstName", resultSet.getString("Fname"));
	            	emplObject.put("lastName", resultSet.getString("Lname"));
	            	emplObject.put("Address", resultSet.getString("Address"));
	            	message = emplObject.toString();
	            }

	            connection.close();
	            return Response
	            	      .status(Response.Status.OK)
	            	      .header("table", "EMPLOYEE")
	            	      .entity(message)
	            	      .build();
	        }
	        catch(Exception e)
	        {
	            System.out.println(e);
	            return null;
	        }	            
	        }
	
	@Path("")
	@POST
	@Produces("application/json")
	public Response createRecord ()  {
	        try { 
	            String fname = "Jane";
	            String Minit = "M";
	            String lname = "Doe";
	            String SSN = "123456777";
	            String DoB = "1995-01-09";
	            String Address = "123 Some Address, NY, USA 20001";
	            String Gender = "M";
	            int Salary = 45000;
	            String superSSN = "123456789";
	            int Dno = 5;

	            String SQL = "INSERT INTO EMPLOYEE VALUES ("
	            		+ "\"" + fname 		+ "\","
	            		+ "\"" + Minit 		+ "\","
	            		+ "\"" + lname 		+ "\"," 
	            		+ "\"" + SSN 		+ "\","
	            		+ "\"" + DoB 		+ "\","
	            		+ "\"" + Address 	+ "\"," 
	            		+ "\"" + Gender 	+ "\", " 
	            		+  Salary 	+ ","
	            		+ "\"" + superSSN 	+ "\","
	            		+ Dno + ")";
	            System.out.println(SQL);
	            
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        	Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		sqlStatement.executeUpdate(SQL);
	            String message="record was added to the database";

	            connection.close();
	            return Response
	            	      .status(Response.Status.OK)
	            	      .header("table", "EMPLOYEE")
	            	      .header("fName", fname)
	            	      .header("lName", lname)
	            	      .entity(message)
	            	      .build();
	        }
	        catch(Exception e)
	        {
	            System.out.println(e);
	            return null;
	        }	            
	        }
	
	@Path("{ssn}/{lname}")
	@PUT
	@Produces("application/json")
	public Response updateRecord (@PathParam("ssn") String ssn, @PathParam("lname") String lname)  {
	        try {   
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        	Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		sqlStatement.executeUpdate("UPDATE EMPLOYEE SET Lname  = \""+lname+"\" WHERE SSN = " + ssn);
	            String message="record updated";
	            
	            connection.close();
	            return Response
	            	      .status(Response.Status.OK)
	            	      .header("table", "EMPLOYEE")
	            	      .header("SSN", ssn)
	            	      .entity(message)
	            	      .build();
	        }
	        catch(Exception e)
	        {
	            System.out.println(e);
	            return null;
	        }	            
	        }
	
	@Path("{ssn}")
	@DELETE
	@Produces("application/json")
	public Response deleteRecord (@PathParam("ssn") String ssn)  {
	        try {
	          
	        	Class.forName("com.mysql.cj.jdbc.Driver");
	        	Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		sqlStatement.executeUpdate("DELETE FROM EMPLOYEE WHERE SSN = " + ssn );
	            String message="record was deleted";

	            connection.close();
	            return Response
	            	      .status(Response.Status.OK)
	            	      .header("table", "EMPLOYEE")
	            	      .header("SSN", ssn)
	            	      .entity(message)
	            	      .build();
	        }
	        catch(Exception e)
	        {
	            System.out.println(e);
	            return null;
	        }	            
	        }
	    
}
