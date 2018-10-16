package me.fireyao.nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuliyuan
 * @date 2018/10/12 15:50
 * @Description:
 */
public class ServerHeartBeatingHandler extends ChannelInboundHandlerAdapter {

    private AtomicInteger free_count = new AtomicInteger(0);
    private AtomicInteger send_count = new AtomicInteger(0);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(stateEvent.state())) {
                System.out.println(" No data was received for a while");
                if (free_count.incrementAndGet() > 2) {
                    System.out.println("close this not active channel");
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        //如果是心跳命令
        String hb = buf.toString(CharsetUtil.UTF_8);
        if ("hb_request".equals(hb)) {
            System.out.println("第" + send_count.incrementAndGet() + "次收到心跳消息" + hb);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
