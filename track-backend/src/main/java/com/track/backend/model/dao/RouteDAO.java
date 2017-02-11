package com.track.backend.model.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.track.backend.model.entities.Route;

@Component
public class RouteDAO extends BaseDAO<Route>{
	
	@SuppressWarnings("unchecked")
	public List<Route> findByUsername(String username){
		return (List<Route>) em.createNamedQuery(Route.NAMED_QUERY_FIND_ROUTES_BY_USERNAME)
					.setParameter("username", username).getResultList();
	}
	
	public Route findByUsernameAndName(String username, String name){
		return (Route) em.createNamedQuery(Route.NAMED_QUERY_FIND_ROUTES_BY_USERNAME_AND_NAME)
					.setParameter("username", username).setParameter("name", name).getSingleResult();
	}
	
}
