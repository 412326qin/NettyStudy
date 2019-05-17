package com.mashibing.io.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class MyNio2Channel {
	public static void main(String[] args) throws Exception {
		// test1();
		test4();
	}

	// 非直接缓冲区 读写操作
	@Test
	public static void test1() throws Exception {
		long startTime = System.currentTimeMillis();

		// 输入流
		FileInputStream inputStream = new FileInputStream("12.pdf");
		// 输出流
		FileOutputStream outputStream = new FileOutputStream("2.pdf");
		// 创建通道
		FileChannel inChannel = inputStream.getChannel();
		FileChannel outChannel = outputStream.getChannel();
		// 分配指定大小的缓冲区
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		while (inChannel.read(byteBuffer) != -1) {
			// 开启读取模式
			byteBuffer.flip();
			// 有数据写入到通道中
			outChannel.write(byteBuffer);
			byteBuffer.clear();
		}

		// 关闭通道，关闭连接
		inChannel.close();
		outChannel.close();
		inputStream.close();
		outputStream.close();

		long endTime = System.currentTimeMillis();

		System.out.println("程序执行时间：" + (endTime - startTime));
	}

	// 直接缓冲区 读写操作
	@Test
	public static void test2() throws IOException {
		long startTime = System.currentTimeMillis();

		// 创建管道
		FileChannel inChannel = FileChannel.open(Paths.get("12.pdf"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("1.pdf"), StandardOpenOption.WRITE);
		// 定义映射文件
		MappedByteBuffer inMappedByte = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMappedByte = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
		// 直接对缓冲区操作
		byte[] dsf = new byte[inMappedByte.limit()];
		inMappedByte.get(dsf);
		outMappedByte.get(dsf);
		inChannel.close();
		outChannel.close();

		long endTime = System.currentTimeMillis();

		System.out.println("程序执行时间：" + (endTime - startTime));

	}

	// 非直接缓冲区 读写操作
	@Test
	public static void test3() throws Exception {
		long startTime = System.currentTimeMillis();

		// 输入流
		FileInputStream inputStream = new FileInputStream("D://Energy.rar");
		// 输出流
		FileOutputStream outputStream = new FileOutputStream("D://Energy1.rar");
		// 创建通道
		FileChannel inChannel = inputStream.getChannel();
		FileChannel outChannel = outputStream.getChannel();
		// 分配指定大小的缓冲区
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		while (inChannel.read(byteBuffer) != -1) {
			// 开启读取模式
			byteBuffer.flip();
			// 有数据写入到通道中
			outChannel.write(byteBuffer);
			byteBuffer.clear();
		}

		// 关闭通道，关闭连接
		inChannel.close();
		outChannel.close();
		inputStream.close();
		outputStream.close();

		long endTime = System.currentTimeMillis();

		System.out.println("程序执行时间：" + (endTime - startTime));
	}

	// 直接缓冲区 读写操作
	@Test
	public static void test4() throws IOException {
		long startTime = System.currentTimeMillis();

		// 创建管道
		FileChannel inChannel = FileChannel.open(Paths.get("12.pdf"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("1.pdf"), StandardOpenOption.READ, 
											StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		// 定义映射文件
		//map方法将文件的某个区域映射到内存中
		/**
		map(FileChannel.MapMode mode, long position, long size)   将此通道的文件区域直接映射到内存中。 
		mode - 根据是按只读、读取/写入或专用（写入时拷贝）来映射文件， 
		分别为 FileChannel.MapMode 类中所定义的 READ_ONLY、READ_WRITE 或 PRIVATE 之一
		position - 文件中的位置，映射区域从此位置开始；必须为非负数
		size - 要映射的区域大小；必须为非负数且不大于 Integer.MAX_VALUE */
		//只有ByteBuffer支持内存映射
		MappedByteBuffer inMappedByte = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMappedByte = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
		// 直接对缓冲区操作
		byte[] dsf = new byte[inMappedByte.limit()];
		inMappedByte.get(dsf);//将缓冲区域的内容输出到指定数组	
		outMappedByte.get(dsf);
		inChannel.close();
		outChannel.close();

		long endTime = System.currentTimeMillis();

		System.out.println("程序执行时间：" + (endTime - startTime));

	}
	@Test
	public void test5() throws Exception{
		//StandardOpenOption.CREATE_NEW如果文件存在就抛异常
		FileChannel inChannel = FileChannel.open(Paths.get(""), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get(""), StandardOpenOption.WRITE,
												StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);
		inChannel.transferTo(0, inChannel.size(), outChannel);
		outChannel.transferFrom(inChannel, 0, inChannel.size());
	}
}










