package com.track.backend.model.entities;

import java.io.IOException;
import java.io.Serializable;
import java.lang.String;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.track.backend.utilities.JsonWebToken;
import com.track.backend.json.serializers.JsonIso8601Serializer;

/**
 * Entity implementation class for Entity: User
 *
 */

@Entity
@NamedQueries({
	@NamedQuery(name = Route.NAMED_QUERY_FIND_ROUTES_BY_USERNAME, query = "select r from Route r join r.trackUser u where u.username=:username"),
	@NamedQuery(name = Route.NAMED_QUERY_FIND_ROUTES_BY_USERNAME_AND_NAME, query = "select r from Route r join r.trackUser u where u.username=:username and r.name=:name")
})
@XmlRootElement
public class Route implements Serializable {
	
	public static final String NAMED_QUERY_FIND_ROUTES_BY_USERNAME = "findRoutesByUsername";
	public static final String NAMED_QUERY_FIND_ROUTES_BY_USERNAME_AND_NAME = "findRoutesByUsernameAndName";
	
	private static final Logger logger = Logger.getLogger(Route.class);
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date routeCreationTime;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.DETACH,CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH}, optional=false)
	@JoinColumn(name="trackuser_id")
	private TrackUser trackUser;
	
	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL, mappedBy="route")
	Set<Footprint> footprints;

	private static final long serialVersionUID = 1L;

	public Route() {
		super();
	}

	public int getId() {
		return id;
	}

	@XmlElement
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	@JsonSerialize(using=JsonIso8601Serializer.class)
	public java.util.Date getRouteCreationTime() {
		return routeCreationTime;
	}

	public void setRouteCreationTime(java.util.Date routeCreationTime) {
		this.routeCreationTime = routeCreationTime;
	}
	
	@XmlTransient
	public TrackUser getTrackUser() {
		return trackUser;
	}

	public void setTrackUser(TrackUser trackUser) {
		this.trackUser = trackUser;
	}

	@XmlTransient
	public Set<Footprint> getFootprints() {
		return footprints;
	}

	public void setFootprints(Set<Footprint> footprints) {
		this.footprints = footprints;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((routeCreationTime == null) ? 0 : routeCreationTime.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((trackUser == null) ? 0 : trackUser.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (routeCreationTime == null) {
			if (other.routeCreationTime != null)
				return false;
		} else if (!routeCreationTime.equals(other.routeCreationTime))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (trackUser == null) {
			if (other.trackUser != null)
				return false;
		} else if (!trackUser.equals(other.trackUser))
			return false;
		return true;
	}

	public static Route parse(String route){
		ObjectMapper mapper = new ObjectMapper();
		Route rt=null;
		try {
			rt = mapper.readValue(route,Route.class);
		} catch (IOException e) {
			logger.error("JSON to object parse error");
			e.printStackTrace();
		}
		return rt;
	}
	
	
}
