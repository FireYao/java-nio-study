package me.fireyao.nio.netty_udp.broadcast.accept;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.fireyao.nio.netty_udp.broadcast.MsgInfo;

/**
 * @author liuliyuan
 * @date 2018/10/17 14:51
 * @Description:
 */
public class MsgAcceptHandler extends SimpleChannelInboundHandler<MsgInfo> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgInfo msg) throws Exception {
        System.out.println(msg.toString());
    }
}
