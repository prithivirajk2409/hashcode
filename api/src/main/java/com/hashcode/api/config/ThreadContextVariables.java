package com.hashcode.api.config;

import com.hashcode.entity.HCUsers;

public class ThreadContextVariables {

	private static ThreadLocal<HCUsers> user = new ThreadLocal<>();

	public static HCUsers getUser() {
		return user.get();
	}

	public static void setUser(HCUsers user) {
		ThreadContextVariables.user.set(user);
	}

}