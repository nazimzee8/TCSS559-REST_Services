package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.Flight;
import models.Luggage;

@Path("/luggage")
public class luggage {
//	public String mysql_ip = "34.28.81.57";
	public String mysql_ip = "34.27.185.186";
	public String username = "admin";
	public String password = "project";
    // do not change the connectStr, keep it as is!
	public String connectStr ="jdbc:mysql://" + mysql_ip + ":3306/project?user=" + username + "&password=" + password ;
	
	@Path("/search")
	@GET
	public Response searchLuggage () throws FileNotFoundException {
		// create a simple string that contains HTML text
		Scanner scanner = new Scanner(new File("/Users/liwenqian/eclipse-workspace/559_assignment2/REST/src/main/webapp/luggage.jsp"));
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
		}
		scanner.close();
		return Response
        	      .status(Response.Status.OK)
        	      .entity(sb.toString())
        	      .build();
	}
	
	@Path("/search")
	@POST
	@Produces("text/html")
	public Response displayLuggage (@FormParam("rfid") String rfid) throws FileNotFoundException {
		// create a simple string that contains HTML text
		Luggage luggage = null;
        try 
        {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		ResultSet resultSet = sqlStatement.executeQuery("Select * from LUGGAGE WHERE RFID = " + "\"" + rfid + "\";");
            
        		while (resultSet.next() ) {
        			luggage = new Luggage(resultSet.getInt("UserID"),
            			resultSet.getString("RFID"), 
            			resultSet.getString("Location"), 
            			resultSet.getString("FlightNum"));
        		}
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
        
        Scanner scanner = new Scanner(new File("/Users/liwenqian/eclipse-workspace/559_assignment2/REST/src/main/webapp/luggage.html"));
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			System.out.println(line);
			sb.append(line);
			if (line.equals("			<h3 style=\"text-align: center\">Luggage Info</h3>")) {
				sb.append("            <table style=\"width:100%\">\n"
						+ "  				<tr>\n"
						+ "    				<th>Luggage RFID</th>\n"
						+ "    				<th>User ID</th>\n"
						+ "    				<th>Flight Number</th>\n"
						+ "    				<th>Current Location</th>\n"
						+ "  				</tr>\n"
						+ "  				<tr>");
				sb.append("  					<td>" + luggage.getRFID() + "</td>");
				sb.append("  					<td>" + luggage.getUserID() + "</td>");
				sb.append("  					<td>" + luggage.getFlightNum() + "</td>");
				sb.append("  					<td>" + luggage.getLocation() + "</td>");
				sb.append("					</tr>\n"
						+ "				</table>");
			}
		}
		scanner.close();
		return Response
        	      .status(Response.Status.OK)
        	      .entity(sb.toString())
        	      .build();
 
	}
	
	@Path("/get")
	@GET
	@Produces("application/json")
	public Response getAllLuggage()  {
		List<Luggage> luggageList = new ArrayList<>();
        try 
        {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		ResultSet resultSet = sqlStatement.executeQuery("Select * from LUGGAGE;");
            
        		while (resultSet.next() ) {
        			Luggage luggage = new Luggage(resultSet.getInt("UserID"),
        					resultSet.getString("RFID"), 
        					resultSet.getString("Location"),
        					resultSet.getString("FlightNum"));
        			luggageList.add(luggage);
        		}
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
		GsonBuilder builder = new GsonBuilder(); 
	    builder.setPrettyPrinting();
		Gson gson = builder.create(); 
		return Response.ok().entity(gson.toJson(luggageList)).build();  
	}
	
	
	@Path("/add")
	@POST
	@Produces("application/json")
	public Response addLuggage(@HeaderParam("userID") int userID, 
			@HeaderParam("rfid") String rfid,
			@HeaderParam("location") String location,
			@HeaderParam("flightNum") String flightNum) {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "INSERT INTO LUGGAGE VALUES ("
    	    		+ userID + ","
    	    		+ "\"" + rfid 		+ "\","
    	    		+ "\"" + location 		+ "\","
    	    		+ "\"" + flightNum 		    + "\")";
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="record was added to the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "LUGGAGE")
            	      .entity(message)
            	      .build();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
	}
	
	@Path("/update/{rfid}")
	@PUT
	@Produces("application/json")
	public Response updateLuggage(@HeaderParam("userID") int userID, 
			@HeaderParam("rfid") String rfid,
			@HeaderParam("location") String location,
			@HeaderParam("flightNum") String flightNum) {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "UPDATE LUGGAGE SET "
    	    		+ "UserID = " + userID + ","
            		+ "Location = \"" + location + "\"," 
            		+ "FlightNum = \"" + flightNum + "\"" 
            		+ " WHERE RFID = \"" + rfid + "\"" ;
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="record was updated in the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "LUGGAGE")
            	      .entity(message)
            	      .build();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
	}
	
	@Path("/delete/{rfid}")
	@DELETE
	@Produces("application/json")
	public Response deleteLuggage(@HeaderParam("rfid") String rfid) {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "DELETE FROM LUGGAGE WHERE RFID = \"" + rfid + "\"";
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="record was deleted from the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "LUGGAGE")
            	      .entity(message)
            	      .build();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
	}
	
	@Path("/delete")
	@DELETE
	@Produces("application/json")
	public Response deleteAllLuggage() {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "DELETE FROM LUGGAGE";
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="All records were deleted from the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "LUGGAGE")
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
	@GET
	@Produces("text/html")
	public Response GETHandler () {
		// create a simple string that contains HTML text
		String outputHTML = "" ;
		outputHTML += "<html>" ;
		outputHTML += " <body>";
		outputHTML += " <h1>TCSS 559: Final Demo</h1>"; 
		outputHTML += " </body>";
	    outputHTML += "</html>";
	    return Response.ok().entity(outputHTML.toString()).build(); }
	
	@Path("")
	@POST
	@Produces("text/html")
	public Response POSTHandler() {
		String outputHTML = "" ;
		outputHTML += "<html>" ;
		outputHTML += " <body>";
		outputHTML += " <h1>Method Now Allowed or Not Supported. IP Logged.</h1>"; outputHTML += " </body>";
	    outputHTML += "</html>";
	    return Response.status(Status.METHOD_NOT_ALLOWED).entity(outputHTML.toString()).build(); 
	}
}
