package com.track.backend.rest.services;

import java.io.IOException;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.track.backend.utilities.JsonWebToken;
import com.track.backend.model.dao.TrackUserDAO;
import com.track.backend.model.entities.TrackUser;
import com.track.backend.services.JsonWebTokenService;
import com.track.backend.utilities.TextFeedback;

@Component
@Path("/AuthenticationService")
public class AuthenticationService {
	
	private final static Logger logger = Logger.getLogger(AuthenticationService.class);
	
	@Autowired
	private TrackUserDAO trackUserDAO;
	
	@Autowired
	private JsonWebTokenService jsonWebTokenService;
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public TrackUser restTest(){
		TrackUser user = new TrackUser();
        user.setAccountCreationTime(new java.util.Date());
        user.setEmail("adam@adam.com");
        user.setUsername("adam");
        user.setPassword("adam");
        user.setName("Adam");
        user.setSurname("Adam");
        user.setPhone("999999999");
        //app.userDAO.add(user);
        //logger.info("jasonWebTokenService:"+jasonWebTokenService);
        logger.info("trackUserDAO:"+trackUserDAO);
		return user;
		
	}
	
	  @Path("/status")
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String returnVersion() {
	    return "Login service is OK...";
	  }
	  
	  
	  private TrackUser validateUser(final String username, final String password){
		  TrackUser user = new TrackUser();
		  try{
			  user = trackUserDAO.findUserByUsername(username);
		  }catch (PersistenceException e){
			  return null;
		  }
		  if(user.getPassword().equals(password)){
			  return user;
		  }
		  else{
			  return null;
		  }
	  }
	  
	  private boolean usernameExists(final String username){
		  TrackUser user = new TrackUser();
		  try{
			  user = trackUserDAO.findUserByUsername(username);
		  }catch (NoResultException e){
			  return false;
		  }
		  return true;
	  }
	  
	  @Path("/login")
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response authenticateCredentials(@HeaderParam("username") String username,
			  @HeaderParam("password") String password)
					  throws JsonGenerationException, JsonMappingException, IOException {
	    if (username == null || password == null) {
	      TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
	    		  "Username and/or password value are/is missing");
	      return Response.status(Status.PRECONDITION_FAILED.getStatusCode()).entity(textFeedback).build();
	    }
	     	 
	    TrackUser user = validateUser(username, password); 
	    
	    if (user == null) {
	      TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
	    		  "Wrong username and/or password");
	      return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
	    }
	    JsonWebToken jwt=jsonWebTokenService.generateFor(username);
	    return Response.status(Status.OK).entity(jwt).build();
	  }
	  
	  @Path("/register")
	  @POST
	  @Consumes(MediaType.APPLICATION_JSON)
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response register(TrackUser trackUser)throws JsonGenerationException, JsonMappingException, IOException {
	    if (trackUser.getUsername() == null || trackUser.getPassword() == null || trackUser.getRepeatPassword()==null) {
	      TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
	    		  "Username and/or password and/or repeat password value are/is missing");
	      return Response.status(Status.PRECONDITION_FAILED.getStatusCode()).entity(textFeedback).build();
	    }
	    if (!trackUser.getPassword().equals(trackUser.getRepeatPassword())) {
		      TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
		    		  "Password and repeat password value are different");
		      return Response.status(Status.PRECONDITION_FAILED.getStatusCode()).entity(textFeedback).build();
	    }
		if(usernameExists(trackUser.getUsername())){
			TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
					"Username already exists. Try another username");
			return Response.status(Status.PRECONDITION_FAILED.getStatusCode()).entity(textFeedback).build();
		}
		try{
			trackUser.setAccountCreationTime(new Date());
			trackUserDAO.add(trackUser);
		}catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
					"Cannot add user,server error");
			return Response.status(Status.SERVICE_UNAVAILABLE.getStatusCode()).entity(textFeedback).build();
		}
		TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
				"User successfully added, now You can login");
		return Response.status(Status.OK.getStatusCode()).entity(textFeedback).build();
		
	  }
	  
}