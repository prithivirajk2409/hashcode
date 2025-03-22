package com.hashcode.api.service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hashcode.utils.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final Logger logger = LogManager.getLogger(JwtService.class);

	@Value("${jwt.secret.key}")
	private String secretKey;

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Value("${jwt.expiration}")
	private Long expiration;

	JwtService() {
//		generateSecretKey();
	}

//	public void generateSecretKey() {
//		try {
//			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//			SecretKey sk = keyGen.generateKey();
//			this.secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//			logger.info("Generated Secret Key : {}", secretKey);
//		} catch (Exception e) {
//			logger.warn(Constants.KEY_EXCEPTION, e);
//		}
//	}

	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder().claims().add(claims).subject(userName).issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration)).and().signWith(getSigningKey())
				.compact();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		try {
			Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
			if (claims.isEmpty()) {
				return false;
			}
			Date currentTime = new Date(System.currentTimeMillis());
			if (currentTime.after(claims.getExpiration())) {
				return false;
			}
			if (!claims.getSubject().equals(userDetails.getUsername())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return false;
	}

	public String getUsernameFromToken(String token) {
		try {
			Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
			return claims.getSubject();

		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return null;
	}

	public SecretKey getSigningKey() {
		byte[] byetArray = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(byetArray);
	}

}