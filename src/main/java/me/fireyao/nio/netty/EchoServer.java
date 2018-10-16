package me.fireyao.nio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import me.fireyao.nio.HostConst;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author liuliyuan
 * @date 2018/10/10 10:08
 * @Description:
 */
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        //创建服务端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建服务端辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //创建I/O事件处理器实例
            bootstrap.group(group)//配置线程组
                    .localAddress(new InetSocketAddress(port))//配置服务器端口号
                    .channel(NioServerSocketChannel.class)//设置创建的channel类型为NioServerSocketChannel
                    //绑定I/O事件的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IdleStateHandler(4, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new ServerHeartBeatingHandler());
                            pipeline.addLast(new DelimiterBasedFrameDecoder(1024, HostConst.DELIMITER));
//                            pipeline.addLast(new LineBasedFrameDecoder(1024));
                            pipeline.addLast(new EchoServerHandler());
                        }
                    });
            //服务端启动类配置完成后，绑定端口，调用同步阻塞方法sync()等待绑定操作完成。
            ChannelFuture future = bootstrap.bind().sync();
            //阻塞等待服务端链路关闭
            future.channel().closeFuture().sync();
        } finally {
            //优雅的关闭线程组，释放相关资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        EchoServer server = new EchoServer(HostConst.DEFAULT_PORT);
        try {
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
