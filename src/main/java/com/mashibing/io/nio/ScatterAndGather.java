package com.mashibing.io.nio;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 分散（Scatter)与聚集(Gather)
 * 分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 * 聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 * @author qin41
 *
 */
public class ScatterAndGather {

	public static void main(String[] args) throws Exception {
		//分散读取
		RandomAccessFile raf1 = new RandomAccessFile("1.txt","rw");
		FileChannel channel1 = raf1.getChannel();
		ByteBuffer buf1 = ByteBuffer.allocate(100);
		ByteBuffer buf2 = ByteBuffer.allocate(200);
		ByteBuffer[] bufs1 = {buf1,buf2};
		channel1.read(bufs1);
		for (ByteBuffer byteBuffer : bufs1) {
			byteBuffer.flip();
		}
		System.out.println(new String(bufs1[0].array(),0,bufs1[0].limit()));
		System.out.println("***");
		System.out.println(new String(bufs1[1].array(),0,bufs1[1].limit()));
		
		//聚集写入
		RandomAccessFile raf2 = new RandomAccessFile("2.txt","rw");
		FileChannel channel2 = raf2.getChannel();
//		ByteBuffer buf3 = ByteBuffer.allocate(200);
//		ByteBuffer buf4 = ByteBuffer.allocate(100);
//		ByteBuffer[] bufs2 = {buf3,buf4};
		channel2.write(bufs1);
//		for (ByteBuffer byteBuffer : bufs1) {
//			byteBuffer.flip();
//		}
//		
		
		
		
		
	}

}
