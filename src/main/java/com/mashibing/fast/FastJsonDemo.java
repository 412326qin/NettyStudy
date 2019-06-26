package com.mashibing.fast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
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
//		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);//全局设置
		String json = "{\"@type\":\"com.mashibing.io.nio.CharsetTest\"}";
//		String json = "{\"@type\":\"com.mashibing.io.nio.CharsetTest\",\"cmd\":{\"@type\":\"sfadsff\"}}";
		CharsetTest obj = (CharsetTest)JSON.parseObject(json, Object.class);
		System.out.println(obj.test("sd"));
		obj.setCmd("cmd");
//		System.out.println(obj);
		
		String json1 = "{\"a\":\"d\",\"c\":\"e\",\"b\":\"4\"}";
		CharsetTest obj2 = JSON.parseObject(json1, CharsetTest.class);
		obj2.test("dddd");
		
		String json3 = "{\"t1\":\"1\",\"t3\":\"\",\"t2\":{\"a1\":\"1\",\"a3\":\"\",\"a2\":\"\"}}";
		HashMap  obj3 = JSON.parseObject(json3,LinkedHashMap.class,Feature.OrderedField);
		System.out.println(obj3);
		HashMap  obj4 = JSON.parseObject(json3,LinkedHashMap.class);
		System.out.println(obj4);
		
		Map<String,Object>  obj5 = JSON.parseObject(json3);
		System.out.println(obj5);
		
		Integer  obj6 = JSON.parseObject(json3).getJSONObject("t2").getInteger("a1");
		System.out.println(obj6);
		
		String json4 = "{\"a\":\"d\",\"b\":\"e\",\"c\":\"4\"}";
		BeanTest bean = JSONObject.parseObject(json4,BeanTest.class);
		System.out.println(bean.getA());
		
	}
}






















