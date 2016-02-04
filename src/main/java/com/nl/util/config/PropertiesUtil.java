package com.nl.util.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil {
	static Logger log = Logger.getLogger(PropertiesUtil.class);
	private static Properties pro;
	private static FileInputStream inputFileStream;
	private static FileOutputStream outputFile;

	public static void readConfFile(String confFilePath) {
		try {
			pro = new Properties();
			// ��ȡ�����ļ�ftpconfig.properties�е����
			inputFileStream = new FileInputStream(confFilePath);
			// װ�������ļ�
			pro.load(inputFileStream);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			log.error("IOException");
			e.printStackTrace();
		} finally {
			try {
				if (inputFileStream != null)
					inputFileStream.close();
			} catch (IOException e) {
				log.error("IOException");
				e.printStackTrace();
			}
		}

	}

	public static String getValue(String key) {
		if (pro.containsKey(key)) {
			return pro.getProperty(key);
		} else {
			return "";
		}
	}

	public String getValue(String filePath, String key) {
		String value = "";
		try {
			inputFileStream = new FileInputStream(filePath);
			pro.load(inputFileStream);
			inputFileStream.close();
			if (pro.contains(key)) {
				value = pro.getProperty(key);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public void clear() {
		pro.clear();
	}

	public void setValue(String key, String value) {
		pro.setProperty(key, value);
	}

	public void saveFile(String fileName, String description) {
		try {
			outputFile = new FileOutputStream(fileName);
			pro.store(outputFile, description);
			outputFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
