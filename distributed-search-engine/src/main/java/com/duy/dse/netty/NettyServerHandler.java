package com.duy.dse.netty;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * rpc服务器处理接受到的请求。抽象类，需要自己提供具体的业务实现方法
 */
@Component
@Sharable
public abstract class NettyServerHandler extends ChannelInboundHandlerAdapter {
	  /**
     * 本方法用于读取客户端发送的信息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SimpleServerHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;
        byte[] resultByte = new byte[result.readableBytes()];
        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
        result.readBytes(resultByte);
        String resultStr = new String(resultByte);
        // 接收并打印客户端的信息
        System.out.println("Client said:" + resultStr);
        // 释放资源
        result.release();
        //向客户端返回处理结果
        String dealResult = this.dealService(resultStr);
        // 在当前场景下，发送的数据必须转换成ByteBuf数组
        ByteBuf encoded = ctx.alloc().buffer(4 * dealResult.length());
        encoded.writeBytes(dealResult.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }
    
    /**
     * 业务处理方法
     * @param resultStr 客户端传入的参数
     * @return 处理的结果
     */
    protected abstract String dealService(String resultStr);
 
    /**
     * 本方法用作处理异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
 
    /**
     * 信息获取完毕后操作
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}