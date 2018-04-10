package com.fireyao.nio.channel;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author liuliyuan
 * @date 2018/4/8 15:33
 * @Description:
 */
public class FileChannelTest {


    private static final String filePath = "G:\\workspaceOne\\nio\\src\\main\\resources\\file\\data.txt";

    @Test
    public void test1() {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            FileChannel fileChannel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(48);
            int read = fileChannel.read(buffer);
            while (read != -1) {
                System.out.println(read);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }

                buffer.clear();
                read = fileChannel.read(buffer);
            }
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test2() throws Exception {

        FileOutputStream inputStream = new FileOutputStream(filePath);
        FileChannel fileChannel = inputStream.getChannel();

        String content = "写入数据ssss...";

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        buffer.put(content.getBytes());

        buffer.flip();

        while (buffer.hasRemaining()) {
            fileChannel.write(buffer, fileChannel.position() + fileChannel.size());
        }

        buffer.clear();

        fileChannel.close();

    }


    @Test
    public void name3() throws Exception {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 5500);


        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(address);

        write(socketChannel);

        socketChannel.close();
    }


    private void write(SocketChannel channel) throws Exception {
        String newData = "12345678";
        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        buf.put(newData.getBytes());

        buf.flip();

        while (buf.hasRemaining()) {
            channel.write(buf);
        }
    }



}
