package com.eastcom.tools.antideadblock.full.pool;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-16
 * Time: 上午10:32
 * To change this template use File | Settings | File Templates.
 */
import com.jcraft.jsch.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class SFTPDownload {
	private String host = "10.39.184.161";
	private String username="coreUser";
	private String password="Px96shm!";
	private int port = 22;
	private ChannelSftp sftp = null;

	private final Logger logger = Logger.getLogger(getClass());

	public boolean connect() {
		try {
			if(sftp != null && sftp.isConnected()){
				logger.info("sftp is connected.");
				return true;
			}
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			logger.info("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect(30*1000);
			logger.info("Session connected.");
			logger.info("Opening Channel.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			logger.info("Connected to " + host + ".");
		} catch (Exception e) {
			logger.error("connect failed,"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Disconnect with server
	 */
	public void disconnect() {
		if(this.sftp != null){
			if(this.sftp.isConnected()){
				this.sftp.disconnect();
				logger.info("sftp disconnect.");
			}else if(this.sftp.isClosed()){
				logger.info("sftp is closed already");
			}
		}

	}
	public void lcd(String localPath){
		try {
			sftp.lcd(localPath);
		} catch (SftpException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	public void cd(String remotePath){
		try {
			sftp.cd(remotePath);
		} catch (SftpException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	public List<String> lists(String remotePath){
		List<String> list = new LinkedList<String>();
		try {
			for(Object o:sftp.ls(remotePath)){
				ChannelSftp.LsEntry e= (ChannelSftp.LsEntry) o;
				list.add(e.getFilename());
			}
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return list;
	}
	public List<String> download(String directory,String localPath,List<String> files){
		List<String> list = new LinkedList<String>();
		for(String file:files){
			try {
				download(directory, file,localPath+""+file);
				list.add(file);
			} catch (Exception e) {
				logger.error("file "+file+" download failed.",e);
			}
		}
		return list;
	}
	private void download(String directory, String downloadFile,String saveFile) throws Exception {
		logger.info("download file,"+directory+downloadFile+" , save as "+saveFile);
		FileOutputStream fos=null;
		try {
			sftp.cd(directory);
			File file = new File(saveFile);
			fos = new FileOutputStream(file);
			sftp.get(downloadFile, fos);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if(fos!=null)
					fos.close();
			} catch (IOException e1) {

			}
			throw e;
		}
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the sftp
	 */
	public ChannelSftp getSftp() {
		return sftp;
	}

	/**
	 * @param sftp the sftp to set
	 */
	public void setSftp(ChannelSftp sftp) {
		this.sftp = sftp;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SFTPDownload ftp= new SFTPDownload();
		ftp.connect();
		try {
			ftp.download("/var/named9/.comm/","db.wxhuawei2_cmnet","D:/db.wxhuawei2_cmnet");
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		ftp.disconnect();
		System.exit(0);
	}


}

