package com.mashibing.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Sharable注解
 * 代表当前Handler是一个可以分享的处理器，也就意味着，服务器注册此Handler后，可以分享给多个客户端
 * 同时使用。如果不适用注解描述类型，则每次客户端请求时，必须为客户端重新创建一个新的Handler对象。
 * 如果handler是一个Sharable的，一定避免定义可写的实例变量：private String name;
 * bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new XxxHandler());
			}
		});
 * ChannelInboundHandlerAdapter集成jar包4.1.35.Final版本
 * ChannelHandlerAdapter继承jar包5.0.0.Alpha2
 * @author 秦子尧 2019年6月23日 下午11:26:18
 */
@Sharable
public class Server4HelloWorldHandler extends ChannelHandlerAdapter {

	/**
	 * 业务处理逻辑
	 * 用户处理读取数据请求的逻辑
	 * ctx：上下文对象，其中包含与客户端建立链接的所有资源，如：对应的Channel
	 * msg：读取到的数据。默认类型是ByteBuf，是Netty自定义的，是对ByteBuffer的封装。不需要考虑复位问题
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
		//获取读取的诗句，是一个缓冲
		ByteBuf readBuffer = (ByteBuf)msg;
		//创建一个字节数组，用于保存缓存中的数据
		byte[] tempDatas = new byte[readBuffer.readableBytes()];
		//将缓存中的数据读取到字节数组中
		readBuffer.readBytes(tempDatas);
		String message = new String(tempDatas,"utf-8");
		System.out.println("from client:"+message);
		if("exit".equals(message)){
			ctx.close();
			return;
		}
		String line = "server message to client";
		//写操作自动释放缓存，避免内存溢出问题
		ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")));
		//注意，如果调用改的是write方法。不会刷新缓存，缓存中的数据不会发送到客户端。必须再次调用flush方法才行
		//write出去的一定是ByteBuf缓存对象
//		ctx.write(Unpooled.copiedBuffer(line.getBytes("utf-8")));
//		ctx.flush();
	}

	/**
	 * 异常处理逻辑，当客户端异常退出的时候，也会运行
	 * ChannelHandlerContext关闭，也代表当前与客户端链接的资源关闭
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable caluse)throws Exception{
		System.out.println("server exceptionCaught method run...");
		ctx.close();
	}
	
}
