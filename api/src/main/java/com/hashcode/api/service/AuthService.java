package com.hashcode.api.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hashcode.dao.UsersDAO;
import com.hashcode.entity.HCUsers;
import com.hashcode.entity.LoginRequest;
import com.hashcode.entity.SignUpRequest;
import com.hashcode.entity.ValidateTokenRequest;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.api.config.ThreadContextVariables;
import com.hashcode.api.response.ApiResponse;
import com.hashcode.utils.Constants;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.api.utils.AuthUserDetailsService;
import com.hashcode.api.utils.ResponseUtility;

@Service
public class AuthService {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthUserDetailsService authUserDetailsService;

	@Autowired
	private AuthenticationManager authManager;

	private static final Logger logger = LogManager.getLogger(AuthService.class);

	private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	public ResponseEntity<ApiResponse<Map<String, Object>>> processSignUpUserRequest(SignUpRequest body) {
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCUsers existingUser = new UsersDAO(sqlSession).getUserByUserName(body.getUserName());
			if (existingUser != null) {
				return ResponseUtility.errorResponse("User Name already exists");
			}
			existingUser = new UsersDAO(sqlSession).getUserByEmailId(body.getEmailId());

			if (existingUser != null) {
				return ResponseUtility.errorResponse("Email Id has been already registered");
			}

			createAndInsertUser(body, sqlSession);
			return ResponseUtility.successResponse(null, HttpStatus.OK, "Signup successful");
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return ResponseUtility.errorResponse("Something went wrong");
	}

	public ResponseEntity<ApiResponse<Map<String, Object>>> processLoginUserRequest(LoginRequest body) {
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			Map<String, Object> data = new HashMap<>();
			HCUsers existingUser = new UsersDAO(sqlSession).getUserByUserName(body.getUserName());
			if (existingUser == null) {
				return ResponseUtility.errorResponse("User Not Found");
			}
			Authentication authentification = authManager
					.authenticate(new UsernamePasswordAuthenticationToken(body.getUserName(), body.getPassword()));
			if (authentification.isAuthenticated()) {
				String token = jwtService.generateToken(body.getUserName());
				data.put("token", token);
				data.put("user", existingUser);
				return ResponseUtility.successResponse(data, "Login Successful");
			}
			return ResponseUtility.errorResponse("Wrong Password");
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return ResponseUtility.errorResponse("Invalid UserName or Password");
	}

	public ResponseEntity<ApiResponse<Map<String, Object>>> processRefreshTokenRequest(ValidateTokenRequest body) {
		Map<String, Object> data = new HashMap<>();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			String userNameFromToken = jwtService.getUsernameFromToken(body.getBearerToken());
			if (userNameFromToken != null) {
				UserDetails userDetails = authUserDetailsService.loadUserByUsername(userNameFromToken);
				if (userDetails != null && jwtService.validateToken(body.getBearerToken(), userDetails)) {
					HCUsers user = new UsersDAO(sqlSession).getUserByUserName(userDetails.getUsername());
					String newToken = jwtService.generateToken(userNameFromToken);
					data.put("refreshStatus", true);
					data.put("token", newToken);
					data.put("user", user);
					return ResponseUtility.successResponse(data);
				}
			}
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		data.put("refreshStatus", false);
		return ResponseUtility.successResponse(data);
	}

	public static void createAndInsertUser(SignUpRequest body, SqlSession sqlSession) {
		try {
			HCUsers user = new HCUsers();
			user.setUserName(body.getUserName());
			user.setEmailId(body.getEmailId());
			user.setPassword(passwordEncoder.encode(body.getPassword()));
			new UsersDAO(sqlSession).upsert(user);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}
}