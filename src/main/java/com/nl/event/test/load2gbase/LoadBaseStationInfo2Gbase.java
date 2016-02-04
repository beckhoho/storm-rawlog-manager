/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月22日 下午2:33:32
 * @Description: 无
 */
package com.nl.event.test.load2gbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import com.nl.event.conf.ConfManager;
import com.nl.util.config.DBCfg;
import com.nl.util.db.DBAccess;

public class LoadBaseStationInfo2Gbase {

	public static void main(String[] args) throws FileNotFoundException {
		String strFile = args.length > 0 ? args[0] :"E:\\Eclipse\\workspace\\XJ_atom_events\\doc\\4G基站配置信息.txt";
		final String tablename=args.length > 1 ? args[1] :"cfg_base_station_info";
		DBAccess dbAccess;
		DBCfg dbCfg = new DBCfg();
		dbCfg.setDbName(args.length > 2 ? args[2] :"bigdata");
		dbCfg.setDriver("com.gbase.jdbc.Driver");
		//dbCfg.setUrl("jdbc:gbase://10.1.4.106:5258/bigdata");
		dbCfg.setUrl(args.length > 3 ? args[3] :"jdbc:gbase://10.238.156.1:5258/bigdata");
		dbCfg.setUser(args.length > 4 ? args[4] :"gbase");
		//dbCfg.setPassword("123456");
		dbCfg.setPassword(args.length > 5 ? args[5] :"gbase20110531");
		ConfManager.getInstance().setDb(dbCfg);
		
		
		long startTime = System.currentTimeMillis();
		FileInputStream in;
		BufferedReader reader;
		
		//OutputStream outputStream=new FileOutputStream(new File("E:\\Eclipse\\workspace\\XJ_atom_events\\doc\\insert4G"), true);
		String line;
		long count = 0;
		Connection conn = null;
		try {
			in = new FileInputStream(new File(strFile));
			reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			Class.forName(dbCfg.getDriver());
			conn = DriverManager.getConnection(dbCfg.getUrl(),dbCfg.getUser(), dbCfg.getPassword());
			dbAccess = new DBAccess(conn);
			
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				String [] fields=line.split(",");
				if (fields.length!=14) continue;
				StringBuffer sql=new StringBuffer();
				
				sql.append("insert into ");
				sql.append(tablename);
				sql.append(" values(");
				for (int i=0;i<fields.length;i++){
					sql.append("'");
					sql.append(fields[i]);
					sql.append("'");
					if (i!=fields.length-1) sql.append(",");
					if (i==fields.length-1) sql.append(");\n");
				}
				//System.out.println(sql.toString());
				//writeLocal(sql.toString(), outputStream);
				dbAccess.runSql(sql.toString());
				count++;
				if (count % 100 == 0) {
					System.out.println(count + " cost " + (System.currentTimeMillis() - startTime));
				}
			}
			reader.close();
			in.close();
			//outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	
	static void writeLocal(String outStr, OutputStream outputStream) {
		byte[] buff = outStr.getBytes();
		try {
			outputStream.write(buff, 0, buff.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
