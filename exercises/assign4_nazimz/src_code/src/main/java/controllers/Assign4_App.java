package controllers;

import javax.ws.rs.ApplicationPath; 
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.util.HashSet; 
import java.util.Set; 

@ApplicationPath("/") 

public class Assign4_App extends Application {
    @Override 
    public Set<Class<?>> getClasses() { 
        HashSet<Class<?>> resources = new HashSet<Class<?>>(); 
        //add classes that you wish to be supported by application 
        resources.add(Assign4.class); 
        resources.add(MultiPartFeature.class);
        resources.add(FileUpload.class);
        resources.add(Topsis.class);
        resources.add(Critic.class);
        resources.add(Entropy.class);
        return resources; 
    } 
}
