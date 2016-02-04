/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月24日 下午4:29:41
 * @Description: 无
 */
package com.nl.event.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.nl.util.config.DBCfg;
import com.nl.util.db.DBAccess;

public class CheckLoadScenarios {

	public static void main(String[] args) {
		DBCfg dbCfg = new DBCfg();
		dbCfg.setDbName("bigdata");
		dbCfg.setDriver("com.gbase.jdbc.Driver");
		dbCfg.setUrl("jdbc:gbase://10.1.4.106:5258/bigdata");
		dbCfg.setUser("gbase");
		dbCfg.setPassword("123456");

		DBAccess dbAccess;
		Connection conn = null;
		String strSql;
		ResultSet rs1;
		ResultSet rs2;
		try {
			Class.forName(dbCfg.getDriver());
			conn = DriverManager.getConnection(dbCfg.getUrl(), dbCfg.getUser(),dbCfg.getPassword());
			dbAccess = new DBAccess(conn);
			strSql = "select a.sql from bigdata.cfg_cover_scenarios a order by a.COVER_SCENARIOS_ID limit 100";
			rs1 = dbAccess.openSelect(strSql);
			while (rs1.next()) {
				System.out.println(rs1.getString("sql").replaceAll("\\?", "'"));
				rs2 = dbAccess.openSelect(rs1.getString("sql").replaceAll("\\?", "'"));
				while (rs2.next()) {
					System.out.println(rs2.getString("CI_NAME"));
				}
				rs2.close();
			}
			rs1.close();
			dbAccess.closeSelect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
