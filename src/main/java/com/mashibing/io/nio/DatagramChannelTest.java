package com.mashibing.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

public class DatagramChannelTest {
	@Test
	public void send() throws Exception{
		DatagramChannel dc = DatagramChannel.open();
		dc.configureBlocking(false);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()){
			String str = scan.nextLine();
			buf.put((new Date().toString()+":\n"+str).getBytes());
			buf.flip();
			dc.send(buf, new InetSocketAddress("localhost",9898));
			buf.clear();
		}
		dc.close();
		scan.close();
	}
	@Test
	public void recive() throws Exception{
		DatagramChannel dc = DatagramChannel.open();
		dc.configureBlocking(false);
		dc.socket().bind(new InetSocketAddress("127.0.0.1",9898));
		Selector selector = Selector.open();
		dc.register(selector, SelectionKey.OP_READ);
		while(selector.select()>0){
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey sk = it.next();
				if(sk.isReadable()){
					ByteBuffer buf = ByteBuffer.allocate(1024);
					dc.receive(buf);
					buf.flip();
					System.out.println(new String(buf.array(),0,buf.limit()));
					buf.clear();
				}
			}
			it.remove();
		}
	}
}
