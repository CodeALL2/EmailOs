package com.org.emaildispatcher.brige;

import com.org.emaildispatcher.model.BufferData;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 此类为邮件缓冲区，防止邮件过多对线程池的冲击
 */
@Slf4j
public class BufferQueue {

    private static final ArrayBlockingQueue<BufferData> bufferQueue = new ArrayBlockingQueue<BufferData>(50);

    /**
     * 将邮件推送到发送缓冲区
     * @param bufferData
     */
    public static void putData(BufferData bufferData){
        try {
            log.info("bufferQueue缓冲信息触发中:{}", bufferData);
            bufferQueue.put(bufferData);
        } catch (InterruptedException e) {
            log.info("bufferQueue缓冲信息触发异常,未进入触发队列");
            e.printStackTrace();
        }
    }

    /**
     * 将邮件从缓冲区中取出
     */
    public static BufferData getData(){
        BufferData bufferData = null;
        try {
            bufferData = bufferQueue.take();
            log.info("bufferQueue缓冲信息已触发成功: {}", bufferData);
        } catch (InterruptedException e) {
            log.error("bufferQueue缓冲信息取出异常");
        }
        return bufferData;
    }
}
