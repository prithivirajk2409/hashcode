package com.hashcode.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashcode.enums.DifficultyType;

public class HCProblems {

	@JsonProperty("problem_id")
	private int problemId;

	@JsonProperty("problem_name")
	private String problemName;

	@JsonProperty("slug")
	private String slug;

	@JsonProperty("content")
	private String content;

	@JsonProperty("difficulty")
	private DifficultyType difficulty;

	@JsonProperty("driver_code")
	private String driverCode;

	@JsonProperty("template_code")
	private String templateCode;

	@JsonProperty("sample_test_case")
	private String sampleTestCase;

	@JsonProperty("created")
	private Timestamp created;

	@JsonProperty("updated")
	private Timestamp updated;

	public int getProblemId() {
		return problemId;
	}

	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}

	public String getProblemName() {
		return problemName;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public DifficultyType getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(DifficultyType difficulty) {
		this.difficulty = difficulty;
	}

	public String getDriverCode() {
		return driverCode;
	}

	public void setDriverCode(String driverCode) {
		this.driverCode = driverCode;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getSampleTestCase() {
		return sampleTestCase;
	}

	public void setSampleTestCase(String sampleTestCase) {
		this.sampleTestCase = sampleTestCase;
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
		return "HCProblems [problemId=" + problemId + ", problemName=" + problemName + ", slug=" + slug + ", content="
				+ content + ", difficulty=" + difficulty + ", driverCode=" + driverCode + ", templateCode="
				+ templateCode + ", sampleTestCase=" + sampleTestCase + ", created=" + created + ", updated=" + updated
				+ "]";
	}

}