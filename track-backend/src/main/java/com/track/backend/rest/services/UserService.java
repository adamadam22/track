package com.track.backend.rest.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jose4j.jwt.consumer.InvalidJwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.track.backend.utilities.JsonWebToken;
import com.track.backend.model.dao.TrackUserDAO;
import com.track.backend.model.entities.TrackUser;
import com.track.backend.utilities.JsonableList;
import com.track.backend.utilities.TextFeedback;

@Component
@Path("/UserService")
public class UserService {
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(UserService.class);
	
	@Autowired
	private TrackUserDAO trackUserDAO;

	@Path("/details")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@HeaderParam("jsonWebToken") String jsonWebToken) 
			throws JsonGenerationException, JsonMappingException, IOException {
		
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
			TrackUser user = trackUserDAO.findUserByUsername(jwt.getUsername());
			return Response.status(Status.OK).entity(user).build();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
	}
	
	@Path("/change/personal-data")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)  
	public Response changePersonalData(@HeaderParam("jsonWebToken") String jsonWebToken, TrackUser trackUser)
			throws JsonGenerationException, JsonMappingException, IOException {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
			TrackUser user = trackUserDAO.findUserByUsername(jwt.getUsername());
			user.setEmail(trackUser.getEmail());
			user.setPhone(trackUser.getPhone());
			user.setName(trackUser.getName());
			user.setSurname(trackUser.getSurname());
			trackUserDAO.update(user);
			TextFeedback textFeedback = TextFeedback.create(Status.OK.getStatusCode(),
					"Personal data successfully saved");
			return Response.status(Status.OK.getStatusCode()).entity(textFeedback).build();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"Cannot add user,server error");
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(textFeedback).build();
		}		
	}
	
	@Path("/change/password")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)  
	public Response changePassword(@HeaderParam("jsonWebToken") String jsonWebToken, TrackUser trackUser)
			throws JsonGenerationException, JsonMappingException, IOException {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
		    if (trackUser.getPassword() == null || trackUser.getRepeatPassword()==null) {
			      TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
			    		  "Password and/or repeat password value are/is missing");
			      return Response.status(Status.PRECONDITION_FAILED.getStatusCode()).entity(textFeedback).build();
		    }
		    if (!trackUser.getPassword().equals(trackUser.getRepeatPassword())) {
		    	TextFeedback textFeedback = TextFeedback.create(Status.PRECONDITION_FAILED.getStatusCode(),
		    			"Password and repeat password value are different");
		    	return Response.status(Status.PRECONDITION_FAILED.getStatusCode()).entity(textFeedback).build();
			}
		    TrackUser user = trackUserDAO.findUserByUsername(jwt.getUsername());
			user.setPassword(trackUser.getPassword());
			trackUserDAO.update(user);
			TextFeedback textFeedback = TextFeedback.create(Status.OK.getStatusCode(),
					"Personal data successfully saved");
			return Response.status(Status.OK.getStatusCode()).entity(textFeedback).build();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"Cannot add user,server error");
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(textFeedback).build();
		}		
	}
	
	@Path("/add/friend")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)  
	public Response addFriend(@HeaderParam("jsonWebToken") String jsonWebToken, @HeaderParam("friendUsername") String friendUsername)
			throws JsonGenerationException, JsonMappingException, IOException {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
			TrackUser user = trackUserDAO.findUserByUsername(jwt.getUsername());
			TrackUser friend = trackUserDAO.findUserByUsername(friendUsername);
			if(jwt.getUsername().equals(friendUsername)){
				TextFeedback textFeedback = TextFeedback.create(Status.BAD_REQUEST.getStatusCode(),
		    			"You cannot add your username to friend list");
				return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(textFeedback).build();
			}
			Set <TrackUser> friends = null;
			try{
				friends= trackUserDAO.findUserByUsername(jwt.getUsername()).getFriends();
			} catch(NoResultException e){
				friends = new HashSet<TrackUser>();
			}
		    if (!friends.add(friend)){
		    	TextFeedback textFeedback = TextFeedback.create(Status.BAD_REQUEST.getStatusCode(),
		    			"User "+friendUsername+" has been alredy added to your friends list");
				return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(textFeedback).build();	
		    }
			user.setFriends(friends);
			trackUserDAO.update(user);
			TextFeedback textFeedback = TextFeedback.create(Status.OK.getStatusCode(),
					"Friend successfully saved");
			return Response.status(Status.OK.getStatusCode()).entity(textFeedback).build();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		} catch(NoResultException e){
			TextFeedback textFeedback = TextFeedback.create(Status.BAD_REQUEST.getStatusCode(),
					"User "+friendUsername+" does not exist");
			return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(textFeedback).build();			
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"Cannot add user,server error");
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(textFeedback).build();
		}		
	}
	
	@Path("/remove/friend")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)  
	public Response removeFriend(@HeaderParam("jsonWebToken") String jsonWebToken, TrackUser friend)
			throws JsonGenerationException, JsonMappingException, IOException {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
			TrackUser user = trackUserDAO.findUserByUsername(jwt.getUsername());
			TrackUser dbfriend = trackUserDAO.findUserByUsername(friend.getUsername());
			Set <TrackUser> friends = user.getFriends();
			if(friends==null){
				TextFeedback textFeedback = TextFeedback.create(Status.BAD_REQUEST.getStatusCode(),
		    			"User "+friend.getUsername()+" is not in your friends list");
				return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(textFeedback).build();
			}
		    if (!friends.contains(dbfriend)){
		    	TextFeedback textFeedback = TextFeedback.create(Status.BAD_REQUEST.getStatusCode(),
		    			"User "+friend.getUsername()+" is not in your friends list");
				return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(textFeedback).build();	
		    }
		    friends.remove(dbfriend);
			user.setFriends(friends);
			trackUserDAO.update(user);
			TextFeedback textFeedback = TextFeedback.create(Status.OK.getStatusCode(),
					"Friend successfully removed");
			return Response.status(Status.OK.getStatusCode()).entity(textFeedback).build();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		} catch(NoResultException e){
			TextFeedback textFeedback = TextFeedback.create(Status.BAD_REQUEST.getStatusCode(),
					"Friend "+friend.getUsername()+" does not exist");
			return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(textFeedback).build();			
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"Cannot add user,server error");
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(textFeedback).build();
		}		
	}
	
	@Path("/list/friends")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listFriends(@HeaderParam("jsonWebToken") String jsonWebToken) 
			throws JsonGenerationException, JsonMappingException, IOException {
		
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
			TrackUser trackUser = trackUserDAO.findFriendsByUsername(jwt.getUsername());
			Set<TrackUser> friendsSet = trackUser.getFriends();
			List<TrackUser> friends = new ArrayList<TrackUser>();
			if(friendsSet!=null){
				friends.addAll(friendsSet);
			}
			return Response.status(Status.OK).entity(JsonableList.fromList(friends)).build();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		} catch(NoResultException e){
			List<TrackUser> friends = new ArrayList<TrackUser>();
			return Response.status(Status.OK).entity(JsonableList.fromList(friends)).build();
		}
	}
}