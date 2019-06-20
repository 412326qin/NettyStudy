package com.mashibing.netty;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

class ClientSelf{
	
	private EventLoopGroup group = null;
	private Bootstrap bootstrap = null;
	
	public ClientSelf(){
		init();
	}
	
	public void init(){
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(group);
	}
	
	public ChannelFuture doRequest(String host, int port, final ChannelHandler... handlers) throws Exception{
		bootstrap.handler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch){
				ch.pipeline().addLast(handlers);
			}
		});
		ChannelFuture future = bootstrap.connect(host, port).sync();
		System.out.println("start connect...");
		return future;
	}
	
	public void release(){
		this.group.shutdownGracefully();
	}
	
	public static void main(String[] args) {
		ClientSelf client = null;
		ChannelFuture future = null;
		Scanner scan = new Scanner(System.in);
		try{
			client = new ClientSelf();
			future = client.doRequest("localhost",9999,new ClientSelfHandler());
			while(true){
				String line = scan.nextLine();
				if("exit".equals(line)){
					future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")))
							.addListener(ChannelFutureListener.CLOSE);
					System.out.println("i want exit");
					break;
				}
				future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8"))).sync();
				TimeUnit.SECONDS.sleep(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			scan.close();
			if(future != null){
				try{
					future.channel().closeFuture().sync();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(client != null){
				client.release();
			}
		}
	}
}

class ClientSelfHandler extends ChannelInboundHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		ByteBuf readBuf = (ByteBuf)msg;
		byte[] tempDatas = new byte[readBuf.readableBytes()];
		readBuf.readBytes(tempDatas);
		String message = new String(tempDatas, "utf-8");
		System.out.println("from client receive:" +message);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		System.out.println("exceptionCaught method run...");
		System.out.println(cause.toString());
		ctx.close();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx){
		System.out.println("this is before invoke...");
		ctx.close();
	}
}













