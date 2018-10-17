package me.fireyao.nio.netty_udp.unicast;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import me.fireyao.nio.HostConst;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author liuliyuan
 * @date 2018/10/17 10:53
 * @Description:
 */
public class Question {

    public final static String QUESTION = "告诉我一句古诗";


    public static void start(int port) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new QuestionHandler());
                        }
                    });
            //不需要建立连接
            ChannelFuture future = bootstrap.bind(0).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(QUESTION, CharsetUtil.UTF_8),
                    new InetSocketAddress(HostConst.DEFAULT_SERVER_IP, port)));
            channel.closeFuture().await(2, TimeUnit.SECONDS);
        } finally {
            group.shutdownGracefully().sync();
        }

    }


    public static void main(String[] args) {
        try {
            Question.start(HostConst.DEFAULT_PORT);
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

}
