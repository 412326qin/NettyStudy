package com.mashibing.netty.protocol;

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
public class Client4Protocol {

	//处理请求和处理服务端响应的线程组
	private EventLoopGroup group = null;
	//客户端服务启动相关配置信息
	//ServerBootstrap是服务端启动配置信息
	private Bootstrap bootstrap = null;
	
	public Client4Protocol(){
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
	public ChannelFuture doRequest(String host,int port,final ChannelHandler... handlers) throws Exception{
		//客户端的Bootstrap没有ChildHandler方法，只有handler方法
		//方法含义等同ServerBootstrap中的childHandler
		//客户端必须绑定处理器，也就是必须调用handler方法
		//服务器必须绑定处理器，必须调用childerHandler方法
		this.bootstrap.handler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception{
				ch.pipeline().addLast(new StringDecoder(Charset.forName("utf-8")));
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
		Client4Protocol client = null;
		ChannelFuture future = null;
		Scanner s = null;
		try{
			client = new Client4Protocol();
			future = client.doRequest("localhost", 9999, new Client4ProtocolHandler());
			
			while(true){
				s = new Scanner(System.in);
				System.out.println("enter message send to sever >");
				String line = s.nextLine();
				line = Client4ProtocolHandler.ProtocolParser.transferTo(line);
				System.out.println("client send protocol content:" + line);
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

class Client4ProtocolHandler extends ChannelHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg){
		try{
			String message = msg.toString();
			System.out.println("client receive protocol content :" + message);
			message = ProtocolParser.parse(message);
			if(message == null){
				System.out.println("error response from server");
				return;
			}
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
	static class ProtocolParser{
		public static String parse(String message){
			String[] temp = message.split("HEADBODY");
			temp[0] = temp[0].substring(4);
			temp[1] = temp[1].substring(0, (temp[1].length()-4));
			int length = Integer.parseInt(temp[0].substring(temp[0].indexOf(":")+1));
			if(length != temp[1].length()){
				return null;
			}
			return temp[1];
		}
		public static String transferTo(String message){
			message = "HEADcontent-length:" + message.length() + "HEADBODY" + message + "BODY";
			return message;
		}
	}
}

















