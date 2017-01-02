package org.openmastery.ideaflow.intellij.handler;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.Key;
import org.openmastery.ideaflow.activity.ActivityHandler;
import org.openmastery.ideaflow.controller.IFMController;

import java.util.HashMap;
import java.util.Map;

public class ProcessExecutionHandler {

	private IFMController controller;
	private ActivityHandler activityHandler;
	private Map<ProcessHandler, ExitCodeListener> processDecodingMap = new HashMap<ProcessHandler, ExitCodeListener>();

	public ProcessExecutionHandler(IFMController controller) {
		this.controller = controller;
		this.activityHandler = controller.getActivityHandler();
	}

	public void processStarting(String executorId, ExecutionEnvironment env) {
		Long taskId = controller.getActiveTaskId();
		String processName = env.getRunProfile().getName();
		Long processId = env.getExecutionId();
		String executionTaskType = getExecutionTaskType(env);
		boolean isDebug = executorId.equals("Debug");

		activityHandler.markProcessStarting(taskId, processId, processName, executionTaskType, isDebug);
	}

	private String getExecutionTaskType(ExecutionEnvironment env) {
		RunnerAndConfigurationSettings runnerAndConfigurationSettings = env.getRunnerAndConfigurationSettings();
		if (runnerAndConfigurationSettings != null) {
			ConfigurationType configurationType = runnerAndConfigurationSettings.getType();
			if (configurationType != null) {
				return configurationType.getDisplayName();
			}
		}
		// TODO: should this return null?
		return "Unknown";
	}

	public void processStarted(ExecutionEnvironment env, ProcessHandler processHandler) {
		ExitCodeListener exitCodeListener = new ExitCodeListener(env.getExecutionId());
		processHandler.addProcessListener(exitCodeListener);
		processDecodingMap.put(processHandler, exitCodeListener);
	}

	public void processTerminated(ProcessHandler processHandler) {
		ExitCodeListener exitCodeListener = processDecodingMap.get(processHandler);
		if (exitCodeListener != null) {
			processHandler.removeProcessListener(exitCodeListener);
			activityHandler.markProcessEnding(exitCodeListener.processId, exitCodeListener.exitCode);
		} else {
			//TODO not supposed to happen, do some error handling stuff
		}
	}

	private static class ExitCodeListener implements ProcessListener {

		int exitCode;
		Long processId;

		ExitCodeListener(Long processId) {
			this.processId = processId;
		}

		@Override
		public void startNotified(ProcessEvent processEvent) {
		}

		@Override
		public void processTerminated(ProcessEvent processEvent) {
			exitCode = processEvent.getExitCode();
		}

		@Override
		public void processWillTerminate(ProcessEvent processEvent, boolean b) {
		}

		@Override
		public void onTextAvailable(ProcessEvent processEvent, Key key) {
		}
	}

}
