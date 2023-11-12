package controllers;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response; 
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

//relative URI path which will serve as the base class to host REST API 
// http://localhost:port_number/REST
@Path("/")
public class REST {
	// @Path: relative path for hosting GET HTTP requests
	//        note the empty string which maps to the relative path of the REST class
	//        http://localhost:port_number/REST
	// @GET   this is used to handle a HTTP request method of type GET
	//        the behavior of this resource is determined by HTTP GET method
	// @Produces specifies the MIME media type representation the resource produces in the response
	@Path("")
	@GET
	@Produces("text/html")
	public Response GETHandler () {
	// create a simple string that contains HTML text
	String outputHTML = "" ;
	outputHTML += "<html>" ;
	outputHTML += " <body>";
	outputHTML += " <h1>TCSS 559: Assignment 4 Demo</h1>"; outputHTML += " <h3>Building a RESTful API in Java</h3>"; outputHTML += " </body>";
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