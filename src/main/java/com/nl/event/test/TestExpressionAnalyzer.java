/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年11月27日 下午5:34:24
* @Description: 无
*/
package com.nl.event.test;

import neu.sxc.expression.Expression;
import neu.sxc.expression.ExpressionFactory;
import neu.sxc.expression.tokens.Valuable;

public class TestExpressionAnalyzer {
	public static void main(String[] args) {
		ExpressionFactory factory = ExpressionFactory.getInstance(); 
		// (S1<=100 && S1>=30) && (S2>10 || S2<20);
		// (1601 || 1602);
		// 1601  12101==5
		// 1602  11101==6
		//(12101==5 || 11101==6);
		// 12101  5  
		// 11101 6
		// "(5==5|| 6==6);"
		
		
		Expression exp = factory.getExpression("(true|| false);"); 
		System.out.println("1"+exp);
		exp.lexicalAnalysis();//词法分析 
		Valuable result = exp.evaluate();//执行 
		System.out.println("2"+result);
		System.out.println(result.getBooleanValue());
	}  

}
