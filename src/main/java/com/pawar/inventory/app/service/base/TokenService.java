package com.pawar.inventory.app.service.base;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for JWT token operations.
 * Centralizes all token parsing, validation, and user information extraction
 * logic.
 * 
 * Supports both auth0/java-jwt library operations.
 */
@Service
public class TokenService extends AbstractBaseService {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Value("${jwt.secret}")
	private String jwtSecret;

	/**
	 * Decodes a token and extracts pipe-separated subject components.
	 * Handles two formats:
	 * <ul>
	 * <li>JWT format (header.payload.signature): extracts the subject claim and
	 * splits it by {@code |}</li>
	 * <li>Plain pipe-separated string (username|userId|roles): splits directly,
	 * which is what the auth service returns after sign-in</li>
	 * </ul>
	 *
	 * @param token raw token value from the session
	 * @return array of decoded components, or an empty array on failure
	 */
	public String[] decodeToken(String token) {
		if (token == null || token.trim().isEmpty()) {
			logWarning("Token is null or empty, skipping decode");
			return new String[0];
		}

		String trimmed = normalizeTokenValue(token.trim());

		if (isJwtFormat(trimmed)) {
			try {
				DecodedJWT decodedJWT = JWT.decode(trimmed);
				String subject = decodedJWT.getSubject();
				if (subject == null || subject.isEmpty()) {
					logWarning("JWT subject is empty");
					return new String[0];
				}
				String[] components = subject.split("\\|");
				logInfo("JWT decoded successfully with " + components.length + " components");
				return components;
			} catch (Exception e) {
				logWarning("JWT decode failed, falling back to plain split: " + e.getMessage());
			}
		}

		// Plain pipe-separated string returned by the auth service (user|userId|roles)
		String[] components = trimmed.split("\\|");
		if (components.length > 0 && !components[0].isEmpty()) {
			logInfo("Token decoded as plain string with " + components.length + " components");
			return components;
		}

		logWarning("Token could not be decoded in any format");
		return new String[0];
	}

	/**
	 * Returns {@code true} when the token string looks like a JWT
	 * (three Base64URL parts separated by two dots).
	 */
	private boolean isJwtFormat(String token) {
		int firstDot = token.indexOf('.');
		if (firstDot < 0)
			return false;
		int secondDot = token.indexOf('.', firstDot + 1);
		return secondDot > firstDot + 1;
	}

	/**
	 * Normalizes token value for decoding.
	 * Supports both raw token strings and JSON wrapper payloads like
	 * {"token":"..."} or {"access_token":"..."}.
	 */
	private String normalizeTokenValue(String token) {
		if (token == null || token.isBlank()) {
			return token;
		}

		if (!token.startsWith("{")) {
			return token;
		}

		try {
			Map<?, ?> parsed = OBJECT_MAPPER.readValue(token, Map.class);
			Object wrappedToken = parsed.get("token");
			if (wrappedToken == null) {
				wrappedToken = parsed.get("access_token");
			}
			if (wrappedToken != null) {
				return wrappedToken.toString().trim();
			}
		} catch (JsonProcessingException e) {
			logWarning("Token JSON normalization failed, using raw value: " + e.getMessage());
		}

		return token;
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

			Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
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
