package com.hashcode.entity;

import com.hashcode.enums.ProgrammingLanguage;

public class CodeExecutionRequest {
	private int userId;
	private int problemId;
	private String submittedCode;
	private ProgrammingLanguage programmingLanguage;
	private long executionJobId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getProblemId() {
		return problemId;
	}

	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}

	public String getSubmittedCode() {
		return submittedCode;
	}

	public void setSubmittedCode(String submittedCode) {
		this.submittedCode = submittedCode;
	}

	public ProgrammingLanguage getProgrammingLanguage() {
		return programmingLanguage;
	}

	public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	public long getExecutionJobId() {
		return executionJobId;
	}

	public void setExecutionJobId(long executionJobId) {
		this.executionJobId = executionJobId;
	}

	@Override
	public String toString() {
		return "CodeExecutionRequest [userId=" + userId + ", problemId=" + problemId + ", submittedCode="
				+ submittedCode + ", programmingLanguage=" + programmingLanguage + ", executionJobId=" + executionJobId
				+ "]";
	}

}