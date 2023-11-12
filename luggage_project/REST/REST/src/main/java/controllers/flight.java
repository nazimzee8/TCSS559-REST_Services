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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.Flight;

@Path("/flight")
public class flight {
	
//	public String mysql_ip = "34.28.81.57";
	public String mysql_ip = "34.27.185.186";
	public String username = "admin";
	public String password = "project";
    // do not change the connectStr, keep it as is!
	public String connectStr ="jdbc:mysql://" + mysql_ip + ":3306/project?user=" + username + "&password=" + password ;
	
	@Path("/search")
	@GET
	public Response searchFlight () throws FileNotFoundException {
		// create a simple string that contains HTML text
		Scanner scanner = new Scanner(new File("/Users/liwenqian/eclipse-workspace/559_assignment2/REST/src/main/webapp/flight.jsp"));
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
	public Response displayFlight (@FormParam("flightNum") String flightNum) throws FileNotFoundException {
		// create a simple string that contains HTML text
		Flight flight = null;
        try 
        {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		ResultSet resultSet = sqlStatement.executeQuery("Select * from FLIGHTS WHERE FlightNum = " + "\"" + flightNum 		+ "\";");
            
        		while (resultSet.next() ) {
        			flight = new Flight(resultSet.getString("FlightNum"),
            			resultSet.getString("DepartingAirport"), 
            			resultSet.getString("ArrivingAirport"), 
            			resultSet.getString("FlightStatus"));
        		}
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
        
        Scanner scanner = new Scanner(new File("/Users/liwenqian/eclipse-workspace/559_assignment2/REST/src/main/webapp/flight.html"));
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			sb.append(line);
			if (line.equals("            <h3 style=\"text-align: center\">Flight Info</h3>")) {
				sb.append("            <table style=\"width:100%\">\n"
						+ "  				<tr>\n"
						+ "    				<th>Flight Number</th>\n"
						+ "    				<th>Departing Airport</th>\n"
						+ "    				<th>Arriving Airport</th>\n"
						+ "    				<th>Flight Status</th>\n"
						+ "  				</tr>\n"
						+ "  				<tr>");
				sb.append("  					<td>" + flight.getFlightNum() + "</td>");
				sb.append("  					<td>" + flight.getDepartingAirport() + "</td>");
				sb.append("  					<td>" + flight.getArrivingAirport() + "</td>");
				sb.append("  					<td>" + flight.getFlightStatus() + "</td>");
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
	public Response getAllFlights ()  {
		List<Flight> flights = new ArrayList<>();
        try 
        {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		Connection connection = DriverManager.getConnection(connectStr); 
        		Statement sqlStatement = connection.createStatement();	 
        		ResultSet resultSet = sqlStatement.executeQuery("Select * from FLIGHTS;");
            
        		while (resultSet.next() ) {
        			Flight flight = new Flight(resultSet.getString("FlightNum"),
            			resultSet.getString("DepartingAirport"), 
            			resultSet.getString("ArrivingAirport"), 
            			resultSet.getString("FlightStatus"));
        			flights.add(flight);
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
		return Response.ok().entity(gson.toJson(flights)).build();  
	}
	
	
	@Path("/add")
	@POST
	@Produces("application/json")
	public Response addFlight(@HeaderParam("flightNum") String flightNum, 
			@HeaderParam("departingAirport") String departingAirport,
			@HeaderParam("arrivingAirport") String arrivingAirport,
			@HeaderParam("flightStatus") String flightStatus) {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "INSERT INTO FLIGHTS VALUES ("
    	    		+ "\"" + flightNum 		+ "\","
    	    		+ "\"" + departingAirport 		+ "\","
    	    		+ "\"" + arrivingAirport 		+ "\","
    	    		+ "\"" + flightStatus 		    + "\")";
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="record was added to the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "FLIGHTS")
            	      .entity(message)
            	      .build();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
	}
	
	@Path("/update/{flightNum}")
	@PUT
	@Produces("application/json")
	public Response updateFlight(@HeaderParam("flightNum") String flightNum, 
			@HeaderParam("departingAirport") String departingAirport,
			@HeaderParam("arrivingAirport") String arrivingAirport,
			@HeaderParam("flightStatus") String flightStatus) {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "UPDATE FLIGHTS SET "
            		+ "DepartingAirport = \"" + departingAirport + "\","
            		+ "ArrivingAirport = \"" + arrivingAirport + "\"," 
            		+ "FlightStatus = \"" + flightStatus + "\"" 
            		+ " WHERE FlightNum = \"" + flightNum + "\"" ;
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="record was updated in the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "FLIGHTS")
            	      .entity(message)
            	      .build();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
	}
	
	@Path("/delete/{flightNum}")
	@DELETE
	@Produces("application/json")
	public Response deleteFlight(@HeaderParam("flightNum") String flightNum) {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "DELETE FROM FLIGHTS WHERE FlightNum = \"" + flightNum + "\"";
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="record was deleted from the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "FLIGHTS")
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
	public Response deleteAllFlights() {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "DELETE FROM FLIGHTS";
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="All records were deleted from the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "FLIGHTS")
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
