package com.org.emaildispatcher.testtree;

import java.util.*;

public class MessageTree {
    private TreeMap<Integer, Integer> messageTreeMap; // 存储分钟数为键，消息等级为值

    public MessageTree() {
        messageTreeMap = new TreeMap<>();
    }

    // 插入分钟等级
    public void addMessage(int minutes, int level) {
        messageTreeMap.put(minutes, level);
    }

    // 查找最接近的分钟等级
    public Integer getClosestLevel(long timestamp) {
        // 获取当前时间戳，并计算差值
        long currentTimestamp = System.currentTimeMillis() / 1000; // 当前时间戳（秒）
        long diffInSeconds = Math.abs(timestamp - currentTimestamp);
        int diffInMinutes = (int) (diffInSeconds / 60); // 转换为分钟
        System.out.println("换算成分钟：" + diffInMinutes);

        // 查找最接近的分钟数
        Map.Entry<Integer, Integer> floorEntry = messageTreeMap.floorEntry(diffInMinutes);
        Map.Entry<Integer, Integer> ceilingEntry = messageTreeMap.ceilingEntry(diffInMinutes);

        // 如果同时找到了floorEntry和ceilingEntry
        if (floorEntry != null && ceilingEntry != null) {
            int floorDiff = Math.abs(floorEntry.getKey() - diffInMinutes);
            int ceilingDiff = Math.abs(ceilingEntry.getKey() - diffInMinutes);

            // 返回最接近的消息等级
            if (floorDiff <= ceilingDiff) {
                return floorEntry.getValue();
            } else {
                return ceilingEntry.getValue();
            }
        } else if (floorEntry != null) {
            return floorEntry.getValue();
        } else if (ceilingEntry != null) {
            return ceilingEntry.getValue();
        }

        return null; // 如果没有找到最接近的分钟等级
    }

    // 打印所有存储的分钟和等级
    public void printAllLevels() {
        for (Map.Entry<Integer, Integer> entry : messageTreeMap.entrySet()) {
            System.out.println("Minutes: " + entry.getKey() + "min, Level: " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        MessageTree messageTree = new MessageTree();

        // 插入一些分钟和对应的消息等级
        messageTree.addMessage(1, 5);  // 1分钟，等级 5
        messageTree.addMessage(2, 6);  // 2分钟，等级 6
        messageTree.addMessage(3, 7);  // 3分钟，等级 7
        messageTree.addMessage(4, 8); //  4分钟，等级 8
        messageTree.addMessage(5, 9); //  5分钟，等级 9
        messageTree.addMessage(6, 10); //  6分钟，等级 10
        messageTree.addMessage(7, 11); //  7分钟，等级 11
        messageTree.addMessage(8, 12); //  8分钟，等级 12
        messageTree.addMessage(9, 13); //  9分钟，等级 13
        messageTree.addMessage(10, 14); // 10分钟，等级 14
        messageTree.addMessage(20, 15); // 20分钟，等级 15
        messageTree.addMessage(30, 16); // 30分钟，等级 16
        messageTree.addMessage(60, 17); // 60分钟，等级 17
        messageTree.addMessage(120, 18); //120分钟，等级 18


        // 查询最接近的分钟等级
        System.out.println("当前时间戳:" + System.currentTimeMillis());
        long queryTimestamp = System.currentTimeMillis() / 1000 + 50 * 60; // 当前时间加上 3 分钟

        System.out.println("加上三分钟后:" + queryTimestamp);
        Integer closestLevel = messageTree.getClosestLevel(queryTimestamp);
        System.out.println("最接近的等级: " + closestLevel);
    }
}

