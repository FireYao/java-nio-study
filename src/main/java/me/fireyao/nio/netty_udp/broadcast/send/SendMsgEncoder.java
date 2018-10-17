package me.fireyao.nio.netty_udp.broadcast.send;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import me.fireyao.nio.netty_udp.broadcast.MsgInfo;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author liuliyuan
 * @date 2018/10/17 14:25
 * @Description: 将消息编码后广播
 */
public class SendMsgEncoder extends MessageToMessageEncoder<MsgInfo> {

    private final InetSocketAddress remoteAddress;

    //LogEventEncoder 创建了即将被发送到指定的 InetSocketAddress
    // 的 DatagramPacket 消息
    public SendMsgEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, MsgInfo msg, List<Object> out) throws Exception {
        byte[] bytes = msg.getMsgByte();

        ByteBuf buffer = ctx.alloc().buffer(msg.getMsgCap());
        //写入时间
        buffer.writeLong(msg.getTime());
        //写入消息id
        buffer.writeLong(msg.getMsgId());
        //分隔符
        buffer.writeByte(MsgInfo.SEPARATOR);
        //内容
        buffer.writeBytes(bytes);
        //将一个拥有数据和目的地地址的新 DatagramPacket 添加到出站的消息列表中
        ctx.writeAndFlush(new DatagramPacket(buffer, remoteAddress));

    }
}
