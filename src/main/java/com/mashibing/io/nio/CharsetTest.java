package com.mashibing.io.nio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class CharsetTest {
	private static String aa = "111";
	
	public CharsetTest(String aa){
		this.aa = aa;
	}
	public CharsetTest(){

	}

	public static void main(String[] args) {
		Map<String, Charset> map = Charset.availableCharsets();
		for (Entry<String, Charset> charset : map.entrySet()) {
			System.out.println(charset.getKey()+"="+charset.getValue());
		}
	}
	@Test
	public String test(String aaa){
		System.out.println(aaa);
		return this.aa;
	}
	public /*static*/ void setCmd(String aaa){
		System.out.println(aaa);
		this.aa = aaa;
		System.out.println("----" + aa);
	}
	@Test
	public void charName() throws Exception{
		Charset cs1 = Charset.forName("gbk");
		CharsetEncoder encode = cs1.newEncoder();
		CharsetDecoder decode = cs1.newDecoder();
		CharBuffer cBuf = CharBuffer.allocate(20);
		cBuf.put("啊的发电啊多发点阿斯蒂芬啊打发士大夫");
		cBuf.flip();
		ByteBuffer bBuf = encode.encode(cBuf);
		for (int i = 0; i < 10; i++) {
			System.out.println(bBuf.get());
		}
		bBuf.flip();
		CharBuffer cBuf2 = decode.decode(bBuf);
		System.out.println(cBuf2.toString());
		System.out.println("***********");
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decode1 = charset.newDecoder();
		bBuf.flip();
		//如果用CharsetDecoder的decode方法出现半个中文，
		//报java.nio.charset.MalformedInputException: Input length = 1异常
		//可以使用Charset.decode()方法，会出现乱码，但不报异常
		CharBuffer cBuf3 = decode1.decode(bBuf);
		System.out.println(cBuf3.toString());
	}
}
