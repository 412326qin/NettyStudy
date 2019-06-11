package com.mashibing.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server4HelloWorld {
	//监听线程组，监听客户端请求
	private EventLoopGroup acceptorGroup = null;
	//处理客户端相关操作线程组，负责处理与客户端的数据通讯
	private EventLoopGroup clientGroup = null;
	//服务启动相关配置信息
	private ServerBootstrap bootstrap = null;
	public Server4HelloWorld() {
		init();
	}
	private void init(){
		acceptorGroup = new NioEventLoopGroup();
		clientGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		//绑定线程组
		bootstrap.group(acceptorGroup,clientGroup);
		//设定通信模式为nio
		bootstrap.channel(NioServerSocketChannel.class);
		//ChannelOption.SO_SNDBUF发送缓冲区、ChannelOption.SO_RCVBUF接受缓冲区、ChannelOption.SO_KEEPALIVE开启心跳检测
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
	public ChannelFuture doAccept(int port, final ChannelHandler... acceptorHandlers) throws InterruptedException{
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
				ch.pipeline().addLast(acceptorHandlers);
			}
		});
		//bind方法 - 绑定监听端口的。ServerBootstrap可以绑定多个监听端口。多次调用bind方法即可
		//sync - 开始监听逻辑，返回一个ChannelFuture。返回接口代表的是监听成功后的一个对应的未来结果
		//使用ChannelFuture实现后续的服务器和客户端的交互；
		ChannelFuture future = bootstrap.bind(port).sync();
//		ChannelFuture future = bootstrap.bind(port).sync();
//		ChannelFuture future = bootstrap.bind(port).sync();
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
		Server4HelloWorld server = null;
		try {
			server = new Server4HelloWorld();
			future = server.doAccept(9999, new Server4HelloWorldHandler());
			System.out.println("server started.");
			//关闭链接的，回收资源
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(null != future){
				try{
					future.channel().closeFuture().sync();
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
