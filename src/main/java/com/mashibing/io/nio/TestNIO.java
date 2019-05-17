package com.mashibing.io.nio;

import java.nio.ByteBuffer;

public class TestNIO {

	public static void main(String[] args) throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		System.out.println(buf.capacity());
		System.out.println(buf.hasRemaining());//是否有剩余缓冲空间
		System.out.println(buf.remaining());//还剩多少可操作的数据
		System.out.println(buf.limit());
		System.out.println(buf);
		System.out.println(buf.put(new byte[]{1,2}));
//		System.out.println(buf.clear());
		System.out.println(buf.mark());
		System.out.println("****");
		System.out.println(buf.put(new byte[]{3,4}));
		System.out.println(buf.reset());
		System.out.println(buf.remaining());//还剩多少缓冲空间
		System.out.println(buf.get(buf.position())+"dd");
		System.out.println("****");
		System.out.println(buf.remaining());//还剩多少缓冲空间
		System.out.println(buf.flip());
		System.out.println("****");
		System.out.println(buf.position());
		System.out.println(buf.get());
//		System.out.println(buf.get());
//		System.out.println(buf.get());//超过下标了
		System.out.println(buf.position());
		System.out.println(buf.rewind());//可重复读数据
		System.out.println(buf.compact());//压缩缓冲区，把没有读取的字节，放在首位
		System.out.println(buf.duplicate());//复制当前缓冲区
		System.out.println("**********8");
		ByteBuffer buff = ByteBuffer.allocate(10);
//		buff.put("1234567890".getBytes("utf-8"));
		buff.put(new byte[]{1,2,3,4,5,6,7});
		buff.flip();
		System.out.println(buff.position()+"pos");
		System.out.println(buff.get());
		System.out.println(buff.get());
		System.out.println(buff.get());
		System.out.println(buff.get());
		System.out.println(buff.position()+"pos");
		System.out.println(buff.limit()+"lim");
		/**
		 * compact的作用
		 * 该方法的作用是将 position 与 limit之间的数据复制到buffer的开始位置，
		 * 复制后 position  = limit -position,limit = capacity
		 * 但如果position 与limit 之间没有数据的话发，就不会进行复制
		 */
		buff.compact();
		System.out.println(buff.position()+"pos");
		System.out.println(buff.limit()+"lim");
		System.out.println(buff.get(0));
		System.out.println(buff.get(1));
		System.out.println(buff.get(2));
		System.out.println(buff.get(3));
		System.out.println(buff.get(4));
		System.out.println(buff.get(5));
		System.out.println(buff.get(6));
		System.out.println(buff.get(7));
		System.out.println(buff.get(8));
		System.out.println(buff.get(9));
		System.out.println(buff.position()+"pos");
		System.out.println(buff.get(0));
		System.out.println(buff.position()+"pos");
		ByteBuffer bu =buff.duplicate();
		System.out.println(bu.position()+"poss");
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.position()+"pos");
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		bu.rewind();//可重复读
		System.out.println("**********8");
		System.out.println(bu.position()+"pos");
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		System.out.println(bu.get());
		
		
	}

}
