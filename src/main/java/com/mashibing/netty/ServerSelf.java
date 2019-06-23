package com.mashibing.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

class ServerSelf{
	
	private EventLoopGroup acceptGroup;
	private EventLoopGroup receiveGroup;
	private ServerBootstrap bootstrap;
	
	public ServerSelf(){
		init();
	}
	
	public void init(){
		acceptGroup = new NioEventLoopGroup();
		receiveGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(acceptGroup, receiveGroup);
		bootstrap//.option(ChannelOption.SO_SNDBUF, 1024*10)
				.option(ChannelOption.SO_RCVBUF, 1024*10);
				//.option(ChannelOption.SO_KEEPALIVE, true);
	}
	
	public ChannelFuture doAccept(int port, final ChannelHandler... handlers)throws Exception{
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch){
				ch.pipeline().addLast(handlers);
			}
		});
		ChannelFuture future = bootstrap.bind(port).sync();
		return future;
	}
	
	public void release(){
		this.acceptGroup.shutdownGracefully();
		this.receiveGroup.shutdownGracefully();
	}
	
	public static void main(String[] args) {
		ServerSelf server = null;
		ChannelFuture future = null;
		try{
			server = new ServerSelf();
			future = server.doAccept(9999, new ServerSelfHandler());
			future.channel().closeFuture().sync();
			System.out.println("server is start...");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(future != null){
				try{
					future.channel().closeFuture().sync();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(server != null){
				server.release();
			}
		}
	}
	
}

@Sharable
class ServerSelfHandler extends ChannelInboundHandlerAdapter{
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		ByteBuf readBuf = (ByteBuf)msg;
		byte[] tempDatas = new byte[readBuf.readableBytes()];
		readBuf.readBytes(tempDatas);
		String line = new String(tempDatas, "utf-8");
		System.out.println("from client:"+line);
		if("exit".equals(line)){
			ctx.close();
			return;
		}
		String message = "i'm reveive:" + line;
		ctx.writeAndFlush(Unpooled.copiedBuffer(message.getBytes("utf-8")));
//		ctx.close();
	}
	
	@Override 
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		System.out.println("exception method run..");
		ctx.close();
	}
}












