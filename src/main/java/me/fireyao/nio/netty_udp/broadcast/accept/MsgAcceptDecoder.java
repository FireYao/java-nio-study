package me.fireyao.nio.netty_udp.broadcast.accept;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import me.fireyao.nio.netty_udp.broadcast.MsgInfo;

import java.util.List;

/**
 * @author liuliyuan
 * @date 2018/10/17 14:43
 * @Description:
 */
public class MsgAcceptDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {

        ByteBuf data = msg.content();

        long time = data.readLong();
        System.out.println("接受到" + time + "发送的消息");

        //消息Id
        long msgId = data.readLong();

        //分隔符
        byte sepa = data.readByte();

        //读取当前(分隔符)索引的位置
        int index = data.readerIndex();

        //从当前索引位置读到消息最后，就是消息内容
        String content = data.slice(index, data.readableBytes()).toString(CharsetUtil.UTF_8);

        MsgInfo msgInfo = new MsgInfo(msg.sender(), content, msgId, time);

        out.add(msgInfo);
    }
}
