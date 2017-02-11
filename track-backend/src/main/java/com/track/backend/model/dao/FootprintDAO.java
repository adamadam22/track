package com.track.backend.model.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.track.backend.model.entities.Footprint;

@Component
public class FootprintDAO extends BaseDAO<Footprint>{
	
	@SuppressWarnings("unchecked")
	public List<Footprint> findByRouteId(int routeId){
		List<Footprint> footprints = (List<Footprint>) em.createNamedQuery(Footprint.NAMED_QUERY_FIND_FOOTPRINTS_BY_ROUTE_ID)
				.setParameter("routeId", routeId).getResultList();
		for (Footprint footprint: footprints){
			footprint.setRouteId(footprint.getRoute().getId());
		}
		return footprints;
	}
	
	@SuppressWarnings("unchecked")
	public List<Footprint> findByRouteIdAndUsername(int routeId, String username){
		List<Footprint> footprints = (List<Footprint>) em.createNamedQuery(Footprint.NAMED_QUERY_FIND_FOOTPRINTS_BY_ROUTE_ID_AND_USERNAME)
				.setParameter("routeId", routeId).setParameter("username", username).getResultList();
		for (Footprint footprint: footprints){
			footprint.setRouteId(footprint.getRoute().getId());
		}
		return footprints;
	}
	
	@SuppressWarnings("unchecked")
	public List<Footprint> findByTimestampAndUsername(Date timestamp, String username){
		List<Footprint> footprints = (List<Footprint>) em.createNamedQuery(Footprint.NAMED_QUERY_FIND_FOOTPRINTS_BY_TIMESTAMP_AND_USERNAME)
				.setParameter("timestamp", timestamp).setParameter("username", username).getResultList();
		for (Footprint footprint: footprints){
			footprint.setRouteId(footprint.getRoute().getId());
		}
		return footprints;
	}
	
	@Override
	public Footprint findById(Object id) {
		Footprint footprint = super.findById(id);
		footprint.setRouteId(footprint.getRoute().getId());
		return footprint;
	}
	
}
