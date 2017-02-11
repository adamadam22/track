package com.track.backend.model.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.track.backend.model.entities.TrackUser;

@Component
public class TrackUserDAO extends BaseDAO<TrackUser>{
	
	public TrackUser findUserByUsername(String username){
		return (TrackUser) em.createNamedQuery(TrackUser.NAMED_QUERY_FIND_USER_BY_USERNAME)
					.setParameter("username",username).getSingleResult();
	}
	
	public TrackUser findFriendsByUsername(String username){
		return (TrackUser) em.createNamedQuery(TrackUser.NAMED_QUERY_FIND_FRIENDS_BY_USERNAME)
					.setParameter("username",username).getSingleResult();
	}
}
