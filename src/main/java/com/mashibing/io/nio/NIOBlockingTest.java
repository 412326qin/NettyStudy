package com.mashibing.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

public class NIOBlockingTest {
	@Test
	public void client() throws Exception{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		sChannel.configureBlocking(false);
		//		sChannel.bind(new InetSocketAddress("127.0.0.1",9898));
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put(new Date().toString().getBytes());
		System.out.println(new Date().toString().getBytes());
		System.out.println(buf.toString());
		//写出通道，输入通道前，一定要flip()
		//写入通道，写出通道前，一定要flip()
		//通道操作前一定要flip();
		buf.flip();
		sChannel.write(buf);
		buf.clear();
		sChannel.close();
	}
	@Test
	public void server() throws Exception{
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//		ssChannel.bind(new InetSocketAddress("127.0.0.1",9898));
		ssChannel.socket().bind(new InetSocketAddress("127.0.0.1",9898));
		ssChannel.configureBlocking(false);
		Selector selector = Selector.open();
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		while(selector.select()>0){
			Iterator<SelectionKey> itSelect = selector.selectedKeys().iterator();
			while(itSelect.hasNext()){
				SelectionKey selectKey = itSelect.next();
				if(selectKey.isAcceptable()){
					SocketChannel sChannel = ssChannel.accept();
					//一定要先配置为非阻塞模式，再注册选择器
					//一定要先配置为非阻塞模式，再注册选择器
					//会报强制关闭远程链接
					sChannel.configureBlocking(false);
					sChannel.register(selector, SelectionKey.OP_READ);
				}else if(selectKey.isReadable()){
					SocketChannel sChannel = (SocketChannel) selectKey.channel();
					ByteBuffer buf = ByteBuffer.allocate(1024);
					int len;
					while((len = sChannel.read(buf)) > 0){
						buf.flip();
						System.out.println(new String(buf.array(),0,len));
						buf.clear();
					}
				}
				itSelect.remove();
			}
		}

	}
	@Test
	public void client1() throws Exception{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("localhost",9898));
		sChannel.configureBlocking(false);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put(new Date().toString().getBytes());
		buf.flip();
		sChannel.write(buf);
		sChannel.close();
	}
	@Test
	public void server1() throws Exception{
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		ssChannel.socket().bind(new InetSocketAddress("localhost",9898));
		ssChannel.configureBlocking(false);
		Selector selector = Selector.open();
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		while(selector.select()>0){
			Iterator<SelectionKey> itSelector = selector.selectedKeys().iterator();
			while(itSelector.hasNext()){
				SelectionKey selectionKey = itSelector.next();
				if(selectionKey.isAcceptable()){
					SocketChannel sChannel = ssChannel.accept();
					sChannel.configureBlocking(false);
					sChannel.register(selector, SelectionKey.OP_READ);
				}else if(selectionKey.isReadable()){
					SocketChannel sChannel = (SocketChannel)selectionKey.channel();
					ByteBuffer buf = ByteBuffer.allocate(1024);
					int len;
					while((len=sChannel.read(buf)) != -1){
						buf.flip();
						System.out.println(new String(buf.array(),0,len));
						buf.clear();
					}
				}
				itSelector.remove();
			}
		}
	}

}



































