package me.fireyao.nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import me.fireyao.nio.HostConst;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author liuliyuan
 * @date 2018/10/10 14:00
 * @Description:
 */
public class EchoClient {

    private int port;
    private String host;

    public EchoClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        //创建客户端NIO线程组，处理网络事件，实际就是Reactor的线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建客户端辅助启动类
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)//启动类配置线程组
                    .remoteAddress(new InetSocketAddress(host, port))//远程服务器地址
                    .channel(NioSocketChannel.class)//配置Channel类型为NioSocketChannel
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(0, 3, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new ClientHeartBeatingHandler());
                            pipeline.addLast(new DelimiterBasedFrameDecoder(1024, HostConst.DELIMITER));
//                            pipeline.addLast(new LineBasedFrameDecoder(1024));
                            pipeline.addLast(new EchoClientHandler());
                        }
                    });//绑定I/O事件的处理器

            //辅助启动类配置完成后，发起异步链接，然后调用同步方法等待连接成功
            ChannelFuture future = bootstrap.connect().sync();
            //阻塞等待客户端链路关闭
            future.channel().closeFuture().sync();
        } finally {
            //线程组优雅的关闭，并释放相关资源
            group.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) {
        EchoClient client = new EchoClient(HostConst.DEFAULT_PORT, HostConst.DEFAULT_SERVER_IP);
        try {
            client.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
