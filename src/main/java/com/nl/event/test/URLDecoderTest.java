/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2016年1月12日 上午10:46:47
 * @Description: 无
 */
package com.nl.event.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.nl.util.GlobalConst;

public class URLDecoderTest {

	public static void main(String[] args) throws Exception {
		String query="WT.branch=xj&WT.host=www.xj.10086.cn&WT.es=http%3A%2F%2Fwww.xj.10086.cn%2Fservice%2F&WT.referrer=http%3A%2F%2Fm.baidu.com%2Ffrom%3D1099a%2Fbd_page_type%3D1%2Fssid%3D0%2Fuid%3D0%2Fbaiduid%3D666AC820F376D68026C4D7F640BC3A9D%2Fw%3D0_10_10086.com%25E7%25BD%2591%25E4%25B8%258A%25E8%2590%25A5%25E4%25B8%259A%2Ft%3Dzbios%2Fl%3D3%2Ftc%3Fref%3Dwww_zbios%26pu%3Dsz%25401320_480%252Ccuid%2540C6A4CF134C6FBDE33C5A794BE1D05DA8EE77386A6OCDGDIEHSC%252Ccua%2540640_1136_iphone_7.0.0.0_0%252Ccut%2540iPhone6%25252C2_9.2%252Cosname%2540baiduboxapp%252Cctv%25401%252Ccfrom%25401099a%252Ccsrc%2540home_box_txt%252Cta%2540zbios_1_9.2_6_0.0%26lid%3D12653196918751570515%26order%3D2%26vit%3Dosres%26tj%3Dwww_normal_2_0_10_title%26m%3D8%26srd%3D1%26cltj%3Dcloud_title%26dict%3D32%26title%3D%25E7%25BD%2591%25E4%25B8%258A%25E8%2590%25A5%25E4%25B8%259A%25E5%258E%2585%26sec%3D9466%26di%3Db21c915aae87d085%26bdenc%3D1%26tch%3D124.0.0.0.0.0%26nsrc%3DIlPT2AEptyoA_yixCFOxXnANedT62v3IEQGG_zZOLGfezZXlxP4lZQRAUTLhRn_ZJoCb9a%26eqid%3Daf992f8edf41280010000001569e0aae%26wd%3D&WT.sr=320x568&WT.ti=%E&WT.co=Yes&WT.hdr.Host=www.xj.10086.cn&WT.hdr.Accept=*/*";
		System.out.println(parse2("date", "time", "ip",  "uri", query));
	}
	
	static String parse2(String date, String time, String ip,String uri, String query) {
		StringBuffer data = new StringBuffer();
		StringBuffer queryParse = new StringBuffer();
		Map<String, String> kv = new HashMap<String, String>();
		String[] fields = null;
		fields=query.split("&");
		for (String s : fields) {
			String parse;
			try {
				parse = URLDecoder.decode(s, "UTF8");
			}catch (UnsupportedEncodingException|IllegalArgumentException e) {
				//com.nl.util.log.Log.error(e.getMessage());
				//com.nl.util.log.Log.error(query);
				continue;
			}
			//System.out.println(parse);
			queryParse.append(parse).append("&");
			if (parse.contains("=")) {
				String[] k_v = parse.split("=");
				if (k_v.length>1) kv.put(k_v[0].toLowerCase(), k_v[1]);
			}
		}

		data.append(date).append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(time).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.co_f")) {
			data.append(kv.get("wt.co_f"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.mobile")) {
			data.append(kv.get("wt.mobile"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(ip).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.gc")) {
			data.append(kv.get("wt.gc"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.branch")) {
			data.append(kv.get("wt.branch"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(uri).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.event")) {
			data.append(kv.get("wt.event"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.pn_sku")) {
			data.append(kv.get("wt.pn_sku"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.si_n")) {
			data.append(kv.get("wt.si_n"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.si_x")) {
			data.append(kv.get("wt.si_x"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("WT.nv")) {
			data.append(kv.get("wt.nv"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.ac")) {
			data.append(kv.get("wt.ac"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.oss")) {
			data.append(kv.get("wt.oss"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.mc_id")) {
			data.append(kv.get("wt.mc_id"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.errcode")) {
			data.append(kv.get("wt.errcode"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);

		data.append(queryParse.substring(0, queryParse.length()-1));
		return data.toString();
	}
	
	String parse1(String date, String time, String ip,String uri, String query) {
		StringBuffer data = new StringBuffer();

		Map<String, String> kv = new HashMap<String, String>();
		String[] fields = null;
		String parse;
		try {//
			parse = URLDecoder.decode(query, "UTF8");//
		} catch (UnsupportedEncodingException|IllegalArgumentException e) {
			com.nl.util.log.Log.error(e.getMessage());
			com.nl.util.log.Log.error(query);
			return null;
		}
		fields=parse.split("&");
		for (String s : fields) {
			//System.out.println(s);
			if (s.contains("=")) {
				String[] k_v = s.split("=");
				if (k_v.length>1) kv.put(k_v[0].toLowerCase(), k_v[1]);
			}
		}
		data.append(date).append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(time).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.co_f")) {
			data.append(kv.get("wt.co_f"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.mobile")) {
			data.append(kv.get("wt.mobile"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(ip).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.gc")) {
			data.append(kv.get("wt.gc"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.branch")) {
			data.append(kv.get("wt.branch"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(uri).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.event")) {
			data.append(kv.get("wt.event"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.pn_sku")) {
			data.append(kv.get("wt.pn_sku"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.si_n")) {
			data.append(kv.get("wt.si_n"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.si_x")) {
			data.append(kv.get("wt.si_x"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("WT.nv")) {
			data.append(kv.get("wt.nv"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.ac")) {
			data.append(kv.get("wt.ac"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.oss")) {
			data.append(kv.get("wt.oss"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.mc_id")) {
			data.append(kv.get("wt.mc_id"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.errcode")) {
			data.append(kv.get("wt.errcode"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);

		data.append(parse);
		return data.toString();
	}

}