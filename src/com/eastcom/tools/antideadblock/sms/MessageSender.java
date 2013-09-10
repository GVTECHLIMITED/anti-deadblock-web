package com.eastcom.tools.antideadblock.sms;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-8-21
 * Time: 上午9:37
 * To change this template use File | Settings | File Templates.
 */
public class MessageSender {
    private String host="10.39.248.73";
    private int port=8089;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private void send(int recordId, String message, List<String> receivers) {
        Socket socket = null;
        OutputStream os = null;
        BufferedWriter bw = null;
        for (String phoneNum : receivers) {
            String data = phoneNum + "||" + message;
            logger.info("10.39.248.73" + ":" + 8089 + " -- " + data);
            try {
                socket = new Socket(host, port);
                os = socket.getOutputStream();
                bw = new BufferedWriter(new OutputStreamWriter(os));
                bw.write(data);
                bw.flush();
            } catch (UnknownHostException e) {
                logger.error(""+e);
            } catch (IOException e) {
                logger.error(""+ e);
            } finally {
                IOUtils.closeQuietly(os);
                IOUtils.closeQuietly(bw);
                if (socket!= null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
    public static void main(String args[]){
        List<String> list = new LinkedList<String>();
        list.add("13155599061");
        new MessageSender().send(1,"Hello",list);
    }
}
