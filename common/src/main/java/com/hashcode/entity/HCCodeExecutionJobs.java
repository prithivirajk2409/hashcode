package com.hashcode.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashcode.enums.ExecutionType;

public class HCCodeExecutionJobs {

	@JsonProperty("execution_job_id")
	private long executionJobId;

	@JsonProperty("status")
	private int status;

	@JsonProperty("execution_type")
	private ExecutionType executionType;

	@JsonProperty("submission_id")
	private Long submissionId;

	@JsonProperty("metadata")
	private String metadata;

	@JsonProperty("created")
	private Timestamp created;

	@JsonProperty("updated")
	private Timestamp updated;

	public long getExecutionJobId() {
		return executionJobId;
	}

	public void setExecutionJobId(long executionJobId) {
		this.executionJobId = executionJobId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ExecutionType getExecutionType() {
		return executionType;
	}

	public void setExecutionType(ExecutionType executionType) {
		this.executionType = executionType;
	}

	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "HCCodeExecutionJobs [executionJobId=" + executionJobId + ", status=" + status + ", executionType="
				+ executionType + ", submissionId=" + submissionId + ", metadata=" + metadata + ", created=" + created
				+ ", updated=" + updated + "]";
	}

}
