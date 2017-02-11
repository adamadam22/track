package com.track.backend.utilities;

import java.io.IOException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import com.track.backend.services.JsonWebTokenService;

@XmlRootElement
public class JsonWebToken {
	
	private static final Logger logger = Logger.getLogger(JsonWebToken.class);
	
	private String token;
	private String username;

	public String getToken() {
		return token;
	}
	
	@XmlElement
	public void setToken(String token) {
		this.token = token;
	}
	
	
	
	public String getUsername() {
		return username;
	}

	@XmlElement
	public void setUsername(String username) {
		this.username = username;
	}

	public void validate() throws InvalidJwtException{
		  JsonWebKey jwk = JsonWebTokenService.getRsaJsonWebKey();
		  // Validate Token's authenticity and check claims
		  JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime()
				  .setAllowedClockSkewInSeconds(30).setRequireSubject().setExpectedAudience(username)
				  .setExpectedIssuer("track.com")
				  .setVerificationKey(jwk.getKey()).build();
		    try {
		        //  Validate the JWT and process it to the Claims
		        JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
		        logger.info("JWT validation succeeded! " + jwtClaims + " username:"+ username);
		      } catch (InvalidJwtException e) {
		        logger.error("JWT is Invalid: " + e + " username:"+ username);
		        throw new InvalidJwtException("JWT is Invalid\n" + e);
		      }
	  }
	
	public static JsonWebToken parse(String jsonWebToken){
		ObjectMapper mapper = new ObjectMapper();
		JsonWebToken jwt=null;
		try {
			jwt = mapper.readValue(jsonWebToken,JsonWebToken.class);
		} catch (IOException e) {
			logger.error("JSON to object parse error");
			e.printStackTrace();
		}
		return jwt;
	}
	


}
