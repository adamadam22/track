package com.track.backend.services;

import org.springframework.stereotype.Component;

import com.track.backend.utilities.JsonWebToken;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

@Component
public class JsonWebTokenService {
	
	private static RsaJsonWebKey rsaJsonWebKey;
	static{
		try {
			rsaJsonWebKey=RsaJwkGenerator.generateJwk(2048);
		} catch (JoseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rsaJsonWebKey.setKeyId("rsaJsonWebKey");
	}

	
	
	public static RsaJsonWebKey getRsaJsonWebKey() {
		return rsaJsonWebKey;
	}

	public JsonWebToken generateFor(String username){
		
		// Create the Claims, which will be the content of the JWT
	    JwtClaims claims = new JwtClaims();
	    claims.setIssuer("track.com");  // who creates the token and signs it
	    claims.setAudience(username); // to whom the token is intended to be sent
	    claims.setExpirationTimeMinutesInTheFuture(360000); // time when the token will expire (10 minutes from now)
	    claims.setGeneratedJwtId(); // a unique identifier for the token
	    claims.setIssuedAtToNow();  // when the token was issued/created (now)
	    //claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
	    claims.setSubject("authorization"); // the subject/principal is whom the token is about
	    //claims.setClaim("email","mail@example.com"); // additional claims/attributes about the subject can be added
	    //List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
	    //claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array
	 
	    
	    JsonWebSignature jws = new JsonWebSignature();
	    
	    // The payload of the JWS is JSON content of the JWT Claims
	    jws.setPayload(claims.toJson());
	    
	    // The JWT is signed using the private key
	    jws.setKey(rsaJsonWebKey.getPrivateKey());
	    
	    // Set the Key ID (kid) header because it's just the polite thing to do.
	    // We only have one key in this example but a using a Key ID helps
	    // facilitate a smooth key rollover process
	    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
	    
	    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
	    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
	    JsonWebToken jwt = new JsonWebToken();
	    try {
	    	jwt.setToken(jws.getCompactSerialization());
	    	jwt.setUsername(username);
		} catch (JoseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return  jwt;
	    
	}
}
