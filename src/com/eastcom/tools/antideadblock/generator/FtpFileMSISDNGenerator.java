package com.eastcom.tools.antideadblock.generator;

import com.eastcom.tools.antideadblock.command.ExecuteCommand;
import com.eastcom.tools.antideadblock.transport.MSISDNSender;
import com.eastcom.tools.antideadblock.util.MSISDNUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-8-16
 * Time: 下午4:28
 * To change this template use File | Settings | File Templates.
 */
@Component("ftp_msisdnGenerator")
public class FtpFileMSISDNGenerator implements MSISDNGenerator {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String fileName;
	private MSISDNSender msisdnSender;
	private String downScript;
	private String localPath;
	private String remoteFile;

	@Autowired
	public FtpFileMSISDNGenerator(@Value("${downScript}") String downScript,
	                              @Value("${localPath}") String localPath,
	                              @Value("${remoteFile}") String remoteFile){
		this.downScript = downScript;
		this.localPath = localPath;
		this.remoteFile = remoteFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public MSISDNSender getMsisdnSender() {
		return msisdnSender;
	}

	public void setMsisdnSender(MSISDNSender msisdnSender) {
		this.msisdnSender = msisdnSender;
	}

	@Override
	//@PostConstruct
	//@Scheduled(cron = "${ftp_msisdn_generator.scheduling}")
	public void generate() {
		//TcpFailure_13081601.dat
		Calendar date = Calendar.getInstance(Locale.CHINA);
		date.add(Calendar.HOUR_OF_DAY, -1);
		//fileName="/var/ftp/test/TcpFailure_"+new SimpleDateFormat("yyMMddHH").format(date.getTime())+".dat";
		//fileName="d:/TcpFailure_13081909.dat.temp";
		fileName = down();
        if(fileName==null){
            logger.warn("can't download file");
            return;
        }
		logger.info("Start parse MSISDN from {} ...", fileName);
		InputStream in;
		try {
			File file = new File(fileName);
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(fileName + " not found.", e);
		}
		StopWatch sw = new StopWatch();
		sw.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		int lineNo = 0;
		try{
			int count=0;
            LinkedList<String> list = new LinkedList<String>();

			while ((line = reader.readLine()) != null){
				logger.info("Processing line {}: {}", lineNo++, line);
				String str[] = line.split("\\|");
				String id = MSISDNUtils.sanitizeToString(str[2]);
				logger.info("id"+id+" "+str[2]);
				if(!list.contains(id) && MSISDNUtils.isValid(id)){
					count++;
                    list.add(id);
					logger.info("line {}:{}",count,id);
					msisdnSender.send(id);
				}
			}
			logger.info("{} parsed complted, obtained {} MSISDN. ({} ms)", new Object[] {fileName, count, sw.getTime()});
		} catch (Exception e){
			logger.error("Parse MSISDN from " + fileName + " failed", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
		}
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}
	public String down(){
		String message=null;
		try {
			logger.info("downScript+\" \"+remoteFile+\" \"+localPath");
			message = ExecuteCommand.execute(downScript+" "+remoteFile+" "+localPath);
			logger.info("excute result:"+message);
			return localPath;
		} catch (Exception e) {
			logger.error("down failure:"+e.getMessage());
			return null;
		}
	}
	public static void main(String args[]){
		//new ftpFileMSISDNGenerator().generate();
		System.out.println(MSISDNUtils.isValid(MSISDNUtils.sanitizeToString("13400017294")));
	}
}
