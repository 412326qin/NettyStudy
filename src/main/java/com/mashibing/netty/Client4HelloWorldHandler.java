package com.mashibing.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class Client4HelloWorldHandler extends ChannelInboundHandlerAdapter{
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		try{
			ByteBuf readBuffer = (ByteBuf)msg;
			byte[]tempDatas = new byte[readBuffer.readableBytes()];
			readBuffer.readBytes(tempDatas);
			System.out.println("from sever: "+ new String(tempDatas,"utf-8"));
		}finally{
			//用于释放缓存，避免内存溢出
			ReferenceCountUtil.release(msg);
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		System.out.println("client exceptionCaught method run...");
//		cause.printStackTrace();
		ctx.close();
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		System.out.println("channelInactive method run..");
		ctx.close();
	}
}
