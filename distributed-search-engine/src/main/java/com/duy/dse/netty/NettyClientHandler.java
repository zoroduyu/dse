package com.duy.dse.netty;
 
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * netty客户端用于发送和接受返回消息的适配器类
 * @author duyu
 *
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
	
	/**
	 * 客户端要发送的信息（索引的报文）
	 */
	protected String msg;
	
	/**
	 * 要发送到的ip，主要用于失败后做处理
	 */
	protected String ip;
	
	public NettyClientHandler(String msg, String ip) {
		super();
		this.msg = msg;
		this.ip = ip;
	}

	/**
     * 本方法用于接收服务端发送过来的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        return;
    }
    
    /**
     * 本方法用于处理异常
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
     * 本方法用于向服务端发送信息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
        encoded.writeBytes(msg.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }
}
