package me.fireyao.nio.netty_http.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import me.fireyao.nio.netty_http.RespConstant;


/**
 * @author liuliyuan
 * @date 2018/10/15 10:15
 * @Description:
 */
public class HttpServerRequestHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        try {
            handleRequest(ctx, request);
        } catch (Exception e) {
            response(ctx, "内部错误", HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }finally {
            ReferenceCountUtil.release(request);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("连接的客户端地址:"
                + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        String response;
        String uri = request.uri();
        String body = request.content().toString(CharsetUtil.UTF_8);
        System.out.println("body:" + body + ", uri: " + uri);
        if ("/502".equalsIgnoreCase(uri)) {
            response(ctx, "502 bad getway", HttpResponseStatus.BAD_GATEWAY);
        }

        if ("/404".equalsIgnoreCase(uri)) {
            response(ctx, "404 NOT FOUND", HttpResponseStatus.NOT_FOUND);
            return;
        }

        if ("/bad".equalsIgnoreCase(uri)) {
            response = "非法请求:" + uri;
            response(ctx, response, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        HttpMethod method = request.method();
        if ("/get".equalsIgnoreCase(uri)) {
            if (HttpMethod.GET.equals(method)) {
                response = RespConstant.getNews();
                response(ctx, response, HttpResponseStatus.OK);
            } else {
                response(ctx, "method must be GET request ", HttpResponseStatus.METHOD_NOT_ALLOWED);
            }
            return;
        }

        if ("/post".equalsIgnoreCase(uri)) {
            if (HttpMethod.POST.equals(method)) {
                response = RespConstant.getNews();
                response(ctx, response, HttpResponseStatus.OK);
            } else {
                response(ctx, "method must be POST request ", HttpResponseStatus.METHOD_NOT_ALLOWED);
            }
            return;
        }

        response(ctx, "404 NOT FOUND", HttpResponseStatus.NOT_FOUND);
    }


    private void response(ChannelHandlerContext ctx, String resp, HttpResponseStatus status) {
        System.out.println("Http status:" + status);
        ByteBuf buf = Unpooled.copiedBuffer(resp, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                "text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
