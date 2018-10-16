package me.fireyao.nio.netty_http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import me.fireyao.nio.HostConst;


/**
 * @author liuliyuan
 * @date 2018/10/15 10:07
 * @Description:
 */
public class HttpServer {

    public final static int MAX_REQUEST_SIZE = 1024 * 1024 * 2;
    private int port;
    private EventLoopGroup group;
    private ServerBootstrap bootstrap;

    public HttpServer(int port, EventLoopGroup group, ServerBootstrap bootstrap) {
        this.port = port;
        this.group = group;
        this.bootstrap = bootstrap;
    }

    public void start() throws InterruptedException {

        try {
            bootstrap.group(group)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("encode", new HttpResponseEncoder());
                            pipeline.addLast("decode", new HttpRequestDecoder());
                            pipeline.addLast("aggre", new HttpObjectAggregator(MAX_REQUEST_SIZE));
                            ch.pipeline().addLast("compressor", new HttpContentCompressor());
                            pipeline.addLast(new HttpServerRequestHandler());
                        }
                    })
                    .channel(NioServerSocketChannel.class);

            System.out.println("服务器启动,port:" + port);
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        HttpServer server = new HttpServer(HostConst.DEFAULT_PORT, group, bootstrap);
        server.start();
    }

}
