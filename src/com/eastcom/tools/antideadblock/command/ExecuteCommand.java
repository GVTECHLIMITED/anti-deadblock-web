package com.eastcom.tools.antideadblock.command;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class ExecuteCommand {
	private static boolean isWindowsOs;

	static {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Win"))
			isWindowsOs = true;
		else
			isWindowsOs = false;
	}

	public static String execute(String command) throws Exception {
		if (command == null) {
			return "";
		}
		String[] execCommand = (String[]) null;
		if (isWindowsOs)
			execCommand = new String[] { "cmd", "/c", command };
		else
			execCommand = new String[] { "sh", "-c", command };
		try {
			Process p = Runtime.getRuntime().exec(execCommand);
			String output = IOUtils.toString(p.getInputStream());
			if (p.waitFor() != 0) {
				throw new Exception("Command '" + command
						+ "' execute failed,return value is " + p.exitValue());
			}
			return output;
		} catch (IOException e) {
			throw new Exception("IOException while executing '"+ command + "'.", e);
		} catch (InterruptedException e) {
			throw new Exception("Interrupted while executing '" + command+ "'.", e);
		}
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
		System.out.println(System.getProperty("os.version"));
	}
}
