package me.fireyao.nio.netty_http.client;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

/**
 * @author liuliyuan
 * @date 2018/10/15 15:05
 * @Description:
 */
public class HttpRespInfo {

    private String content;

    private HttpResponseStatus status;

    private HttpHeaders headers;

    public HttpRespInfo() {
    }

    public HttpRespInfo(String content, HttpResponseStatus status, HttpHeaders headers) {
        this.content = content;
        this.status = status;
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "HttpRespInfo{" +
                "content='" + content + '\'' +
                ", status=" + status +
                ", headers=" + headers +
                '}';
    }


    public static HttpRespInfo convert(FullHttpResponse response){
        return new HttpRespInfo(response.content().toString(CharsetUtil.UTF_8),response.status(),response.headers());
    }

}
