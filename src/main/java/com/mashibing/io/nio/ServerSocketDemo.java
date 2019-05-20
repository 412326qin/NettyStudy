package com.mashibing.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

/**
 * 使用nio完成网络通信的三个核心
 * 1、通道（Channel）：负责链接
 * 	java.nio.channels.Channel接口：
 * 		SelectableChannel
 * 			SocketChannel
 * 			ServerSocketChannel
 * 			DatagramChannel
 * 
 * 			Pipe.SinkChannel
 * 			Pipe.SourceChannel
 * 2、缓冲区（Buffer）：负责数据则存取
 * 
 * 3、选择器（Selector）：时SelectableChannel的多路复用器。
 * 	用于监控SelectableChannel的io状况
 * 
 * @author qin41
 *
 */
public class ServerSocketDemo {

	@Test
	public void client() throws Exception{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		FileChannel inChannel = FileChannel.open(Paths.get("1.txt"),StandardOpenOption.READ);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		while(inChannel.read(buf) != -1){
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		inChannel.close();
		sChannel.close();
	}
	
	@Test
	public void server() throws Exception{
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		FileChannel outChannel = FileChannel.open(Paths.get("3.txt"),StandardOpenOption.CREATE,
												StandardOpenOption.WRITE,StandardOpenOption.READ);
//		ssChannel.socket();
		ssChannel.bind(new InetSocketAddress("localhost",9898));
		SocketChannel sChannel = ssChannel.accept();
		ByteBuffer buf = ByteBuffer.allocate(1024);
		while(sChannel.read(buf)!=-1){
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		sChannel.close();
		outChannel.close();
		ssChannel.close();
	}

}





















