/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2016年1月19日 下午4:03:23
* @Description: 无
*/
package com.nl.event.test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TestGetLastDay {
	
	public static void main(String[] args) {
		
		System.out.println(getdate(-1));
	}

	public static String getdate(int i) // //获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
	{
		Date dat = null;
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DATE, i);
		dat = cd.getTime();
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
		return dformat.format(dat);
	}
}
