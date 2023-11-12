package controllers;

import javax.ws.rs.ApplicationPath; 
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.util.HashSet;
import java.util.Set;
// *********************************************************************** 
// define a base URI for which all of the resource
// URI will point to this application default path
// we will use "/api".
// you can have a structure for API versioning // using this technique. For example:
// "/v1" for supporting version 1 release
// "/v2" for supporting version 2 release
// "/v2.1" for supporting version 2.1 release
// *********************************************************************** //more details can be found on this link: //https://docs.oracle.com/javaee/7/api/javax/ws/rs/ApplicationPath.html 
@ApplicationPath("/api")
// This main java class will be used to declare a root
// resource for our application as well as other
// provider classes
public class main extends Application{
// This method returns a collection (non-empty) with
// specific classes to provide support for which are
// going to be handled when published our JAX-RS application 
	@Override
	
	public Set<Class<?>> getClasses() {
	
		HashSet h = new HashSet<Class<?>>();
		//add classes that you wish to be supported by application 
		h.add( REST.class );
		h.add( flight.class );
		h.add( luggage.class );
		h.add(passenger.class);
		h.add(MultiPartFeature.class);
		h.add( employee.class );
		return h;
	}
}