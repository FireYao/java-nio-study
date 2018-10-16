package me.fireyao.nio.netty_http.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.concurrent.BlockingQueue;

/**
 * @author liuliyuan
 * @date 2018/10/15 11:02
 * @Description:
 */
public class HttpClientInBoundHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private BlockingQueue<HttpRespInfo> queue;

    public HttpClientInBoundHandler(BlockingQueue<HttpRespInfo> queue) {
        super();
        this.queue = queue;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        try {
            queue.add(HttpRespInfo.convert(response));
        } catch (Exception e) {
            ctx.close();
        }
    }
}
