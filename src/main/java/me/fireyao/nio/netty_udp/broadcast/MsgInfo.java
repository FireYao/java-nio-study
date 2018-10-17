package me.fireyao.nio.netty_udp.broadcast;

import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author liuliyuan
 * @date 2018/10/17 14:16
 * @Description: 消息
 */
public class MsgInfo {
    public static final byte SEPARATOR = (byte) ':';
    /*源的 InetSocketAddress*/
    private final InetSocketAddress source;
    /*消息内容*/
    private final String msg;
    /*消息id*/
    private final long msgId;
    /*消息发送或者接受的时间*/
    private final long time;

    //用于传入消息的构造函数
    public MsgInfo(String msg) {
        this(null, msg, -1, System.currentTimeMillis());
    }

    //用于传出消息的构造函数
    public MsgInfo(InetSocketAddress source, long msgId,
                   String msg) {
        this(source, msg, msgId, System.currentTimeMillis());
    }

    public MsgInfo(InetSocketAddress source, String msg, long msgId, long time) {
        this.source = source;
        this.msg = msg;
        this.msgId = msgId;
        this.time = time;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getMsg() {
        return msg;
    }

    public byte[] getMsgByte() {
        return msg.getBytes(CharsetUtil.UTF_8);
    }

    public long getMsgId() {
        return msgId;
    }

    public long getTime() {
        return time;
    }

    public int getMsgCap() {
        //两个long型msgId和time+分隔符SEPARATOR+msg消息内容的长度
        return 8 * 2 + 1 + getMsgByte().length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getTime());
        builder.append(" [");
        builder.append(getSource().toString());
        builder.append("] ：[");
        builder.append(getMsgId());
        builder.append("] ：");
        builder.append(getMsg());
        return builder.toString();
    }
}
