package com.org.emaildispatcher.handler;

import com.org.emaildispatcher.util.RedisUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private RedisUtil redisUtil;
    public NettyServerInitializer(RedisUtil redisUtil){
        this.redisUtil = redisUtil;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        log.info("已经获取到连接{}", socketChannel.remoteAddress());
        socketChannel.pipeline().addLast(new HttpServerCodec()); //http的编解码
        socketChannel.pipeline().addLast(new HttpRequestHandler(redisUtil));
    }
}
