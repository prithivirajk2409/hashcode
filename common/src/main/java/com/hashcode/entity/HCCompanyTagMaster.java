package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HCCompanyTagMaster {

	@JsonProperty("company_id")
	private int companyId;

	@JsonProperty("company_name")
	private String companyName;

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}