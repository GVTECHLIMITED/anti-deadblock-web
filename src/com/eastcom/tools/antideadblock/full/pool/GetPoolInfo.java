package com.eastcom.tools.antideadblock.full.pool;

import com.eastcom.tools.antideadblock.dao.GGSNDao;
import com.eastcom.tools.antideadblock.dao.GGSNGroupDao;
import com.eastcom.tools.antideadblock.dao.data.GGSNGroup;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.ggsn.GGSNProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-29
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
@Component("getPoolInfo")
public class GetPoolInfo {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	GGSNProvider ggsnProvider;
	@Autowired
	GGSNGroupDao ggsnGroupDao;
	@Autowired
	GGSNDao ggsnDao;
	LinkedList<Host> hosts = new LinkedList<Host>();
	LinkedList<String> formats = new LinkedList<String>();
	Host host;
	String strHost;
	String user;
	String password;
	String separator;
	String remotePath;
	String localPath;
	String charset;
	public void init(){
		logger.info(user+" "+password);
		logger.info(charset+" "+separator+" "+remotePath+" "+localPath+" "+separator);
		try{
			String strHosts[] = strHost.split(separator);
			String strUser[] = user.split(separator);
			String strPassword[] = password.split(separator);
			for(int i=0;i<strHosts.length;i++){
				Host objectHost = new Host();
				objectHost.host = strHosts[i];
				objectHost.user = strUser[i];
				objectHost.password = strPassword[i];
				hosts.add(objectHost);
			}
		} catch (Exception e){
			logger.error("",e);
		}
		logger.info(hosts.toString());

		execute();
	}
	private LinkedList<String> getPoolFilesName(){
		LinkedList<String> names = new LinkedList<String>();
		List<GGSNGroup> ggsnGroups = ggsnGroupDao.findAll();
		for(GGSNGroup group:ggsnGroups){
			names.add(group.getId());
		}
		return names;
	}
	@Scheduled(cron = "${getPoolInfo.scheduling}")
	//@PostConstruct
	public void execute(){
		formats = getPoolFilesName();
		logger.info("formats = "+formats);
		List<String> files = getConfigFile();
		List<String> list = downloadFile(files);
		handFile(list);
	}
	public List<String> downloadFile(List<String> files){
		logger.info("start download file "+files);
		List<String> list = new LinkedList<String>();
		SFTPDownload sftpDownload = new SFTPDownload();
		sftpDownload.setHost(host.host);
		sftpDownload.setUsername(host.user);
		sftpDownload.setPassword(host.password);
		sftpDownload.setPort(host.port);
		if(sftpDownload.connect()){
			list = sftpDownload.download(remotePath,localPath,files);
			sftpDownload.disconnect();
		}
		return list;
	}
	public void handFile(List<String> files){
		for(String str:files){
			try {
				logger.info("start handle file "+str);
				handFile(str);
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}
	public void handFile(String file) throws Exception {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(localPath+file), Charset.forName(charset)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("read file error:" + localPath+file + "-" + e.getMessage());
			throw new Exception("read file error:" + localPath+file + "-" + e.getMessage());
		}
		String line = reader.readLine();
		while(line!=null){
			logger.info("handle line："+line);
			Pattern p=Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
			Matcher m=p.matcher(line);
			if(m.find()){
				String ip = m.group();
				logger.info("GTPCADDRESS:"+ip);
				setPoolNameForGGSN(ip,file);
			}
			line = reader.readLine();
		}
	}
	public void setPoolNameForGGSN(String gtpcIp,String poolId){
		GGSN ggsn  = ggsnProvider.findByGtpCAddress(gtpcIp);
		if(ggsn==null){
			logger.error("no ggsn gtpcaddress is "+gtpcIp);
			return;
		}
		GGSNGroup ggsnGroup = ggsnGroupDao.findById(poolId);
		ggsn.setPool(ggsnGroup.getName());
		ggsnDao.update(ggsn);
	}
	public List<String> getConfigFile(){
		List<String> pools = new LinkedList<String>();
		SFTPDownload sftpDownload = new SFTPDownload();
		for(Host host:hosts){
			sftpDownload.setHost(host.host);
			sftpDownload.setUsername(host.user);
			sftpDownload.setPassword(host.password);
			sftpDownload.setPort(host.port);
			if(sftpDownload.connect()){
				List<String> list = sftpDownload.lists(remotePath);
				for(String str:list){
					for(String format:formats){
						if(str.matches(format)){
							pools.add(str);
							ggsnGroupDao.contrast(str);
							break;
						}
					}
				}
				this.host = host;
				break;
			}
		}
		sftpDownload.disconnect();
		logger.info("pools:"+pools);
		return pools;
	}

	public LinkedList<Host> getHosts() {
		return hosts;
	}

	public void setHosts(LinkedList<Host> hosts) {
		this.hosts = hosts;
	}

	public LinkedList<String> getFormats() {
		return formats;
	}

	public void setFormats(LinkedList<String> formats) {
		this.formats = formats;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public String getStrHost() {
		return strHost;
	}

	public void setStrHost(String strHost) {
		this.strHost = strHost;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	class Host{
		String host;
		String user;
		String password;
		int port=22;
		public String toString(){
			return host+"|"+user+"|"+password;
		}
	}
}
