package com.mashibing.io.nio;

import org.junit.Test;

/**
 * 通道（Channel）：用于源节点于目标节点的链接。在java nio 中负责缓冲区中的数据传输。
 * Channel本身不存储数据。因此需要配合缓冲区进行传输
 * java.nio.channels.Channel 接口
 * 	FileChannel-----------操作文件
 * 	SocketChannel---------网络io->tcp
 * 	ServerSocketChannel---网络io
 * 	DatagramChannel-------网络io->udp
 * 
 * java针对支持通道的类提供了getChannel()方法
 * 	本地io：
 * 		FileInputStream/FileOutputStream
 * 		RandomAccessFile
 * 	网络io：
 * 		Socket
 * 		ServerSocket
 * 		DatagramSocket
 * 在jdk 1.7中的nio.2针对各个通道提供了静态方法open()
 * 在jdk 1.7中的nio.2的Files工具类的newByteChannel()
 * 
 * @author qin41
 *
 */
public class ChannelDemo {

	public static void main(String[] args) {
		
	}
	
	@Test
	public void test1(){
		
	}
}
