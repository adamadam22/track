package com.track.backend.model.entities;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.track.backend.json.serializers.JsonIso8601Serializer;

/**
 * Entity implementation class for Entity: User
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = Footprint.NAMED_QUERY_FIND_FOOTPRINTS_BY_ROUTE_ID, query = 
			"select f from Footprint f join f.route r where r.id=:routeId order by r.routeCreationTime desc"),
	@NamedQuery(name = Footprint.NAMED_QUERY_FIND_FOOTPRINTS_BY_ROUTE_ID_AND_USERNAME, query = 
			"select f from Footprint f join f.route r join r.trackUser u where r.id=:routeId and u.username=:username order by f.footprintCreationTime asc"),
	@NamedQuery(name = Footprint.NAMED_QUERY_FIND_FOOTPRINTS_BY_TIMESTAMP_AND_USERNAME, query = 
			"select f from Footprint f join f.route r join r.trackUser u where f.footprintCreationTime>=:timestamp and u.username=:username order by f.footprintCreationTime asc")
})	
@XmlRootElement
public class Footprint implements Serializable {

	public static final String NAMED_QUERY_FIND_FOOTPRINTS_BY_ROUTE_ID = "findFootprintsByRouteId";
	public static final String NAMED_QUERY_FIND_FOOTPRINTS_BY_ROUTE_ID_AND_USERNAME = "findFootprintsByRouteIdAndUsername";
	public static final String NAMED_QUERY_FIND_FOOTPRINTS_BY_TIMESTAMP_AND_USERNAME = "findFootprintsByTimestampAndUsername";

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private double latitude;
	private double longitude;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date footprintCreationTime;
	
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, optional=false)
	@JoinColumn(name="route_id")
	private Route route;
	@Transient
	int routeId;

	private static final long serialVersionUID = 1L;

	public Footprint() {
		super();
	}

	public int getId() {
		return id;
	}

	@XmlElement
	public void setId(int id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	@XmlElement
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@XmlElement
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@JsonSerialize(using=JsonIso8601Serializer.class)
	public java.util.Date getFootprintCreationTime() {
		return footprintCreationTime;
	}

	public void setFootprintCreationTime(java.util.Date footprintCreationTime) {
		this.footprintCreationTime = footprintCreationTime;
	}

	@XmlTransient
	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
	
	public int getRouteId() {
		return routeId;
	}
	
	@XmlElement
	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((footprintCreationTime == null) ? 0 : footprintCreationTime.hashCode());
		result = prime * result + id;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Footprint other = (Footprint) obj;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (footprintCreationTime == null) {
			if (other.footprintCreationTime != null)
				return false;
		} else if (!footprintCreationTime.equals(other.footprintCreationTime))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		return true;
	}

	
}
