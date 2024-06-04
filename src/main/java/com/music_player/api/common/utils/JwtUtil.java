//$Id$
package com.music_player.api.common.utils;

import java.security.Key;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	
    private static final Logger LOGGER = Logger.getLogger(JwtUtil.class.getName());

	
	public static JwtUtil getInstance() {
		return JwtUtilInstance.INSTANCE;
	}
	
	private static class JwtUtilInstance {
		private static final JwtUtil INSTANCE = new JwtUtil();
	}

    // Secret key for signing the JWT
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Generate a JWT token
    public String generateToken(String username) {
    	try {
    		long now = System.currentTimeMillis();
    		return Jwts.builder()
    				.setSubject(username)
    				.setIssuedAt(new Date(now))
    				.setExpiration(new Date(now + 3600000)) // Token valid for 1 hour
    				.signWith(key)
    				.compact();	
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error generating token", e);
            throw e; 
		}
    }
}
