package me.fireyao.nio.netty_http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import me.fireyao.nio.HostConst;
import me.fireyao.nio.netty_http.server.HttpServer;

import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author liuliyuan
 * @date 2018/10/15 10:50
 * @Description:
 */
public class HttpClient {

    private int port;

    private String ip;


    private EventLoopGroup group;

    private Bootstrap bootstrap;

    //传递channelHandler中收到的http响应
    private BlockingQueue<HttpRespInfo> queue;

    public HttpClient(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    private ChannelFuture connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast("aggre", new HttpObjectAggregator(HttpServer.MAX_REQUEST_SIZE));
                        ch.pipeline().addLast("decompressor", new HttpContentDecompressor());
                        queue = new ArrayBlockingQueue(1);
                        pipeline.addLast(new HttpClientInBoundHandler(queue));
                    }
                });
        return bootstrap.connect(ip, port).sync();
    }

    public HttpRespInfo doRequest(String body, String uri, HttpMethod method) throws Exception {
        try {
            ChannelFuture future = connect();
            Channel channel = future.channel();
            URI u = new URI(uri);
            DefaultFullHttpRequest request =
                    new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                            method,
                            u.toASCIIString(),
                            Unpooled.wrappedBuffer(body.getBytes("UTF-8")));

            request.headers().set(HttpHeaderNames.HOST, ip);
            request.headers()
                    .set(HttpHeaderNames.CONNECTION,
                            HttpHeaderValues.KEEP_ALIVE);
            request.headers()
                    .set(HttpHeaderNames.CONTENT_LENGTH,
                            request.content().readableBytes());

            channel.writeAndFlush(request);
            HttpRespInfo take = queue.take();
            channel.closeFuture().sync();
            return take;
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient(HostConst.DEFAULT_PORT, HostConst.DEFAULT_SERVER_IP);
        HttpRespInfo response = client.doRequest("hello server,I'm get request ", "/get", HttpMethod.GET);
        System.out.println(response);

        HttpRespInfo notF = client.doRequest("hello server,I'm 404 request ", "/asdasd", HttpMethod.GET);
        System.out.println(notF);

        HttpRespInfo post = client.doRequest("hellow server, I'm post request ", "/post", HttpMethod.POST);
        System.out.println(post);

        HttpRespInfo badReq = client.doRequest("hellow server, I'm bad request ", "/post", HttpMethod.GET);
        System.out.println(badReq);

        HttpRespInfo badGw = client.doRequest("hellow server, I'm bad getWay ", "/502", HttpMethod.GET);
        System.out.println(badGw);

    }

}
