package com.mashibing.netty.fixedLength;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class Server4FixedLength {
	//监听线程组，监听客户端请求
	private EventLoopGroup acceptorGroup = null;
	//处理客户端相关操作线程组，负责处理与客户端的数据通讯
	private EventLoopGroup clientGroup = null;
	//服务启动相关配置信息
	private ServerBootstrap bootstrap = null;
	public Server4FixedLength() {
		init();
	}
	private void init(){
		//初始化线程组，构建线程组的时候，如果不传递参数，则默认构建的线程组数是CPU核心数量。
		acceptorGroup = new NioEventLoopGroup(1);//参数传1代表线程组的线程数为1（单线程模型）
		clientGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		//绑定线程组
		bootstrap.group(acceptorGroup,clientGroup);
//		bootstrap.group(acceptorGroup,acceptorGroup);单线程模型
		//设定通信模式为nio
		bootstrap.channel(NioServerSocketChannel.class);
		//设定缓冲区大小，缓冲区的单位是字节
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		//ChannelOption.SO_SNDBUF发送缓冲区、ChannelOption.SO_RCVBUF接受缓冲区、ChannelOption.SO_KEEPALIVE开启心跳检测（链接有效性）
		bootstrap.option(ChannelOption.SO_SNDBUF,16*1024)
				.option(ChannelOption.SO_RCVBUF, 16*1024)
				.option(ChannelOption.SO_KEEPALIVE,true);
	}
	/**
	 * 监听处理逻辑
	 * @param port监听端口
	 * @param acceptorHandlers 处理器，如何处理客户端请求
	 * @return
	 * @throws InterruptedException 
	 */
//	public ChannelFuture doAccept(int port,ChannelHandler.Sharable){
	public ChannelFuture doAccept(int port) throws InterruptedException{
		/*
		 * childHandler是服务端的Bootstrap独有的方法，适用于提供处理对象的
		 * 可以一次性增加若干个处理逻辑，是类似责任链模式的处理方式
		 * 增加A，B两个处理逻辑，在处理客户端请求数据的时候根据A-》B顺序一次处理
		 * 
		 * ChannelInintializer - 用于提供处理器的一个模型对象。
		 * 	其中定义了一个方法，initChannel方法
		 * 	方法用于初始化处理逻辑责任链条的。
		 * 	可以保证服务端的Boootstrap只初始化一次处理器，尽量提供和处理逻辑的重用
		 * 	避免反复的创建处理器对象。节约资源开销
		 */
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelHandler[] acceptorHandlers = new ChannelHandler[3];
				//定长Handler。通过构造参数设置消息长度（单位是字节）。发送消息长度不足可以使用空格不全。
				acceptorHandlers[0] = new FixedLengthFrameDecoder(3);
				//字符串解码器Handler，会自动处理channelRead方法的msg参数，将ByteBuf类型的数据转换为字符串。
				acceptorHandlers[1] = new StringDecoder(Charset.forName("utf-8"));
				acceptorHandlers[2] = new Server4FixedLengthHandler();
				ch.pipeline().addLast(acceptorHandlers);
			}
		});
		//bind方法 - 绑定监听端口的。ServerBootstrap可以绑定多个监听端口。多次调用bind方法即可
		//sync - 开始监听逻辑，返回一个ChannelFuture。返回接口代表的是监听成功后的一个对应的未来结果
		//使用ChannelFuture实现后续的服务器和客户端的交互；
		ChannelFuture future = bootstrap.bind(port).sync();
//		ChannelFuture future = bootstrap.bind(port).sync();
//		ChannelFuture future = bootstrap.bind(port).sync();
		System.out.println("return future");
		return future;
	}
	/*
	 * 回收方法,shutdownGracefully - 方法是一个安全关闭的方法，可以保证不放弃任何一个已接受的客户端请求
	 */
	public void release(){
		this.acceptorGroup.shutdownGracefully();
		this.clientGroup.shutdownGracefully();
	}
	public static void main(String[] args) throws InterruptedException {
		ChannelFuture future = null;
		Server4FixedLength server = null;
		try {
			server = new Server4FixedLength();
			future = server.doAccept(9999);
			System.out.println("server started.");
			//关闭链接的，回收资源
			future.channel().closeFuture().sync();
			System.out.println("server end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(null != future){
				try{
					future.channel().closeFuture().sync();
					System.out.println("server end.");
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			if(null != server){
				server.release();
			}
		}
	}
}
class Server4FixedLengthHandler extends ChannelHandlerAdapter{
	//业务处理逻辑
	@Override 
	public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
//		ByteBuf buf = (ByteBuf)msg;
		String message = msg.toString();
		System.out.println("from client:" + message);
		String line = "ok ";
		ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")));
	}
	//异常处理逻辑
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		System.out.println("server exceptionCaught method run..");
//		cause.printStackTrace();
		ctx.close();
	}
}


























