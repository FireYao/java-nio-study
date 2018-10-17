package me.fireyao.nio.netty_udp.broadcast.send;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import me.fireyao.nio.HostConst;
import me.fireyao.nio.netty_udp.broadcast.MsgInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author liuliyuan
 * @date 2018/10/17 14:18
 * @Description: 广播端
 */
public class SendMsgBroadCaster {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;


    public SendMsgBroadCaster(InetSocketAddress remoteAddress) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        //引导该 NioDatagramChannel（无连接的）
        bootstrap.group(group).channel(NioDatagramChannel.class)
                //设置 SO_BROADCAST 套接字选项
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new SendMsgEncoder(remoteAddress));
                    }
                });
    }

    public void start() throws InterruptedException {
        ChannelFuture future = bootstrap.bind(0).sync();

        Channel channel = future.channel();
        int count = 0;
        while (true) {
            channel.writeAndFlush(new MsgInfo(null, ++count,
                    HostConst.getLogInfo()));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }


    public void stop() {
        group.shutdownGracefully();
    }


    public static void main(String[] args) throws Exception {
        SendMsgBroadCaster sender = new SendMsgBroadCaster(
                //表明本应用发送的报文并没有一个确定的目的地，也就是进行广播
                new InetSocketAddress("255.255.255.255", HostConst.DEFAULT_PORT));
        try {
            sender.start();
        } finally {
            sender.stop();

        }

    }


}
