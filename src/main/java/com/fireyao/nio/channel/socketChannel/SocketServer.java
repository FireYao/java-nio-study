package com.fireyao.nio.channel.socketChannel;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author liuliyuan
 * @date 2018/4/8 16:51
 * @Description:
 */
public class SocketServer {

    public static void main(String[] args) {
//        server();
//        nioServer();
        SelectorAdpter.selector();
    }


    /**
     * NIO server
     */
    public static void nioServer() {
        try {

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(5500));
            serverSocketChannel.configureBlocking(false);
            System.out.println("服务器正常启动。。。。");
            while (true) {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = serverSocketChannel.accept();
                    if (socketChannel != null) {
                        SocketAddress clientAddress = socketChannel.getRemoteAddress();
                        System.out.println("Handling client at " + clientAddress);
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        //读取的字节数
                        int read = socketChannel.read(byteBuffer);

                        while (read != -1) {
                            byteBuffer.flip();
                            StringBuffer sb = new StringBuffer();
                            while (byteBuffer.hasRemaining()) {
                                sb.append((char) byteBuffer.get());
                            }

                            byteBuffer.compact();
                            read = socketChannel.read(byteBuffer);
                            System.out.println(sb.toString());
                        }

                    }
                } catch (IOException e) {
                } finally {
                    if (socketChannel != null) {
                        socketChannel.close();
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 常规IO server
     */
    public static void server() {
        ServerSocket socket = null;
        InputStream in = null;
        try {
            socket = new ServerSocket(5500);
            System.out.println("服务器正常启动。。。。");
            int recvMsgSize = 0;
            byte[] recvBuf = new byte[1024];
            while (true) {
                Socket client = socket.accept();
                SocketAddress clientAddress = client.getRemoteSocketAddress();
                System.out.println("Handling client at " + clientAddress);
                in = client.getInputStream();
                while ((recvMsgSize = in.read(recvBuf)) != -1) {
                    byte[] temp = new byte[recvMsgSize];
                    System.arraycopy(recvBuf, 0, temp, 0, recvMsgSize);
                    System.out.println(new String(temp));
                }
            }
        } catch (Exception e) {

        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
