package me.fireyao.nio.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @author liuliyuan
 * @date 2018/4/10 15:50
 * @Description: Buffer
 * capacity:容量
 * position:当前位置
 * limit : 写模式下：最多能往Buffer里写多少数据
 * 读模式下： 默认值等于容量capacity
 */
public class BufferTest {

    /**
     *
     */
    @Test
    public void getPut() {
        //分配一个5个字节大小的Buffer
        ByteBuffer buffer = ByteBuffer.allocate(5);
        //写数据到buffer
        buffer.put((byte) 'A');
        print(buffer);
        /**
         *  每次写数据到buffer中
         *  position会向前移动到下一个可插入数据的Buffer单元
         *  +1
         */
        buffer.put((byte) 'B');
        buffer.put((byte) 'C');
        buffer.put((byte) 'D');
        print(buffer);

        /**
         * flip() 切换读写模式
         * 此时limit = capacity
         * position=0
         * 在[position,limit]区间的数据才能读取
         *
         * position现在用于标记读的位置，limit表示之前写进了多少个byte、char等 —— 现在能读取多少个byte、char等。
         */
        buffer.flip();

        print(buffer);
        //A
        System.out.println((char) buffer.get());
        print(buffer);
        /**
         * 每次从buffer中读数据时
         * position向前移动到下一个可读的位置
         * -1
         */
        //B
        System.out.println((char) buffer.get());
        print(buffer);

        //C
        System.out.println((char) buffer.get());
        print(buffer);

        //D
        System.out.println((char) buffer.get());
        print(buffer);

        //java.nio.BufferUnderflowException
        //System.out.println((char) buffer.get());
        //print(buffer);

        /**
         * rewind()将position设回0，可以重读Buffer中的所有数据。limit保持不变
         */
        buffer.rewind();
        //A
        System.out.println((char) buffer.get());
        print(buffer);
        //B
        System.out.println((char) buffer.get());
        print(buffer);

        //C
        System.out.println((char) buffer.get());
        print(buffer);

        //D
        System.out.println((char) buffer.get());
        print(buffer);


        buffer.compact();
        buffer.clear();

        buffer.put((byte) 'E');
        print(buffer);


    }



    public void print(ByteBuffer byteBuffer) {
        System.out.println("capacity:" + byteBuffer.capacity() + "\n" +
                "position:" + byteBuffer.position() + "\n" +
                "limit:" + byteBuffer.limit());
    }

}
