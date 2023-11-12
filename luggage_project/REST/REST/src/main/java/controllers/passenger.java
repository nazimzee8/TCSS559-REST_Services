package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
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


@Path("/passenger")
public class passenger {
	
//	public String mysql_ip = "34.28.81.57";
	public String mysql_ip = "34.132.73.43";
	public String username = "admin";
	public String password = "project";
    // do not change the connectStr, keep it as is!
	public String connectStr ="jdbc:mysql://" + mysql_ip + ":3306/project?user=" + username + "&password=" + password ;
	
//	@Path("/get")
//	@GET
//	@Produces("application/json")
//	public Response getAllPassengers ()  {
//		List<Flight> flights = new ArrayList<>();
//        try 
//        {
//        		Class.forName("com.mysql.cj.jdbc.Driver");
//        		Connection connection = DriverManager.getConnection(connectStr); 
//        		Statement sqlStatement = connection.createStatement();	 
//        		ResultSet resultSet = sqlStatement.executeQuery("Select * from FLIGHTS;");
//            
//        		while (resultSet.next() ) {
//        			Flight flight = new Flight(resultSet.getString("FlightNum"),
//            			resultSet.getString("Destination"), resultSet.getString("Airport"));
//        			flights.add(flight);
//        		}
//        }
//        catch(Exception e)
//        {
//            System.out.println(e);
//            return null;
//        }
//		GsonBuilder builder = new GsonBuilder(); 
//	    builder.setPrettyPrinting();
//		Gson gson = builder.create(); 
//		return Response.ok().entity(gson.toJson(flights)).build();  
//	}
	
	
	@Path("/add")
	@POST
	@Produces("application/json")
	public Response addFlight(@HeaderParam("userID") int userID, 
			@HeaderParam("userName") String userName,
			@HeaderParam("password") String password,
			@HeaderParam("flightNum") int flightNum,
			@HeaderParam("flightStatus") boolean flightStatus) {
		
        try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    String SQL = "INSERT INTO PASSENGER VALUES ("
    	    		+ "" + userID 		+ ","
    	    		+ "\"" + userName 		+ "\","
    	    		+ "\"" + password 		+ "\","
    	    		+ "" + flightNum 		+ ","
    	    		+ "" + flightStatus 		    + ")";
            System.out.println(SQL);
            
        	Connection connection = DriverManager.getConnection(connectStr); 
    		Statement sqlStatement = connection.createStatement();	 
    		sqlStatement.executeUpdate(SQL);
    		connection.close();
    	    String message="record was added to the database";

            
    	    return Response
            	      .status(Response.Status.OK)
            	      .header("table", "PASSENGER")
            	      .entity(message)
            	      .build();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
	}
	
//	@Path("/update/{flightNum}")
//	@PUT
//	@Produces("application/json")
//	public Response updateFlight(@HeaderParam("flightNum") String flightNum, 
//			@HeaderParam("destination") String destination,
//			@HeaderParam("airport") String airport) {
//		
//        try {
//    	    Class.forName("com.mysql.cj.jdbc.Driver");
//    	    String SQL = "UPDATE FLIGHTS SET "
//            		+ "Destination = \"" + destination + "\","
//            		+ "Airport = \"" + airport + "\"" 
//            		+ " WHERE FlightNum = \"" + flightNum + "\"" ;
//            System.out.println(SQL);
//            
//        	Connection connection = DriverManager.getConnection(connectStr); 
//    		Statement sqlStatement = connection.createStatement();	 
//    		sqlStatement.executeUpdate(SQL);
//    		connection.close();
//    	    String message="record was updated in the database";
//
//            
//    	    return Response
//            	      .status(Response.Status.OK)
//            	      .header("table", "FLIGHTS")
//            	      .entity(message)
//            	      .build();
//        }
//        catch(Exception e)
//        {
//            System.out.println(e);
//            return null;
//        }
//	}
//	
//	@Path("/delete/{flightNum}")
//	@DELETE
//	@Produces("application/json")
//	public Response deleteFlight(@HeaderParam("flightNum") String flightNum) {
//		
//        try {
//    	    Class.forName("com.mysql.cj.jdbc.Driver");
//    	    String SQL = "DELETE FROM FLIGHTS WHERE FlightNum = \"" + flightNum + "\"";
//            System.out.println(SQL);
//            
//        	Connection connection = DriverManager.getConnection(connectStr); 
//    		Statement sqlStatement = connection.createStatement();	 
//    		sqlStatement.executeUpdate(SQL);
//    		connection.close();
//    	    String message="record was deleted from the database";
//
//            
//    	    return Response
//            	      .status(Response.Status.OK)
//            	      .header("table", "FLIGHTS")
//            	      .entity(message)
//            	      .build();
//        }
//        catch(Exception e)
//        {
//            System.out.println(e);
//            return null;
//        }
//	}
//	
//	@Path("/delete")
//	@DELETE
//	@Produces("application/json")
//	public Response deleteAllFlights() {
//		
//        try {
//    	    Class.forName("com.mysql.cj.jdbc.Driver");
//    	    String SQL = "DELETE FROM FLIGHTS";
//            System.out.println(SQL);
//            
//        	Connection connection = DriverManager.getConnection(connectStr); 
//    		Statement sqlStatement = connection.createStatement();	 
//    		sqlStatement.executeUpdate(SQL);
//    		connection.close();
//    	    String message="All records were deleted from the database";
//
//            
//    	    return Response
//            	      .status(Response.Status.OK)
//            	      .header("table", "FLIGHTS")
//            	      .entity(message)
//            	      .build();
//        }
//        catch(Exception e)
//        {
//            System.out.println(e);
//            return null;
//        }
//	}
	
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
