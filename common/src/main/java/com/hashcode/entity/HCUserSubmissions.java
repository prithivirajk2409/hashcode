package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashcode.enums.AcceptanceStatus;
import com.hashcode.enums.ProgrammingLanguage;

public class HCUserSubmissions {

	@JsonProperty("submission_id")
	private long submissionId;

	@JsonProperty("problem_id")
	private int problemId;

	@JsonProperty("user_id")
	private int userId;

	@JsonProperty("programming_language")
	private ProgrammingLanguage programmingLanguage;

	@JsonProperty("acceptance_status")
	private AcceptanceStatus acceptanceStatus;

	@JsonProperty("submitted_code")
	private String submittedCode;

	@JsonProperty("execution_time")
	private Long executionTime;

	@JsonProperty("execution_memory")
	private Long executionMemory;

	@JsonProperty("execution_job_id")
	private long executionJobId;

	@JsonProperty("metadata")
	private String metadata;

	@JsonProperty("created")
	private String created;

	@JsonProperty("updated")
	private String updated;

	public long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(long submissionId) {
		this.submissionId = submissionId;
	}

	public int getProblemId() {
		return problemId;
	}

	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public ProgrammingLanguage getProgrammingLanguage() {
		return programmingLanguage;
	}

	public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	public AcceptanceStatus getAcceptanceStatus() {
		return acceptanceStatus;
	}

	public void setAcceptanceStatus(AcceptanceStatus acceptanceStatus) {
		this.acceptanceStatus = acceptanceStatus;
	}

	public String getSubmittedCode() {
		return submittedCode;
	}

	public void setSubmittedCode(String submittedCode) {
		this.submittedCode = submittedCode;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public Long getExecutionMemory() {
		return executionMemory;
	}

	public void setExecutionMemory(Long executionMemory) {
		this.executionMemory = executionMemory;
	}

	public long getExecutionJobId() {
		return executionJobId;
	}

	public void setExecutionJobId(long executionJobId) {
		this.executionJobId = executionJobId;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "HCUserSubmissions [submissionId=" + submissionId + ", problemId=" + problemId + ", userId=" + userId
				+ ", programmingLanguage=" + programmingLanguage + ", acceptanceStatus=" + acceptanceStatus
				+ ", submittedCode=" + submittedCode + ", executionTime=" + executionTime + ", executionMemory="
				+ executionMemory + ", executionJobId=" + executionJobId + ", metadata=" + metadata + ", created="
				+ created + ", updated=" + updated + "]";
	}

}