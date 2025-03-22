package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HCProblemCompanyTagMapper {

	@JsonProperty("problem_id")
	private int problemId;

	@JsonProperty("company_id")
	private int companyId;

	public int getProblemId() {
		return problemId;
	}

	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	@Override
	public String toString() {
		return "HCProblemCompanyTagMapper [problemId=" + problemId + ", companyId=" + companyId + "]";
	}

}