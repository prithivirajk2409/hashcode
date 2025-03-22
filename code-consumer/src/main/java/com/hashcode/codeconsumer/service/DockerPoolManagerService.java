package com.hashcode.codeconsumer.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.transport.DockerHttpClient;
import com.hashcode.enums.AcceptanceStatus;
import com.hashcode.enums.AppMode;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;

import jakarta.annotation.PostConstruct;

import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Service
public class DockerPoolManagerService {

	private static final Logger logger = LogManager.getLogger(DockerPoolManagerService.class);
	private DockerClient dockerClient;
	private int POOL_SIZE = 2;
	private Map<String, BlockingQueue<String>> containerPool;
	private Map<String, String> languageBaseImageMap;
	private Map<String, String> bashRunScriptMap;
	private Map<String, String> bashCompileScriptMap;
	private Map<String, String> codeFileNameMap;
	private Map<String, String> uniqueFolderContainerMap;

	@PostConstruct
	public void init() {
		try {
			initializeDockerClient();
			initializeLanguageBaseImageMap();
			initializeBashScriptMap();
			initializeContainerPool();
			initializeCodeFileNameMap();
		} catch (Exception e) {
			logger.error(e);
		}

	}

	private void initializeDockerClient() {
		DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost("unix:///var/run/docker.sock").build();
		DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder().dockerHost(config.getDockerHost()).build();
		dockerClient = DockerClientBuilder.getInstance(config).withDockerHttpClient(httpClient).build();
	}

	private void initializeLanguageBaseImageMap() {
		this.languageBaseImageMap = new HashMap<>();
		languageBaseImageMap.put("python", "python:3.9");
		languageBaseImageMap.put("java", "openjdk:17-slim");
		languageBaseImageMap.put("cpp", "gcc:latest");
	}

