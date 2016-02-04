/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月6日 下午12:45:07
 * @Description: 无
 */
package com.nl.util.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.nl.util.config.FTPCfg;

public class FTPManager {

	public static boolean connect(FTPClient ftp, String path, String addr,
			int port, String username, String password) throws IOException {
		boolean result = false;
		ftp = new FTPClient();
		int reply;

		ftp.connect(addr, port);
		ftp.login(username, password);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();

		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			return result;
		}
		ftp.changeWorkingDirectory(path);
		result = true;
		return result;
	}
	
	
	public static FTPClient getClient(FTPCfg cfg) throws IOException {
		FTPClient ftp = new FTPClient();
		int reply;
		ftp.connect(cfg.getHost(), 21);
		ftp.login(cfg.getUser(), cfg.getPassword());
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();

		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			return null;
		}
		ftp.changeWorkingDirectory(cfg.getHomePath());
		return ftp;
	}

}
