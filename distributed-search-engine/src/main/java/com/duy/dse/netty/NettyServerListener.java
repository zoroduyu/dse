package com.duy.dse.netty;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.duy.dse.config.NettyConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务启动监听器
 *
 * @author duyu
 */
@Component
public class NettyServerListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerListener.class);
    /**
     * 创建bootstrap
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    /**
     * BOSS
     */
    EventLoopGroup boss = new NioEventLoopGroup();
    /**
     * Worker
     */
    EventLoopGroup work = new NioEventLoopGroup();
    /**
     * 通道适配器
     */
    @Autowired
    private NettyServerHandlerService nettyServerHandlerService;
    /**
     * NETT服务器配置类
     */
    @Autowired
    private NettyConfig nettyConfig;

    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        LOGGER.info("关闭服务器....");
        //优雅退出
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

    /**
     * 开启及服务线程
     */
    public void start() {
        // 从配置文件中(application.yml)获取服务端监听端口号
        int port = nettyConfig.getPort();
        serverBootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024);
        try {
            //设置事件处理
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    //传入具体的业务处理类
                    pipeline.addLast(nettyServerHandlerService);
                }
            });
            LOGGER.info("netty服务器在[{}]端口启动监听", port);
            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.info("[出现异常] 释放资源");
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}