	private void initializeBashScriptMap() {
		this.bashRunScriptMap = new HashMap<>();
		this.bashCompileScriptMap = new HashMap<>();
		try {
			InputStream inputStream = CommonUtilities.class.getClassLoader().getResourceAsStream("bash/run-python.sh");
			bashRunScriptMap.put("python", new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

			inputStream = CommonUtilities.class.getClassLoader().getResourceAsStream("bash/run-java.sh");
			bashRunScriptMap.put("java", new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

			inputStream = CommonUtilities.class.getClassLoader().getResourceAsStream("bash/run-cpp.sh");
			bashRunScriptMap.put("cpp", new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

			inputStream = CommonUtilities.class.getClassLoader().getResourceAsStream("bash/compile-python.sh");
			bashCompileScriptMap.put("python", new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

			inputStream = CommonUtilities.class.getClassLoader().getResourceAsStream("bash/compile-java.sh");
			bashCompileScriptMap.put("java", new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

			inputStream = CommonUtilities.class.getClassLoader().getResourceAsStream("bash/compile-cpp.sh");
			bashCompileScriptMap.put("cpp", new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	private void initializeCodeFileNameMap() {
		this.codeFileNameMap = new HashMap<>();
		codeFileNameMap.put("python", "code.py");
		codeFileNameMap.put("java", "code.java");
		codeFileNameMap.put("cpp", "code.cpp");
	}

	private void initializeContainerPool() {
		this.containerPool = new ConcurrentHashMap<>();
		this.uniqueFolderContainerMap = new HashMap<>();
		for (String language : languageBaseImageMap.keySet()) {
			BlockingQueue<String> pool = new LinkedBlockingQueue<>(POOL_SIZE);
			for (int i = 0; i < POOL_SIZE; i++) {
				String containerId = createContainer(language);
				pool.offer(containerId);
			}
			containerPool.put(language, pool);
		}
	}

	private String createContainer(String language) {
		try {
			String baseImage = languageBaseImageMap.get(language);
			String runScript = bashRunScriptMap.get(language);
			String compileScript = bashCompileScriptMap.get(language);
			String folderUniqueId = UUID.randomUUID().toString();
			HostConfig hostConfig = HostConfig.newHostConfig()
					.withBinds(Bind.parse(Constants.WORKING_DIR_PATH + folderUniqueId + "/:/app/execution/info/:rw"));
			logger.info(folderUniqueId);
			CreateContainerResponse container = dockerClient.createContainerCmd(baseImage).withHostConfig(hostConfig)
					.withCmd("/bin/sh").withTty(true).exec();

			uniqueFolderContainerMap.put(container.getId(), folderUniqueId);
			dockerClient.startContainerCmd(container.getId()).exec();
			logger.info("Created Container  : {} for baseImage : {}", container.getId(), baseImage);

			dockerClient.copyArchiveToContainerCmd(container.getId())
					.withTarInputStream(createTarArchive(Map.of("run.sh", runScript, "compile.sh", compileScript)))
					.withRemotePath("/app/execution/info").exec();

			ExecCreateCmdResponse execCmd = dockerClient.execCreateCmd(container.getId())
					.withCmd("chmod", "+x", "/app/execution/info/run.sh", "/app/execution/info/compile.sh")
					.withAttachStderr(true).withAttachStdout(true).exec();
			executeDockerCommand(container.getId(), execCmd.getId());
			execCmd = dockerClient.execCreateCmd(container.getId()).withCmd("apt-get", "update").withAttachStderr(true)
					.withAttachStdout(true).exec();
			executeDockerCommand(container.getId(), execCmd.getId());
			execCmd = dockerClient.execCreateCmd(container.getId()).withCmd("apt-get", "install", "time")
					.withAttachStderr(true).withAttachStdout(true).exec();
			executeDockerCommand(container.getId(), execCmd.getId());

			logger.info("Loaded run and compile bash script into container");
			return container.getId();
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return null;

	}

	public String acquireContainer(String language) {
		String containerId = null;
		try {
			BlockingQueue<String> pool = containerPool.get(language);
			containerId = pool.take();
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return containerId;
	}

	public void releaseContainer(String language, String containerId) {
		BlockingQueue<String> pool = containerPool.get(language);
		pool.offer(containerId);
	}

	public Map<String, Object> executeCodeForTest(String language, String code, String testCaseString)
			throws Exception {
		Map<String, Object> data = new HashMap<>();
		String containerId = acquireContainer(language);
		try {
			dockerClient.copyArchiveToContainerCmd(containerId)
					.withTarInputStream(createTarArchive(Map.of(codeFileNameMap.get(language), code)))
					.withRemotePath("/app/execution/info").exec();

			ExecCreateCmdResponse execCmd = dockerClient.execCreateCmd(containerId)
					.withCmd("./app/execution/info/compile.sh").withAttachStderr(true).withAttachStdout(true).exec();

			executeDockerCommand(containerId, execCmd.getId());

			String compilationStatus = streamContentFromContainer(containerId, "AcceptanceStatus.txt");
			if (compilationStatus.equals(AcceptanceStatus.COMPILATION_ERROR.toString())) {
				String compilationOutput = streamContentFromContainer(containerId, "CompilationOutput.txt");
				data.put("acceptanceStatus", compilationStatus);
				data.put("compilationOutput", compilationOutput);
				return data;
			}
			List<Map<String, Object>> metadataList = new ArrayList<>();
			String cumulativeAcceptanceStatus = AcceptanceStatus.ACCEPTED.toString();
			JSONArray testCaseArray = new JSONArray(testCaseString);
			for (int i = 0; i < testCaseArray.length(); i++) {
				JSONObject testCase = testCaseArray.getJSONObject(i);
				Map<String, Object> testMetadata = new HashMap<>();

				dockerClient.copyArchiveToContainerCmd(containerId)
						.withTarInputStream(createTarArchive(Map.of("Input.txt", testCase.getString("input"),
								"Expected.txt", testCase.getString("expected"))))
						.withRemotePath("/app/execution/info").exec();

				ExecCreateCmdResponse execCmdRun = dockerClient.execCreateCmd(containerId)
						.withCmd("./app/execution/info/run.sh").withAttachStderr(true).withAttachStdout(true).exec();
				executeDockerCommand(containerId, execCmdRun.getId());

				String acceptanceStatus = streamContentFromContainer(containerId, "AcceptanceStatus.txt");
				String runtimeOutput = streamContentFromContainer(containerId, "RuntimeOutput.txt");

				if (!acceptanceStatus.equals(AcceptanceStatus.ACCEPTED.toString())
						&& !acceptanceStatus.equals(AcceptanceStatus.WRONG_ANSWER.toString())) {
					data.put("acceptanceStatus", acceptanceStatus);
					data.put("input", testCase.getString("input"));
					data.put("expected", testCase.getString("expected"));
					data.put("runtimeOutput", runtimeOutput);
					return data;
				}

				if (acceptanceStatus.equals(AcceptanceStatus.WRONG_ANSWER.toString())) {
					cumulativeAcceptanceStatus = AcceptanceStatus.WRONG_ANSWER.toString();
				}

				String metadata[] = streamContentFromContainer(containerId, "Metadata.txt").split("\n");
				String output = streamContentFromContainer(containerId, "Output.txt");

				long executionTime = Integer.parseInt(metadata[0]);
				long executionMemory = Integer.parseInt(metadata[1]);

				testMetadata.put("input", testCase.getString("input"));
				testMetadata.put("output", output);
				testMetadata.put("expected", testCase.getString("expected"));
				testMetadata.put("acceptanceStatus", AcceptanceStatus.valueOf(acceptanceStatus));
				testMetadata.put("executionTime", executionTime);
				testMetadata.put("executionMemory", executionMemory);
				metadataList.add(testMetadata);

			}
			data.put("acceptanceStatus", cumulativeAcceptanceStatus);
			data.put("metadata", metadataList);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
			throw e;
		} finally {
			logger.info("Container {} of language {} is released", containerId, language);
			releaseContainer(language, containerId);
		}
		return data;
	}

	public void executeDockerCommand(String containerId, String commandId) {
		try {
			dockerClient.execStartCmd(commandId).start().awaitCompletion();
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public String streamContentFromContainer(String containerId, String fileName) {
		String content = "";
		try {
			content = Files
					.lines(Paths.get(
							Constants.WORKING_DIR_PATH + uniqueFolderContainerMap.get(containerId) + "/" + fileName))
					.collect(Collectors.joining(System.lineSeparator()));
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return content;
	}

	public Map<String, Object> executeCodeForSubmit(String language, String code, String testCaseString)
			throws Exception {
		Map<String, Object> result = new HashMap<>();
		long maxExecutionTime = 0;
		long maxExecutionMemory = 0;
		String containerId = acquireContainer(language);
		try {
			dockerClient.copyArchiveToContainerCmd(containerId)
					.withTarInputStream(createTarArchive(Map.of(codeFileNameMap.get(language), code)))
					.withRemotePath("/app/execution/info").exec();
			ExecCreateCmdResponse execCmd = dockerClient.execCreateCmd(containerId)
					.withCmd("./app/execution/info/compile.sh").withAttachStderr(true).withAttachStdout(true).exec();
			executeDockerCommand(containerId, execCmd.getId());
			String compilationStatus = streamContentFromContainer(containerId, "AcceptanceStatus.txt");

			if (compilationStatus.equals(AcceptanceStatus.COMPILATION_ERROR.toString())) {
				String compilationOutput = streamContentFromContainer(containerId, "CompilationOutput.txt");
				result.put("acceptanceStatus", AcceptanceStatus.valueOf(compilationStatus));
				result.put("compilationOutput", compilationOutput);
				return result;
			}

			ExecCreateCmdResponse execCmdRun = null;

			JSONArray testCaseArray = new JSONArray(testCaseString);
			int processedTestCases = 0;
			long totalTime = 0;
			int totalTestCases = testCaseArray.length();
			for (int i = 0; i < testCaseArray.length(); i++) {
				JSONObject testCase = testCaseArray.getJSONObject(i);
				dockerClient.copyArchiveToContainerCmd(containerId)
						.withTarInputStream(createTarArchive(Map.of("Input.txt", testCase.getString("input"),
								"Expected.txt", testCase.getString("expected"))))
						.withRemotePath("/app/execution/info").exec();

				long start = System.currentTimeMillis();
//				Process execProcess = new ProcessBuilder("docker", "exec", containerId,
//						"./app/execution/info/run.sh").start();
//				execProcess.waitFor();
				execCmdRun = dockerClient.execCreateCmd(containerId).withCmd("./app/execution/info/run.sh")
						.withAttachStderr(true).withAttachStdout(true).exec();
				executeDockerCommand(containerId, execCmdRun.getId());

				long end = System.currentTimeMillis();
				totalTime += (end - start);
				logger.info("Time taken : {}", end - start);

				String acceptanceStatus = streamContentFromContainer(containerId, "AcceptanceStatus.txt");

				if (!AcceptanceStatus.valueOf(acceptanceStatus).equals(AcceptanceStatus.ACCEPTED)) {
					if (AcceptanceStatus.valueOf(acceptanceStatus).equals(AcceptanceStatus.WRONG_ANSWER)) {
						String output = streamContentFromContainer(containerId, "Output.txt");
						result.put("output", output);
					}
					String runtimeOutput = streamContentFromContainer(containerId, "RuntimeOutput.txt");
					result.put("input", testCase.getString("input"));
					result.put("expected", testCase.getString("expected"));
					result.put("acceptanceStatus", AcceptanceStatus.valueOf(acceptanceStatus));
					result.put("runtimeOutput", runtimeOutput);
					result.put("processedTestCases", processedTestCases);
					result.put("totalTestCases", totalTestCases);
					return result;
				}
				String metadata[] = streamContentFromContainer(containerId, "Metadata.txt").split("\n");
				long executionTime = Integer.parseInt(metadata[0]);
				long executionMemory = Integer.parseInt(metadata[1]);
				maxExecutionTime = Math.max(maxExecutionTime, executionTime);
				maxExecutionMemory = Math.max(maxExecutionMemory, executionMemory);
				processedTestCases++;

			}

			logger.info("Total Time Taken : {}", totalTime);
			result.put("executionTime", maxExecutionTime);
			result.put("executionMemory", maxExecutionMemory);
			result.put("acceptanceStatus", AcceptanceStatus.ACCEPTED);
			result.put("processedTestCases", processedTestCases);
			result.put("totalTestCases", totalTestCases);

		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
			throw e;
		} finally {
			logger.info("Container {} of language {} is released", containerId, language);
			releaseContainer(language, containerId);
		}
		return result;
	}

	public static ByteArrayInputStream createTarArchive(Map<String, String> files) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(byteArrayOutputStream);
			for (Map.Entry<String, String> file : files.entrySet()) {
				String content = file.getValue();
				String fileName = file.getKey();
				TarArchiveEntry entry = new TarArchiveEntry(fileName);
				byte[] byteCode = content.getBytes();
				entry.setSize(byteCode.length);
				tarOutputStream.putArchiveEntry(entry);
				tarOutputStream.write(byteCode);
				tarOutputStream.closeArchiveEntry();
			}

			tarOutputStream.finish();
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	}

//	public static ByteArrayInputStream createTarArchive(String code, String fileName) {
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		try {
//			TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(byteArrayOutputStream);
//			TarArchiveEntry entry = new TarArchiveEntry(fileName);
//			byte[] byteCode = code.getBytes();
//			entry.setSize(byteCode.length);
//			tarOutputStream.putArchiveEntry(entry);
//			tarOutputStream.write(byteCode);
//			tarOutputStream.closeArchiveEntry();
//			tarOutputStream.finish();
//		} catch (Exception e) {
//			logger.info(e);
//		}
//		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//	}

	public void shutdown() {
		for (BlockingQueue<String> pool : containerPool.values()) {
			for (String containerId : pool) {
				dockerClient.stopContainerCmd(containerId).exec();
				dockerClient.removeContainerCmd(containerId).withForce(true).exec();
			}
		}
	}

}