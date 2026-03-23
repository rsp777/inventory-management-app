package com.pawar.inventory.app.service.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Service for JWT token operations.
 * Centralizes all token parsing, validation, and user information extraction logic.
 * 
 * Supports both auth0/java-jwt library operations.
 */
@Service
public class TokenService extends AbstractBaseService {
	
	@Value("${jwt.secret}")
	private String jwtSecret;
	
	/**
	 * Decodes JWT token and extracts subject components
	 * 
	 * @param token JWT token string
	 * @return Array of decoded token components (username|info|roles|etc)
	 */
	public String[] decodeToken(String token) {
		if (token == null || token.trim().isEmpty()) {
			logWarning("Token is null or empty, skipping decode");
			return new String[0];
		}
		try {
			DecodedJWT decodedJWT = JWT.decode(token);
			String subject = decodedJWT.getSubject();
			
			if (subject == null || subject.isEmpty()) {
				logWarning("Token subject is empty");
				return new String[0];
			}
			
			String[] decodedToken = subject.split("\\|");
			logInfo("Token decoded successfully with " + decodedToken.length + " components");
			return decodedToken;
		} catch (Exception e) {
			logWarning("Failed to decode token: " + e.getMessage());
			return new String[0];
		}
	}
	
	/**
	 * Validates JWT token signature using secret
	 * 
	 * @param token JWT token string
	 * @return true if token is valid, false otherwise
	 */
	public boolean validateToken(String token) {
		try {
			if (jwtSecret == null || jwtSecret.isEmpty()) {
				logWarning("JWT secret not configured");
				return false;
			}
			
			Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
			JWTVerifier verifier = JWT.require(algorithm).build();
			verifier.verify(token);
			logInfo("Token validated successfully");
			return true;
		} catch (JWTVerificationException | IllegalArgumentException e) {
			logWarning("Token validation failed: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Extracts username from decoded token
	 * 
	 * @param jwtToken JWT token string
	 * @return username (first component of decoded token)
	 */
	public String getUserName(String jwtToken) {
		try {
			String[] decodedToken = decodeToken(jwtToken);
			if (decodedToken.length > 0) {
				String userName = decodedToken[0];
				logInfo("Extracted username: " + userName);
				return userName;
			}
			logWarning("No username found in token");
			return null;
		} catch (Exception e) {
			logWarning("Error extracting username: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Extracts user ID from decoded token
	 * 
	 * @param jwtToken JWT token string
	 * @return user ID (typically second component)
	 */
	public String getUserId(String jwtToken) {
		try {
			String[] decodedToken = decodeToken(jwtToken);
			if (decodedToken.length > 1) {
				String userId = decodedToken[1];
				logInfo("Extracted user ID: " + userId);
				return userId;
			}
			logWarning("No user ID found in token");
			return null;
		} catch (Exception e) {
			logWarning("Error extracting user ID: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Extracts roles string from decoded token
	 * 
	 * @param jwtToken JWT token string
	 * @return roles string (typically third component)
	 */
	public String getRolesString(String jwtToken) {
		try {
			String[] decodedToken = decodeToken(jwtToken);
			if (decodedToken.length > 2) {
				String roles = decodedToken[2];
				logInfo("Extracted roles string with length: " + roles.length());
				return roles;
			}
			logWarning("No roles found in token");
			return null;
		} catch (Exception e) {
			logWarning("Error extracting roles: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Gets token expiration time
	 * 
	 * @param jwtToken JWT token string
	 * @return expiration timestamp in milliseconds
	 */
	public long getTokenExpiration(String jwtToken) {
		try {
			DecodedJWT decodedJWT = JWT.decode(jwtToken);
			if (decodedJWT.getExpiresAt() != null) {
				long expiration = decodedJWT.getExpiresAt().getTime();
				logInfo("Token expiration: " + expiration);
				return expiration;
			}
			logWarning("Token has no expiration");
			return -1;
		} catch (Exception e) {
			logWarning("Error getting token expiration: " + e.getMessage());
			return -1;
		}
	}
	
	/**
	 * Checks if token is expired
	 * 
	 * @param jwtToken JWT token string
	 * @return true if token is expired, false otherwise
	 */
	public boolean isTokenExpired(String jwtToken) {
		try {
			long expiration = getTokenExpiration(jwtToken);
			if (expiration < 0) {
				return false; // No expiration set
			}
			boolean expired = System.currentTimeMillis() > expiration;
			logInfo("Token expired status: " + expired);
			return expired;
		} catch (Exception e) {
			logWarning("Error checking token expiration: " + e.getMessage());
			return true; // Assume expired if error
		}
	}
}
