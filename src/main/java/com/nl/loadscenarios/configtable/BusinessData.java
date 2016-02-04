/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月13日 下午2:28:17
 * @Description: 无
 */
package com.nl.loadscenarios.configtable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.loadscenarios.configtable.bean.Scenarios;
import com.nl.util.config.DBCfg;
import com.nl.util.db.DBAccess;

public class BusinessData {
	private static final Logger LOG = LoggerFactory
			.getLogger(BusinessData.class);
	private DBCfg db;
	private final static BusinessData INSTANCE = new BusinessData();

	public static BusinessData getInstance() {
		return INSTANCE;
	}

	public DBCfg getDb() {
		return db;
	}

	public void setDb(DBCfg db) {
		this.db = db;
	}

	private List<Scenarios> scenariosList;//

	public List<Scenarios> getScenariosList() {
		return scenariosList;
	}

	public void setScenariosList(List<Scenarios> scenariosList) {
		this.scenariosList = scenariosList;
	}

	public void loadScenarios() {
		DBAccess dbAccess;
		Connection conn = null;
		String strSql;
		ResultSet rs;
		try {
			Class.forName(this.db.getDriver());
			conn = DriverManager.getConnection(this.db.getUrl(),
					this.db.getUser(), this.db.getPassword());
			dbAccess = new DBAccess(conn);
			// 1
			strSql = "select distinct b.COVER_SCENARIOS_ID,b.COVER_SCENARIOS,b.REDIS_TABLENAME,b.SQL from bigdata.cfg_cover_scenarios b where b.IN_USE='1' and b.IS_CONFIGURED='1' order by b.COVER_SCENARIOS_ID";
			System.out.println("----scenariosList sql:" + strSql);
			rs = dbAccess.openSelect(strSql);
			scenariosList = new ArrayList<>();
			while (rs.next()) {
				System.out.println("runsql:" + rs.getString("COVER_SCENARIOS"));
				Scenarios scenarios = new Scenarios();
				scenarios.setCoverScenariosId(rs.getString("COVER_SCENARIOS_ID"));
				scenarios.setCoverSenarios(rs.getString("COVER_SCENARIOS"));
				scenarios.setRedisTablename(rs.getString("REDIS_TABLENAME"));
				scenarios.setSql(rs.getString("SQL").replaceAll("\\?", "'"));
				scenariosList.add(scenarios);
			}
			rs.close();
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
	
	
	public ConcurrentHashMap<String,String > loadLacciByScenarios(String scenarios ) {
		ConcurrentHashMap<String,String > lacci=new ConcurrentHashMap<String,String >();
		DBAccess dbAccess;
		Connection conn = null;
		String strSql;
		ResultSet rs;
		try {
			Class.forName(this.db.getDriver());
			conn = DriverManager.getConnection(this.db.getUrl(),
					this.db.getUser(), this.db.getPassword());
			dbAccess = new DBAccess(conn);
			strSql ="select distinct LAC,CI,CI_NAME from bigdata.cfg_base_station_info where STATUS='现网' and COVER_SCENARIOS='"+scenarios+"'";
			System.out.println("----lacci sql:" + strSql);
			rs = dbAccess.openSelect(strSql);
			
			while (rs.next()) {
				//System.out.println("runsql:" + rs.getString("LAC")+rs.getString("CI")+":"+rs.getString("CI_NAME"));
				lacci.put(rs.getString("LAC")+rs.getString("CI"), rs.getString("CI_NAME"));
				
			}
			rs.close();
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
		return lacci;
	}
	
	public ConcurrentHashMap<String,String > loadLacciBySql(String sql ) {
		ConcurrentHashMap<String,String > lacci=new ConcurrentHashMap<String,String >();
		DBAccess dbAccess;
		Connection conn = null;
		ResultSet rs;
		try {
			Class.forName(this.db.getDriver());
			conn = DriverManager.getConnection(this.db.getUrl(),
					this.db.getUser(), this.db.getPassword());
			dbAccess = new DBAccess(conn);
			System.out.println("----lacci sql:" + sql);
			rs = dbAccess.openSelect(sql);
			while (rs.next()) {
				System.out.println(rs.getString("LAC")+rs.getString("CI")+":"+rs.getString("CI_NAME"));
				lacci.put(rs.getString("LAC")+rs.getString("CI"), rs.getString("CI_NAME"));
				
			}
			rs.close();
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
		return lacci;
	}

	public static void main(String[] args) throws Exception {

		DBCfg dbCfg = new DBCfg();
		dbCfg.setDbName(args.length > 2 ? args[2] : "bigdata");
		dbCfg.setDriver("com.gbase.jdbc.Driver");
		// dbCfg.setUrl("jdbc:gbase://10.1.4.106:5258/bigdata");
		dbCfg.setUrl(args.length > 3 ? args[3]
				: "jdbc:gbase://10.1.4.106:5258/bigdata");
		dbCfg.setUser(args.length > 4 ? args[4] : "gbase");
		// dbCfg.setPassword("123456");
		dbCfg.setPassword(args.length > 5 ? args[5] : "123456");

		BusinessData.getInstance().setDb(dbCfg);
		BusinessData.getInstance().loadScenarios();
		BusinessData.getInstance().loadLacciByScenarios("机场");
		BusinessData.getInstance().loadLacciBySql("select distinct LAC,CI,CI_NAME from bigdata.cfg_base_station_info where STATUS='现网' and COVER_SCENARIOS='机场'");

	}

}
