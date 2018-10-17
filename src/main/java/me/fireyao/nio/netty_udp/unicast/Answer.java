package me.fireyao.nio.netty_udp.unicast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import me.fireyao.nio.HostConst;

/**
 * @author liuliyuan
 * @date 2018/10/17 10:53
 * @Description:
 */
public class Answer {

    public final static String ANSWER = "古诗来了：";

    public static void start(int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new AnswerHandler());
                        }
                    });
            //没有接受客户端连接的过程，监听本地端口即可
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        try {
            Answer.start(HostConst.DEFAULT_PORT);
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

}
