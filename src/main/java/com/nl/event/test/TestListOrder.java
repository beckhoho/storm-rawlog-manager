/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年11月18日 上午9:20:05
* @Description: 无
*/
package com.nl.event.test;

import java.util.ArrayList;
import java.util.List;


public class TestListOrder {

	public static void main(String[] args) {
		
		List<String> strList=new ArrayList<String>();
		
		strList.add("0");
		strList.add("1");
		strList.add("2");
		strList.add("3");
		strList.add("4");
		strList.add("5");
		strList.add("6");
		strList.add("7");
		strList.add("8");
		strList.add("9");
		
		for(String str:strList)
			System.out.println(str);
		

	}
}
