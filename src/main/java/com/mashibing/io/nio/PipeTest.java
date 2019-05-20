package com.mashibing.io.nio;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

import org.junit.Test;

public class PipeTest {
	@Test
	public void test() throws Exception{
		Pipe pipe = Pipe.open();
		ByteBuffer buf = ByteBuffer.allocate(1024);
		Pipe.SinkChannel sinkChannel = pipe.sink();
		buf.put("通过单向管道发送数据".getBytes());
		buf.flip();
		sinkChannel.write(buf);
		
		Pipe.SourceChannel sourceChannel = pipe.source();
		buf.flip();
		int len = sourceChannel.read(buf);
		System.out.println(new String(buf.array(),0,len));
		sourceChannel.close();
		sinkChannel.close();
	}
}
