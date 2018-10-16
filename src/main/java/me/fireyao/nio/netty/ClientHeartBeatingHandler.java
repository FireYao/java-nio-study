package me.fireyao.nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuliyuan
 * @date 2018/10/12 15:50
 * @Description:
 */
public class ClientHeartBeatingHandler extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("hb_request",
            CharsetUtil.UTF_8));

    private AtomicInteger free_count = new AtomicInteger(0);
    private AtomicInteger send_count = new AtomicInteger(0);
    private AtomicInteger f_count = new AtomicInteger(0);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("build connect " + LocalDateTime.now());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("close connect " + LocalDateTime.now());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("循环请求的时间：" + LocalDateTime.now() + "，次数" + f_count.incrementAndGet());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(stateEvent.state())) {
                if (free_count.incrementAndGet() <= 3) {
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                } else {
                    System.out.println("不再发送心跳");
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
