package com.org.emaildispatcher.netty;

import com.org.emaildispatcher.handler.NettyServerInitializer;
import com.org.emaildispatcher.util.RedisUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class NettyServer {

    @Resource
    private RedisUtil redisUtil;

    public void start(InetSocketAddress addressConfig){
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, workerGroup) //绑定处理线程池
                    .channel(NioServerSocketChannel.class)
                    .localAddress(addressConfig)
                    .childHandler(new NettyServerInitializer(redisUtil)) //handler的初始化
                    .option(ChannelOption.SO_BACKLOG, 256); //设定连接长度

            ChannelFuture future = bootstrap.bind(addressConfig).sync();
            log.info("开始监听端口" + addressConfig.getPort());
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
            log.error("服务端连接异常断开");
            boss.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
