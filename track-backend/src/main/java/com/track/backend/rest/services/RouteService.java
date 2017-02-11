package com.track.backend.rest.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.track.backend.utilities.JsonWebToken;
import com.track.backend.model.dao.RouteDAO;
import com.track.backend.model.dao.TrackUserDAO;
import com.track.backend.model.entities.Route;
import com.track.backend.utilities.JsonableList;
import com.track.backend.utilities.TextFeedback;

@Component
@Path("/RouteService")
public class RouteService {
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(RouteService.class);
	
	@Autowired
	private RouteDAO routeDAO;
	
	@Autowired
	private TrackUserDAO trackUserDAO;
	
	@Path("/add")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addRoute(@HeaderParam("jsonWebToken") String jsonWebToken, Route route) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		
		try{
			route.setTrackUser(trackUserDAO.findUserByUsername(jwt.getUsername()));
			routeDAO.add(route);
		} catch (Exception e) {
			if(e.getCause().getClass()==ConstraintViolationException.class){
				TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			    		  "Route name already exists");
				return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
			}
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during adding new route");
			return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
		TextFeedback textFeedback = TextFeedback.create(Status.OK.getStatusCode(),
	    		  "Route added successfully");
		return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
	}
	
	@Path("/remove")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeRoute(@HeaderParam("jsonWebToken") String jsonWebToken, Route route) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		
		try{
			Route attachedRoute = routeDAO.findById(route.getId());
			routeDAO.delete(attachedRoute);
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during removing new route");
			return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
		TextFeedback textFeedback = TextFeedback.create(Status.OK.getStatusCode(),
	    		  "Route removed successfully");
		return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
	}
	
	@Path("/list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRoutes(@HeaderParam("jsonWebToken") String jsonWebToken) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		List<Route> routes = null;
		try{
			routes=routeDAO.findByUsername(jwt.getUsername());
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during requesting for user's routes list");
		      return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
		//GenericEntity<List<Route>> genericEntity = new GenericEntity<List<Route>>(routes){};
		return Response.status(Status.OK).entity(JsonableList.fromList(routes)).build();
	}
	
	@Path("/get/route")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRouteByName(@HeaderParam("jsonWebToken") String jsonWebToken, @HeaderParam("routeName") String routeName) {
		JsonWebToken jwt = JsonWebToken.parse(jsonWebToken);
		try{
			jwt.validate();
		} catch (InvalidJwtException e) {
			TextFeedback textFeedback = TextFeedback.create(Status.FORBIDDEN.getStatusCode(),
					"Access denied for this functionality");
			return Response.status(Status.FORBIDDEN.getStatusCode()).entity(textFeedback).build();
		}
		Route route = null;
		try{
			route=routeDAO.findByUsernameAndName(jwt.getUsername(),routeName);
		} catch (Exception e) {
			TextFeedback textFeedback = TextFeedback.create(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    		  "Error during requesting for route");
		      return Response.status(textFeedback.getStatusCode()).entity(textFeedback).build();
		}
		return Response.status(Status.OK).entity(route).build();
	}
}