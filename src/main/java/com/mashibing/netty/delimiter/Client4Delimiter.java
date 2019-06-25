package com.mashibing.netty.delimiter;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.ReferenceCountUtil;

/**
 * 因为客户端是请求的发起者，不需要监听
 * @author 秦子尧 2019年6月10日 下午10:04:18
 */
public class Client4Delimiter {

	//处理请求和处理服务端响应的线程组
	private EventLoopGroup group = null;
	//客户端服务启动相关配置信息
	//ServerBootstrap是服务端启动配置信息
	private Bootstrap bootstrap = null;
	
	public Client4Delimiter(){
		init();
	}
	
	public void init(){
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		//绑定线程组
		bootstrap.group(group);
		//设定通讯模式为NIO
		bootstrap.channel(NioSocketChannel.class);
	}
	public ChannelFuture doRequest(String host,int port) throws Exception{
		//客户端的Bootstrap没有ChildHandler方法，只有handler方法
		//方法含义等同ServerBootstrap中的childHandler
		//客户端必须绑定处理器，也就是必须调用handler方法
		//服务器必须绑定处理器，必须调用childerHandler方法
		this.bootstrap.handler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception{
				//数据分隔符
				ByteBuf delimiter = Unpooled.copiedBuffer("$E$".getBytes("utf8"));
				ChannelHandler[] handlers = new ChannelHandler[3];
				handlers[0] = new DelimiterBasedFrameDecoder(1024, delimiter);
				handlers[1] = new StringDecoder(Charset.forName("utf-8"));
				handlers[2] = new Client4DelimiterHandler();
				ch.pipeline().addLast(handlers);
			}
		});
		//建立链接
		ChannelFuture future = this.bootstrap.connect(host,port).sync();
		return future;
	}
	public void release(){
		this.group.shutdownGracefully();
	}
	public static void main(String[] args) {
		Client4Delimiter client = null;
		ChannelFuture future = null;
		Scanner s = null;
		try{
			client = new Client4Delimiter();
			future = client.doRequest("localhost", 9999);
			
			while(true){
				s = new Scanner(System.in);
				System.out.println("enter message send to sever(enter 'exit' for close client)");
				String line = s.nextLine();
				if("exit".equals(line)){
					//addListener - 增加监听，当某条件满足的时候，触发监听器
					//ChannelFutureListener.CLOSE - 关闭监听器，代表ChannelFutrue执行返回后，关闭链接。
					future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")))
						.addListener(ChannelFutureListener.CLOSE);
					break;
				}
				//Unpooled是一个工具类，会返回一个ByteBuf（不是ByteBuffer）
				future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")));
				TimeUnit.SECONDS.sleep(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			s.close();
			if(null != future){
				try {
					future.channel().closeFuture().sync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(null != client){
				client.release();
			}
		}
	}
}

class Client4DelimiterHandler extends ChannelHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg){
		try{
			String message = msg.toString();
			System.out.println("from server:" + message);
		}finally{
			//用于释放缓存，避免内存溢出
			ReferenceCountUtil.release(msg);
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		System.out.println("exceptionCaught method run...");
		ctx.close();
	}
}

















