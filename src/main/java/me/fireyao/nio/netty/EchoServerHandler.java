package me.fireyao.nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import me.fireyao.nio.HostConst;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuliyuan
 * @date 2018/10/10 10:09
 * @Description:
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * 每个信息入站时调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将msg转换成Netty的ByteBuf对象
        ByteBuf in = (ByteBuf) msg;
        String rec = in.toString(CharsetUtil.UTF_8);
        System.out.println("Server accept :" + rec + ":" + counter.incrementAndGet());
        //异步发送应答消息给客户端,write方法只是把待发送的消息发送到缓冲区。真正发送需要调用flush，将缓冲区的消息全部写到SocketChannel中。
        ctx.writeAndFlush(Unpooled.copiedBuffer(rec + HostConst.DELIMITER_STR, CharsetUtil.UTF_8));
    }

    /**
     * 捕获到异常时调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //关闭ChannelHandlerContext，释放ChannelHandlerContext相关联的句柄等资源
        ctx.close();
    }
}
