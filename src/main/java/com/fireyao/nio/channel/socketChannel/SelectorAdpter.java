package com.fireyao.nio.channel.socketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author liuliyuan
 * @date 2018/4/10 10:19
 * @Description:
 */
public class SelectorAdpter {

    private static final int BUF_SIZE = 1024;
    private static final int PORT = 5500;
    private static final int TIMEOUT = 3000;


    public static void selector() {
        Selector selector = null;
        ServerSocketChannel ssc = null;
        try {
            //创建Selector实例
            selector = Selector.open();
            //打开ServerSocketChannel
            ssc = ServerSocketChannel.open();
            //绑定端口
            ssc.socket().bind(new InetSocketAddress(PORT));
            //channel设置为非阻塞式
            ssc.configureBlocking(false);
            System.out.println("服务器正常启动。。。。");
            //注册selector
            /**
             * SelectionKey {
                Connect 连接就绪
                Accept 接受就绪
                Read 读就绪
                Write 写就绪
             * }
             */
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                if (selector.select(TIMEOUT) == 0) {
                    System.out.println("==");
                    continue;
                }
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable() && key.isValid()) {
                        handleWrite(key);
                    }
                    if (key.isConnectable()) {
                        System.out.println("isConnectable = true");
                    }
                    iter.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    selector.close();
                }
                if (ssc != null) {
                    ssc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
    }

    public static void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        long bytesRead = sc.read(buf);
        while (bytesRead > 0) {
            buf.flip();
            StringBuffer sb = new StringBuffer();
            while (buf.hasRemaining()) {
                sb.append((char) buf.get());
            }
            buf.clear();
            bytesRead = sc.read(buf);
            System.out.println(sb.toString());
        }
        if (bytesRead == -1) {
            sc.close();
        }
    }

    public static void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        while (buf.hasRemaining()) {
            sc.write(buf);
        }
        buf.compact();
    }

}
