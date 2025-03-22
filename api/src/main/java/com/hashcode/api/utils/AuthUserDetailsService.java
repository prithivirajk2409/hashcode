package com.hashcode.api.utils;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hashcode.dao.UsersDAO;
import com.hashcode.entity.HCUsers;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.utils.Constants;

@Service
public class AuthUserDetailsService implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(AuthUserDetailsService.class);

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCUsers user = new UsersDAO(sqlSession).getUserByUserName(username);
			logger.info(user.toString());
			return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
					new ArrayList<>());
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return null;
	}

}