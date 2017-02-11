package com.track.backend.rest.services;

import java.util.Date;
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
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.track.backend.model.dao.RouteDAO;
import com.track.backend.model.dao.TrackUserDAO;
import com.track.backend.utilities.JsonWebToken;
import com.track.backend.model.dao.FootprintDAO;
import com.track.backend.model.entities.Route;
import com.track.backend.model.entities.TrackUser;
import com.track.backend.model.entities.Footprint;
import com.track.backend.utilities.DateParser;
import com.track.backend.utilities.JsonableList;
import com.track.backend.utilities.TextFeedback;

@Component
@Path("/FootprintService")
public class FootprintService {
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(FootprintService.class);
	
	@Autowired
	private RouteDAO routeDAO;
		
	@Autowired
	private FootprintDAO footprintDAO;
	
	@Autowired
	private TrackUserDAO trackUserDAO;
	
	@Path("/add")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addFootprint(@HeaderParam("jsonWebToken") String jsonWebToken, Footprint footprint) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		
		try{
			footprint.setRoute(routeDAO.findById(footprint.getRouteId()));
			footprintDAO.add(footprint);
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during adding new footprint");
		      return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
		TextFeedback textFeedback = TextFeedback.create(Status.CREATED.getStatusCode(),
	    		  "Footprint added successfully");
		return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
	}
	
	@Path("/list/route")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRouteFootpints(@HeaderParam("jsonWebToken") String jsonWebToken,
			@HeaderParam("route") String route) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		Route rt= Route.parse(route);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		
		List<Footprint> footprints = null;
		try{			
			footprints = footprintDAO.findByRouteIdAndUsername(rt.getId(), jwt.getUsername());
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during requesting for route's footprint list");
		      return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
		return Response.status(Status.OK).entity(JsonableList.fromList(footprints)).build();
	}

	@Path("/list/current")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listCurrentFootpints(@HeaderParam("jsonWebToken") String jsonWebToken,
			@HeaderParam("timestamp") String timestamp) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		Date timestampJavaUtilDate = DateParser.parseISO8601(timestamp);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		
		List<Footprint> footprints = null;
		try{			
			footprints = footprintDAO.findByTimestampAndUsername(timestampJavaUtilDate, jwt.getUsername());
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during requesting for current footprint list");
		      return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
		return Response.status(Status.OK).entity(JsonableList.fromList(footprints)).build();
	}
	
	@Path("/list/friend")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listFriendFootpints(@HeaderParam("jsonWebToken") String jsonWebToken,
			@HeaderParam("friend") String friendString, @HeaderParam("timestamp") String timestamp) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		TrackUser friend = TrackUser.parse(friendString);
		Date timestampJavaUtilDate = DateParser.parseISO8601(timestamp);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		
		List<Footprint> footprints = null;
		Set<TrackUser> friends = null;
		TrackUser user = null;
		try{			
			footprints = footprintDAO.findByTimestampAndUsername(timestampJavaUtilDate, friend.getUsername());
			user = trackUserDAO.findFriendsByUsername(friend.getUsername());
			friends = user.getFriends();
			for(TrackUser tu:friends){
				if(tu.getUsername().equals(jwt.getUsername())){
					return Response.status(Status.OK).entity(JsonableList.fromList(footprints)).build();
				}
			}
			throw new NoResultException();			
		} 
		catch (NoResultException e){
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"You are not in friends list of "+friend.getUsername());
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during requesting for current footprint list");
		      return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
	}
}