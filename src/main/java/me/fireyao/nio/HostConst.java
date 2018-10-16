package me.fireyao.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

public class HostConst {
    //服务器端口号
    public static int DEFAULT_PORT = 1133;
    public static String DEFAULT_SERVER_IP = "127.0.0.1";
    public final static String DELIMITER_STR = "$_";
    public final static ByteBuf DELIMITER = Unpooled.copiedBuffer(DELIMITER_STR.getBytes());


    public static void main(String[] args) {

        System.out.println(Unpooled.copiedBuffer("汉", CharsetUtil.US_ASCII).capacity());
        System.out.println(Unpooled.copiedBuffer("汉", Charset.forName("GBK")).capacity());
        System.out.println(Unpooled.copiedBuffer("汉", CharsetUtil.UTF_8).capacity());
        System.out.println(Unpooled.copiedBuffer("汉", CharsetUtil.UTF_16).capacity());

    }
}
