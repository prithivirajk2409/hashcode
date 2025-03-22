package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HCTestCases {

	@JsonProperty("problem_id")
	private int problemId;

	@JsonProperty("test_case")
	private String testCase;

	public int getProblemId() {
		return problemId;
	}

	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}

	@Override
	public String toString() {
		return "HCTestCases [problemId=" + problemId + ", testCase=" + testCase + "]";
	}

}