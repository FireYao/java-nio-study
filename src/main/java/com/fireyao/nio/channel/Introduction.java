/**
 * @author liuliyuan
 * @date 2018/4/8 15:13
 * @Description: NIO中最重要的集中Channel的实现
 *
 *  FileChannel  用于文件的数据读写 不可以设置为非阻塞模式，他只能在阻塞模式下运行。
    DatagramChannel 用于UDP的数据读写
    SocketChannel 用于TCP的数据读写
    ServerSocketChannel 允许我们监听TCP链接请求，每个请求会创建会一个SocketChannel
 *
 */
package com.fireyao.nio.channel;