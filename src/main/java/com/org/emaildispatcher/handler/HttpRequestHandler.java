package com.org.emaildispatcher.handler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.org.emaildispatcher.brige.BufferQueue;
import com.org.emaildispatcher.centralcontrol.EmailTaskChecker;
import com.org.emaildispatcher.model.BufferData;
import com.org.emaildispatcher.model.EmailRedisModel;
import com.org.emaildispatcher.util.RedisUtil;
import com.org.emaildispatcher.util.TimestampToDate;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private RedisUtil redisUtil;
    public HttpRequestHandler(RedisUtil redisUtil){
        this.redisUtil = redisUtil;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {

        log.info("请求的地址{}", msg.uri());
        if (msg.uri().equals("/favicon.ico")){
            return;
        }

        QueryStringDecoder decoder = new QueryStringDecoder(msg.uri());
        Map<String, List<String>> parameters = decoder.parameters();
        String redisKey = parameters.get("redisKey").get(0);

        log.info("请求路径:{}",redisKey);

        EmailRedisModel emailTask = getRedisTask(redisKey);

        byte[] bytes = "<h1>OK!</h1>".getBytes();;
        //检查邮件是否完整
        if (!EmailTaskChecker.checkEmail(emailTask)) {
            //说明邮件有不完整的地方
            log.error("{}邮件不完整", redisKey);
            bytes = "<h1>No!</h1>".getBytes();
            return;
        }else {
            //检查邮件的时间戳是否小于当前时间戳，如果小于当前时间则投递到redis的zset定时任务集合中
            long nowTime = System.currentTimeMillis() / 1000; //秒级
            long timer = Long.parseLong(emailTask.getTime());


            if (checkTime(nowTime, timer)) { //当前时间小于定时时间
                //投递给redis zset定时队列
                log.info("定时任务已开启，将在{}后投递到消息队列中", TimestampToDate.toTime(timer));
                redisUtil.addTimerTask(redisKey, timer);
            } else {
                //异步的提交
                CompletableFuture.runAsync(() -> {
                    BufferData bufferData = new BufferData(redisKey);
                    BufferQueue.putData(bufferData);
                });
            }
        }
        //返回响应
        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
        response.headers().setInt(CONTENT_LENGTH, bytes.length);
        response.content().writeBytes(bytes);
        ctx.writeAndFlush(response);
    }

    public EmailRedisModel getRedisTask(String key){
        Object value = redisUtil.getEmailTask(key);
        String jsonString = JSON.toJSONString(value);
        EmailRedisModel emailRedisModel = JSONObject.parseObject(jsonString, EmailRedisModel.class);
        log.info("转换成功{}", emailRedisModel);
        return emailRedisModel;
    }

    /**
     * 检查当前时间是否小于定时时间
     * @param nowTime
     * @param timer
     * @return
     */
    private boolean checkTime(long nowTime, long timer){
        if (nowTime < timer){
            return true;
        }
        return false;
    }

}
