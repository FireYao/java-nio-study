package me.fireyao.nio.netty_udp.unicast;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.Random;

/**
 * @author liuliyuan
 * @date 2018/10/17 10:58
 * @Description:
 */
public class AnswerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final String[] DICTIONARY = {
            "只要功夫深，铁棒磨成针。",
            "旧时王谢堂前燕,飞入寻常百姓家。",
            "洛阳亲友如相问，一片冰心在玉壶。",
            "一寸光阴一寸金，寸金难买寸光阴。",
            "老骥伏枥，志在千里，烈士暮年，壮心不已"};
    private static Random r = new Random();

    private String nextQuote() {
        return DICTIONARY[r.nextInt(DICTIONARY.length - 1)];
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf byteBuf = msg.content();
        String content = byteBuf.toString(CharsetUtil.UTF_8);

        System.out.println(content);

        if (Question.QUESTION.equals(content))
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(Answer.ANSWER + nextQuote(), CharsetUtil.UTF_8), msg.sender()));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
