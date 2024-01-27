package ServerClient;

import ByteBufferStudy.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class NotBlockingQueue {
        public static void main(String[] args) {
            // 创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 获得服务器通道
            try(ServerSocketChannel server = ServerSocketChannel.open()) {
                // 为服务器通道绑定端口
                server.bind(new InetSocketAddress(8080));
                // 用户存放连接的集合
                ArrayList<SocketChannel> channels = new ArrayList<>();
                // 循环接收连接
                while (true) {
                    // 设置为非阻塞模式，没有连接时返回null，不会阻塞线程
                    server.configureBlocking(false);
                    System.out.println("在连接前"); //一直出现
                    SocketChannel socketChannel = server.accept();
                    // 通道不为空时才将连接放入到集合中
                    if (socketChannel != null) {
                        System.out.println("after connecting...");
                        channels.add(socketChannel);
                    }
                    // 循环遍历集合中的连接
                    for(SocketChannel channel : channels) {
                        // 处理通道中的数据
                        // 设置为非阻塞模式，若通道中没有数据，会返回0，不会阻塞线程
                        channel.configureBlocking(false);
                        int read = channel.read(buffer);
                        if(read > 0) {
                            buffer.flip();
                            ByteBufferUtil.debugRead(buffer);
                            buffer.clear();
                            System.out.println("after reading");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
