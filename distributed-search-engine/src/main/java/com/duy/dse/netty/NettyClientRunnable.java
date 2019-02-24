package com.duy.dse.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @author duyu
 *
 */
public class NettyClientRunnable implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientRunnable.class);
    /**
     * 初始化Bootstrap实例， 此实例是netty客户端应用开发的入口
     */
    private Bootstrap bootstrap;
    /**
     * 工人线程组
     */
    private EventLoopGroup worker;
    /**
     * 远程端口
     */
    private int port;
    /**
     * 远程服务器ip
     */
    private String ip;
    
    /**
     * 要同步的索引报文
     */
    private String msg;

    public NettyClientRunnable(int port, String ip,String msg) {
        this.port = port;
        this.ip = ip;
        bootstrap = new Bootstrap();
        worker = new NioEventLoopGroup();
        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        this.msg = msg;
    }

	@Override
	public void run() {
		  LOGGER.info("{} -> [启动连接] {}:{}", this.getClass().getName(), ip, port);
	        bootstrap.handler(new NettyClientHandler(msg, ip));
	        ChannelFuture f = bootstrap.connect(ip, port);
	        try {
	            f.channel().closeFuture().sync();
	        } catch (Exception e) {
	        	LOGGER.info("通知服务器{}失败，报错{}",ip,e);
	        }
	}

}
