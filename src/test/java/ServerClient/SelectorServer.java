package ServerClient;

import com.sun.org.apache.bcel.internal.generic.Select;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Slf4j
public class SelectorServer {
        public static void main5(String[] args) throws IOException {
            Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(8080));
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                System.out.println("不会死循环");
                // 没有事件发生 会阻塞，有事件 会继续
                // 事件未处理 ，就还是会select（）继续   直到事件处理或者取消
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();

                    if (next.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                        System.out.println("Client connected: " + sc.getRemoteAddress());
                    } else if (next.isReadable()) {
                        SocketChannel sc = (SocketChannel) next.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(200);
                        int bytesRead = sc.read(buffer);
                        if (bytesRead > 0) {
                            buffer.flip();
                            byte[] data = new byte[buffer.remaining()];
                            buffer.get(data);
                            System.out.println("Received: " + new String(data));
                            buffer.clear();
                        } else if (bytesRead == -1) {
                            next.cancel();
                        }
                    }
                    next.cancel();
                    iterator.remove();
                }
            }
        }


//       首先通过ssc.register(selector, 0, null)将ServerSocketChannel注册到Selector上，
//       并指定感兴趣的事件为OP_ACCEPT。而后在接受连接后，将接受到的SocketChannel注册到同一个Selector上，
//       并指定感兴趣的事件为OP_ACCEPT，以便继续接受其他连接请求。
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(8080));
        SelectionKey ssckey = ssc.register(selector, 0, null);
        ssckey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", ssckey);


        while(true){
            System.out.println("不会死循环");
            // select方法， 没有事件发生，线程阻塞，有事件，现成才会恢复运行
            selector.select();
            // 处理事件，selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                // 就是有效事件的key
                SelectionKey key = iterator.next();
                // 需要把selectKeys中删除

                log.debug("key:{}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
//                    sc.register(selector, 0, null);
//                    // 注意注册的是read事件
//                    key.interestOps(SelectionKey.OP_READ);
                    sc.register(selector, SelectionKey.OP_READ);
                    System.out.println("Client connected: " + sc.getRemoteAddress());
                } else if (key.isReadable()) {
                    try {
                        SocketChannel sc = (SocketChannel) key.channel();
                        log.debug("{}", sc);
                        ByteBuffer buffer = ByteBuffer.allocate(200);
                        if (sc.read(buffer) > 0) {
                            buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                            System.out.println("Received Read: " + new String(data));
                            buffer.clear();
                        }
                        else {
                            key.cancel();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }
//                    SocketChannel sc = (SocketChannel) key.channel();
//                    ByteBuffer buffer = ByteBuffer.allocate(200);
//                    int bytesRead = sc.read(buffer);
//                    if (bytesRead > 0) {
//                        buffer.flip();
//                        byte[] data = new byte[buffer.remaining()];
//                        buffer.get(data);
//                        System.out.println("Received: " + new String(data));
//                        buffer.clear();
//                    } else if (bytesRead == -1) {
//                        key.cancel();
//                    }
                }
                iterator.remove();
            }
        }
    }
}
