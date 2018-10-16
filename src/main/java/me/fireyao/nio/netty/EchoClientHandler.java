package me.fireyao.nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import me.fireyao.nio.HostConst;

/**
 * @author liuliyuan
 * @date 2018/10/10 14:05
 * @Description:
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 当服务端返回应答消息时，该方法被调用
     *
     * @param channelHandlerContext
     * @param byteBuf               接收到的服务端应答消息
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("Client accept : " + byteBuf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 当客户端和服务端TCP链路建立成功后，Netty的NIO线程会调用该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.write(Unpooled.copiedBuffer("hello netty" + HostConst.DELIMITER_STR, CharsetUtil.UTF_8));
            ctx.flush();
        }
    }

    /**
     * 发生异常时调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

