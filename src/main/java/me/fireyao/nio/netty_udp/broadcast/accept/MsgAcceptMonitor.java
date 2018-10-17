package me.fireyao.nio.netty_udp.broadcast.accept;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import me.fireyao.nio.HostConst;

import java.net.InetSocketAddress;

/**
 * @author liuliyuan
 * @date 2018/10/17 14:41
 * @Description:
 */
public class MsgAcceptMonitor {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;


    public MsgAcceptMonitor(InetSocketAddress address) {

        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                //设置套接字选项 SO_BROADCAST
                .option(ChannelOption.SO_BROADCAST, Boolean.TRUE)
                //允许端口重用
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new MsgAcceptDecoder());
                        ch.pipeline().addLast(new MsgAcceptHandler());
                    }
                })
                .localAddress(address);
    }

    public void start() throws InterruptedException {
        try {
            ChannelFuture future = bootstrap.bind().syncUninterruptibly();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MsgAcceptMonitor monitor = new MsgAcceptMonitor(
                new InetSocketAddress(HostConst.DEFAULT_PORT)
        );

        monitor.start();

    }
}
