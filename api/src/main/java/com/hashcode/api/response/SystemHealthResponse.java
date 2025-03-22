package com.hashcode.api.response;

public class SystemHealthResponse {

	private boolean health;
	private boolean defaultDatabaseHealth;

	public boolean isHealth() {
		updateHealthy();
		return health;
	}

	public void updateHealthy() {
		if (this.defaultDatabaseHealth) {
			this.health = true;
		}
	}

	public void setHealth(boolean health) {
		this.health = health;
	}

	public boolean isDefaultDatabaseHealth() {
		return defaultDatabaseHealth;
	}

	public void setDefaultDatabaseHealth(boolean defaultDatabaseHealth) {
		this.defaultDatabaseHealth = defaultDatabaseHealth;
	}

	@Override
	public String toString() {
		return "\n----------------------------SystemHealthRespone------------------------\n"
				+ "SystemHealthResponse [health=" + health + ", defaultDatabaseHealth=" + defaultDatabaseHealth + "]"
				+ "\n----------------------------SystemHealthRespone------------------------\n";
	}

}