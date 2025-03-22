package com.hashcode.api.config;

import java.io.IOException;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hashcode.api.service.JwtService;
import com.hashcode.api.utils.AuthUserDetailsService;
import com.hashcode.dao.UsersDAO;
import com.hashcode.entity.HCUsers;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.utils.Constants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthUserDetailsService authUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			String token = request.getHeader("Authorization");
			if (token != null && token.startsWith("Bearer ")) {
				logger.info(token);
				String bearerToken = token.substring(7);
				String userNameFromToken = jwtService.getUsernameFromToken(bearerToken);
				if (userNameFromToken != null) {
					UserDetails userDetails = authUserDetailsService.loadUserByUsername(userNameFromToken);
					if (userDetails != null && jwtService.validateToken(bearerToken, userDetails)
							&& SecurityContextHolder.getContext().getAuthentication() == null) {
						// Assign user variable to context
						HCUsers user = new UsersDAO(sqlSession).getUserByUserName(userDetails.getUsername());
						ThreadContextVariables.setUser(user);

						logger.info("Secret Key :{}", jwtService.getSecretKey());
						UsernamePasswordAuthenticationToken authentification = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						logger.info("{}", authentification.isAuthenticated());
						SecurityContextHolder.getContext().setAuthentication(authentification);
					}
				}
			}
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}

		filterChain.doFilter(request, response);

	}

}