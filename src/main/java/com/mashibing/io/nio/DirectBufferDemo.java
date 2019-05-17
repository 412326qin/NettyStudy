package com.mashibing.io.nio;

import java.nio.ByteBuffer;

public class DirectBufferDemo {

	public static void main(String[] args) {
		ByteBuffer buf = ByteBuffer.allocateDirect(20);
		System.out.println(buf.order());
		System.out.println(buf.isDirect());
	}

}
