package com.mashibing.netty;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class HelloNetty {
    public static void main(String[] args) {
        new NettyServer(8888).serverStart();
    }
}

class NettyServer {

    int port = 8888;

    public NettyServer(int port) {
        this.port = port;
    }
    public void serverStart() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        ChannelFuture f = null;
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_RCVBUF, 1024*10)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                    	System.out.println("test1");
                        ch.pipeline().addLast(new Handler());
                    }
                });

        try {
            f = b.bind(port).sync();
        	System.out.println("test2");

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        	if(f != null){
        		try {
					f.channel().closeFuture().sync();
					System.out.println("f is close");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }
}

@Sharable
class Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        System.out.println("server: channel read");
        ByteBuf buf = (ByteBuf)msg;
        Thread.sleep(100);
        System.out.println(buf.toString(CharsetUtil.UTF_8));
        System.out.println(new Date().getTime());
        System.out.println(this.toString());

        ctx.writeAndFlush(Unpooled.copiedBuffer("sdf".getBytes("utf-8")));
//        ctx.writeAndFlush(msg);

        ctx.close();

//        buf.release();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
