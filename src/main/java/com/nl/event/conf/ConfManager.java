/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月13日 下午2:28:17
 * @Description: 无
 */
package com.nl.event.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.bean.AtomEvent;
import com.nl.bean.RuleInstance;
import com.nl.bean.SaleActive;
import com.nl.bean.SaleActiveRuleParam;
import com.nl.bean.SourceData;
import com.nl.util.config.DBCfg;
import com.nl.util.db.DBAccess;

public class ConfManager {
	private static final Logger LOG = LoggerFactory.getLogger(ConfManager.class);
	private DBCfg db;
	private final static ConfManager INSTANCE= new ConfManager();
	public static ConfManager getInstance() {
		return INSTANCE;
	}
	//*********************************GetSaleActiveListSpout*****************************
	private List<SaleActive> saleActiveList;// active_id
	private Map<String, List<RuleInstance>> ruleInstances;// active_id,ruleInstanceList
	private Map<String, List<SaleActiveRuleParam>> ruleInstanceParams;// ruleInstanceId ,paramList
	public void loadSaleActive() {
		DBAccess dbAccess;
		Connection conn = null;
		String strSql;
		ResultSet rs;
		try {
			Class.forName(this.db.getDriver());
			conn = DriverManager.getConnection(this.db.getUrl(),this.db.getUser(), this.db.getPassword());
			dbAccess = new DBAccess(conn);
			// 1
			strSql = "select distinct t1.active_id ,t1.step_id,t1.usergroup_id,t1.usergroup_refresh,t1.area_id,t1.is_capture,t1.active_opp_id,t1.rule_exprs,t1.request_timestamp from  cfg_sale_active  t1 inner join  cfg_custom_group_collect_info t2 on t1.active_id = t2.active_id where t2.is_finish =1 and t1.active_id not in(select distinct t1.active_id from  cfg_sale_active  t1 inner join  cfg_custom_group_collect_info t2 on t1.active_id = t2.active_id where t2.is_finish =0)";
			LOG.info("----saleActiveList sql:" + strSql);
			rs = dbAccess.openSelect(strSql);
			saleActiveList = new ArrayList<>();
			while (rs.next()) {
				LOG.info("--1--runsql-------------" + rs.getString("active_id"));
				SaleActive saleActive = new SaleActive();
				saleActive.setActiveId(rs.getString("active_id"));
				saleActive.setStepId(rs.getString("step_id"));
				saleActive.setUserGroupId(rs.getString("usergroup_id"));
				saleActive.setUserGroupRefresh(rs.getString("usergroup_refresh"));
				saleActive.setAreaId(rs.getString("area_id"));
				saleActive.setIsCapture(rs.getString("is_capture"));
				saleActive.setActiveOppId(rs.getString("active_opp_id"));
				saleActive.setRuleExpres(rs.getString("rule_exprs"));
				// saleActive.setRequestContent(rs.getString(1));
				saleActive.setRequestTimeStamp(rs.getString("request_timestamp"));
				saleActiveList.add(saleActive);
			}
			rs.close();
			dbAccess.closeSelect();
			// 2
			strSql = "select t.active_id,t.rule_instance_id,t.event_id,t.param_exprs,t.param_exprs_detail from CFG_SALE_ACTIVE_RULE t where t.active_id in(select distinct t1.active_id from  cfg_sale_active  t1 inner join  cfg_custom_group_collect_info t2 on t1.active_id = t2.active_id where t2.is_finish =1 and t1.active_id not in(select distinct t1.active_id from  cfg_sale_active  t1 inner join  cfg_custom_group_collect_info t2 on t1.active_id = t2.active_id where t2.is_finish =0))";
			LOG.info("----ruleList sql:" + strSql);
			rs = dbAccess.openSelect(strSql);
			ruleInstances = new HashMap<String, List<RuleInstance>>();
			while (rs.next()) {
				LOG.info("--2--runsql-------------");
				RuleInstance ruleInstance = new RuleInstance();
				ruleInstance.setActiveId(rs.getString("active_id"));
				ruleInstance.setRuleInstanceId(rs.getString("rule_instance_id"));
				// ruleInstance.setRuleId(rs.getString(""));
				ruleInstance.setEventId(rs.getString("event_id"));
				ruleInstance.setParamExpres(rs.getString("param_exprs"));
				ruleInstance.setParamExpresDetail(rs.getString("param_exprs_detail"));
				List<RuleInstance> list = ruleInstances.get(ruleInstance.getActiveId());
				if (list == null) {
					list = new ArrayList<RuleInstance>();
					list.add(ruleInstance);
				} else {
					list.add(ruleInstance);
				}
				ruleInstances.put(ruleInstance.getActiveId(), list);
			}
			rs.close();
			dbAccess.closeSelect();
			// 3
			strSql = "select t.active_id,t.rule_instance_id,t.param_id,t.oper_id,t.param_value from cfg_sale_active_rule_param t where t.active_id in(select distinct t1.active_id from  cfg_sale_active  t1 inner join  cfg_custom_group_collect_info t2 on t1.active_id = t2.active_id where t2.is_finish =1 and t1.active_id not in(select distinct t1.active_id from  cfg_sale_active  t1 inner join  cfg_custom_group_collect_info t2 on t1.active_id = t2.active_id where t2.is_finish =0))";
			LOG.info("----paramList sql:" + strSql);
			rs = dbAccess.openSelect(strSql);
			ruleInstanceParams = new HashMap<String, List<SaleActiveRuleParam>>();
			while (rs.next()) {
				LOG.info("--3--runsql-------------");
				SaleActiveRuleParam saleActiveRuleParam = new SaleActiveRuleParam();
				saleActiveRuleParam.setActiveId(rs.getString("active_id"));
				saleActiveRuleParam.setRuleInstanceId(rs.getString("rule_instance_id"));
				saleActiveRuleParam.setParamId(rs.getString("param_id"));
				saleActiveRuleParam.setParamValue(rs.getString("param_value"));
				saleActiveRuleParam.setOperId(rs.getString("oper_id"));
				List<SaleActiveRuleParam> list = ruleInstanceParams.get(saleActiveRuleParam.getRuleInstanceId());
				if (list == null) {
					list = new ArrayList<SaleActiveRuleParam>();
					list.add(saleActiveRuleParam);
				} else {
					list.add(saleActiveRuleParam);
				}
				ruleInstanceParams.put(saleActiveRuleParam.getRuleInstanceId(),list);
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

	public List<SaleActive> getSaleActiveList() {
		return saleActiveList;
	}

	public Map<String, List<SaleActiveRuleParam>> getRuleInstanceParams() {
		return ruleInstanceParams;
	}

	public void setRuleInstanceParams(
			Map<String, List<SaleActiveRuleParam>> ruleInstanceParams) {
		this.ruleInstanceParams = ruleInstanceParams;
	}

	public void setSaleActiveList(List<SaleActive> saleActiveList) {
		this.saleActiveList = saleActiveList;
	}

	public Map<String, List<RuleInstance>> getRuleInstances() {
		return ruleInstances;
	}

	public void setRuleInstances(Map<String, List<RuleInstance>> ruleInstances) {
		this.ruleInstances = ruleInstances;
	}
	//*********************************************************************************

	//*******************************CaptureAtomEventBolt*******************************
	private SourceData sourceData;
	private List<AtomEvent> atomEventList;
	private List<AtomEvent> delayAtomEventList;

	public void loadCaptureConfigBySourceId(String sourceId) {
		DBAccess dbAccess;
		Connection conn = null;
		String strSql;
		ResultSet rs;
		try {
			Class.forName(this.db.getDriver());
			conn = DriverManager.getConnection(this.db.getUrl(),this.db.getUser(), this.db.getPassword());
			dbAccess = new DBAccess(conn);
			strSql = "select SOURCE_ID,SOURCE_NAME,SOURCE_DESC,FIELDS_LIST,FIELD_DELIMITER,FIELDS_SIZE,KEY_FIELD_INDEX,KEY_FIELD,INSTANCE from bigdata.CFG_SOURCE_DATA where SOURCE_ID="
					+ sourceId;
			rs = dbAccess.openSelect(strSql);
			sourceData = new SourceData();
			while (rs.next()) {
				sourceData.setSourceId(rs.getString("SOURCE_ID"));
				sourceData.setSourceName(rs.getString("SOURCE_NAME"));
				sourceData.setSourceDesc(rs.getString("SOURCE_DESC"));
				sourceData.setFieldsList(rs.getString("FIELDS_LIST"));
				sourceData.setFieldsSize(rs.getInt("FIELDS_SIZE"));
				sourceData.setFieldDelimiter(rs.getString("FIELD_DELIMITER"));
				sourceData.setKeyField(rs.getString("KEY_FIELD"));
				sourceData.setKeyFieldIndex(rs.getInt("KEY_FIELD_INDEX"));
				sourceData.setInstance(rs.getString("INSTANCE"));
			}
			rs.close();
			dbAccess.closeSelect();
			strSql = "select EVENT_ID,EVENT_NAME,CAPTURE_FIELD,EXPRESSION,EXPRESSION_PARAM from bigdata.INF_ATOM_EVENT where IN_USE=1 AND IS_REALIZE=1 AND SOURCE_ID="
					+ sourceId;
			rs = dbAccess.openSelect(strSql);
			atomEventList = new ArrayList<AtomEvent>();
			delayAtomEventList= new ArrayList<AtomEvent>();
			while (rs.next()) {
				AtomEvent atomEvent = new AtomEvent();
				atomEvent.setEventId(rs.getString("EVENT_ID"));
				atomEvent.setEventName(rs.getString("EVENT_NAME"));
				// atomEvent.setSourceId(rs.getString(1));
				atomEvent.setCaptureField(rs.getString("CAPTURE_FIELD"));
				atomEvent.setExpression(rs.getString("EXPRESSION"));
				atomEvent.setExpressionParam(rs.getString("EXPRESSION_PARAM"));
				if (rs.getString("EXPRESSION") != null) {
					if ("ALL".equals(rs.getString("EXPRESSION"))
							|| rs.getString("EXPRESSION").contains("==")
							|| rs.getString("EXPRESSION")
									.startsWith("LOCATION")) {
						atomEventList.add(atomEvent);
					}
					if (rs.getString("EXPRESSION").startsWith("DELAY_LOCATION")) {
						delayAtomEventList.add(atomEvent);
					}
				}
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

	public SourceData getSourceData() {
		return sourceData;
	}

	public List<AtomEvent> getAtomEventList() {
		return atomEventList;
	}

	public List<AtomEvent> getDelayAtomEventList() {
		return delayAtomEventList;
	}

	//*********************************************************************************

	public DBCfg getDb() {
		return db;
	}

	public void setDb(DBCfg db) {
		this.db = db;
	}

	public static void main(String[] args) throws Exception {

		List<SaleActive> saleActiveList;// active_id
		Map<String, List<RuleInstance>> ruleInstances;// active_id , rulelist
		Map<String, List<SaleActiveRuleParam>> ruleInstanceParams;// rule_id ,

		DBCfg dbCfg = new DBCfg();
		dbCfg.setDbName("bigdata");
		dbCfg.setDriver("com.gbase.jdbc.Driver");
		dbCfg.setUrl("jdbc:gbase://10.238.156.1:5258/bigdata");
		dbCfg.setUser("gbase");
		dbCfg.setPassword("gbase20110531");

		ConfManager.getInstance().setDb(dbCfg);
		ConfManager.getInstance().loadSaleActive();
		saleActiveList = ConfManager.getInstance().getSaleActiveList();
		ruleInstances = ConfManager.getInstance().getRuleInstances();
		ruleInstanceParams = ConfManager.getInstance().getRuleInstanceParams();

		for (SaleActive saleActive : saleActiveList) {
			LOG.info("-----saleActive------"
					+ saleActive.getActiveId());
			for (RuleInstance ruleInstance : ruleInstances.get(saleActive.getActiveId())) {
				LOG.info("----rule-------"
						+ ruleInstance.getRuleInstanceId());
				for (SaleActiveRuleParam p : ruleInstanceParams
						.get(ruleInstance.getRuleInstanceId())) {
					LOG.info("----p-------" + p.getParamId() + ":"
							+ p.getParamValue());

				}
			}
		}
	}

}

/*private Map<String, String> station;
private Map<String, String> airport;
private Map<String, String> scenicSpots;
private Map<String, String> otherScene;

public void loadLocaltionRecognition() {
	DBAccess dbAccess;
	Connection conn = null;
	String strSql;
	ResultSet rs;
	try {
		Class.forName(this.db.getDriver());
		conn = DriverManager.getConnection(this.db.getUrl(),
				this.db.getUser(), this.db.getPassword());
		dbAccess = new DBAccess(conn);

		// 车站
		strSql = "select lac,ci form xxx where 覆盖场景 like '车站'";
		rs = dbAccess.openSelect(strSql);
		station = new HashMap<String, String>();
		while (rs.next()) {
			station.put(rs.getString(1) + rs.getString(2), rs.getString(3));
			LOG.info("\nQuery result:\n" + "lac:" + rs.getString(1) + "\n"
					+ "ci:" + rs.getString(2) + "\n" + "覆盖场景:"
					+ rs.getString(3));

		}
		rs.close();
		dbAccess.closeSelect();

		// 机场
		strSql = "select lac,ci form xxx where 覆盖场景 like '机场'";
		rs = dbAccess.openSelect(strSql);
		airport = new HashMap<String, String>();
		while (rs.next()) {
			airport.put(rs.getString(1) + rs.getString(2), rs.getString(3));
			LOG.info("\nQuery result:\n" + "lac:" + rs.getString(1) + "\n"
					+ "ci:" + rs.getString(2) + "\n" + "覆盖场景:"
					+ rs.getString(3));

		}
		rs.close();
		dbAccess.closeSelect();

		// 旅游景点
		strSql = "select lac,ci form xxx where 覆盖场景 like '旅游景点'";
		rs = dbAccess.openSelect(strSql);
		scenicSpots = new HashMap<String, String>();
		while (rs.next()) {
			scenicSpots.put(rs.getString(1) + rs.getString(2),
					rs.getString(3));
			LOG.info("\nQuery result:\n" + "lac:" + rs.getString(1) + "\n"
					+ "ci:" + rs.getString(2) + "\n" + "覆盖场景:"
					+ rs.getString(3));

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

public Map<String, String> getStation() {
	return station;
}

public void setStation(Map<String, String> station) {
	this.station = station;
}

public Map<String, String> getAirport() {
	return airport;
}

public void setAirport(Map<String, String> airport) {
	this.airport = airport;
}

public Map<String, String> getScenicSpots() {
	return scenicSpots;
}

public void setScenicSpots(Map<String, String> scenicSpots) {
	this.scenicSpots = scenicSpots;
}

public Map<String, String> getOtherScene() {
	return otherScene;
}

public void setOtherScene(Map<String, String> otherScene) {
	this.otherScene = otherScene;
}
*/


/*private Map<String, String> urlRecognition;// <url,class>

public void loadUrlRecognition() {
	DBAccess dbAccess;
	Connection conn = null;
	String strSql;
	ResultSet rs;
	try {
		Class.forName(this.db.getDriver());
		conn = DriverManager.getConnection(this.db.getUrl(),
				this.db.getUser(), this.db.getPassword());
		dbAccess = new DBAccess(conn);
		strSql = "select a.code url_code,a.name url_name,a.url,b.code class_code,b.name class_name from xjdm.d_pref_url a inner join  xjdm.d_pref_class b on a.class_code=b.code where a.in_use=1 and b.in_use=1";
		rs = dbAccess.openSelect(strSql);
		urlRecognition = new HashMap<String, String>();
		while (rs.next()) {
			urlRecognition.put(rs.getString(3), rs.getString(4));
			LOG.info("\nQuery result:\n" + "url code:" + rs.getInt(1)
					+ "\n" + "url name:" + rs.getString(2) + "\n" + "url:"
					+ rs.getString(3) + "\n" + "class code:" + rs.getInt(4)
					+ "\n" + "class name:" + rs.getString(5));

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

public Map<String, String> getUrlRecognition() {
	return urlRecognition;
}*/