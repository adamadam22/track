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

/**
 * Entity implementation class for Entity: User
 *
 */
@XmlRootElement
@Entity
@NamedQueries({
	@NamedQuery(name = TrackUser.NAMED_QUERY_FIND_USER_BY_USERNAME, query = "select u from TrackUser u where u.username=:username"),
	@NamedQuery(name = TrackUser.NAMED_QUERY_FIND_FRIENDS_BY_USERNAME, query = "select u from TrackUser u JOIN FETCH u.friends where u.username=:username")
})
public class TrackUser implements Serializable {
	
	public static final String NAMED_QUERY_FIND_USER_BY_USERNAME = "findUserByUsername";
	public static final String NAMED_QUERY_FIND_FRIENDS_BY_USERNAME = "findFriendsByUsername";

	private static final Logger logger = Logger.getLogger(TrackUser.class);
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	private String surname;
	
	@Column(unique=true,nullable=false)
	private String username;
	@Column(nullable=false)
	private String password;
	
	@Transient
	private String repeatPassword;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date accountCreationTime;
	
	private String email;
	private String phone;
	
	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL, mappedBy="trackUser")
	Set<Route> routes;
	
	@ManyToMany
	@JoinTable (
				name="user_friend",
				joinColumns={ @JoinColumn(name="user_id", referencedColumnName="id") },
				inverseJoinColumns = { @JoinColumn(referencedColumnName = "id", name = "friend_id" ) }
				)
	Set<TrackUser> friends;
		
	private static final long serialVersionUID = 1L;

	public TrackUser() {
		super();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String getRepeatPassword() {
		return repeatPassword;
	}

	public java.util.Date getAccountCreationTime() {
		return accountCreationTime;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}
	
	@XmlTransient
	public Set<Route> getRoutes() {
		return routes;
	}
	
	@XmlTransient
	public Set<TrackUser> getFriends() {
		return friends;
	}
	
	@XmlElement
	public void setId(int id) {
		this.id = id;
	}
	
	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public void setSurname(String surname) {
		this.surname = surname;
	}

	@XmlElement
	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement
	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlElement
	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	@XmlElement
	public void setAccountCreationTime(java.util.Date accountCreationTime) {
		this.accountCreationTime = accountCreationTime;
	}

	@XmlElement
	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setRoutes(Set<Route> routes) {
		this.routes = routes;
	}

	public void setFriends(Set<TrackUser> friends) {
		this.friends = friends;
	}
	
	public static TrackUser parse(String trackUser){
		ObjectMapper mapper = new ObjectMapper();
		TrackUser tu=null;
		try {
			tu = mapper.readValue(trackUser,TrackUser.class);
		} catch (IOException e) {
			logger.error("JSON to object parse error");
			e.printStackTrace();
		}
		return tu;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountCreationTime == null) ? 0 : accountCreationTime.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((repeatPassword == null) ? 0 : repeatPassword.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		TrackUser other = (TrackUser) obj;
		if (accountCreationTime == null) {
			if (other.accountCreationTime != null)
				return false;
		} else if (!accountCreationTime.equals(other.accountCreationTime))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (repeatPassword == null) {
			if (other.repeatPassword != null)
				return false;
		} else if (!repeatPassword.equals(other.repeatPassword))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
}
