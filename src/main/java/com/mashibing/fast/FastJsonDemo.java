package com.mashibing.fast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.mashibing.io.nio.CharsetTest;

public class FastJsonDemo {
	public static void main(String[] args) {
		/**
		 * 必须是setXXX方法，不能是静态方法，不过static成员变量可以直接修改值。
		 * 如果是1.2.58安全版本可以使用
		 * ParserConfig.getGlobalInstance().addAccept("com.mashibing.io");
		 * 添加白名单，如果有多个包名，用,逗号隔开
		 */
//		ParserConfig.getGlobalInstance().addAccept("com.mashibing.io");//非全局
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);//全局设置
		String json = "{\"@type\":\"com.mashibing.io.nio.CharsetTest\"}";
//		String json = "{\"@type\":\"com.mashibing.io.nio.CharsetTest\",\"cmd\":{\"@type\":\"sfadsff\"}}";
		CharsetTest obj = (CharsetTest)JSON.parseObject(json, Object.class);
		System.out.println(obj.test("sd"));
		obj.setCmd("cmd");
		System.out.println(obj);
		
		String json1 = "{}";
		CharsetTest obj2 = (CharsetTest)JSON.parseObject(json1, CharsetTest.class);
		obj2.test("dddd");
	}
}
