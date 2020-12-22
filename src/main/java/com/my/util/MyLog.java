package com.my.util;


import net.mamoe.mirai.message.data.MessageChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLog {
    final static Logger logger = LoggerFactory.getLogger("MYLOG");

    public static void accept(long sender, MessageChain chain, String matched) {
        logger.info("FROM: {}, MESSAGE: {}, MATCHED: {}", sender, chain.contentToString(), matched);
    }

    public static void reply(MessageChain chain) {
        logger.info(chain.contentToString());
    }

    public static void failed(String matched, String info) {
        logger.error("MATCHED: {}, INFO: {}", matched, info);
    }

    public static void failed(String info) {
        logger.error(info);
    }

    public static void info(String text) {
        logger.info(text);
    }

    public static void error(Exception e) {
        logger.error(e.toString());
    }
}
