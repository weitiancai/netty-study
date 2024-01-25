package ByteBufferStudy;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {

        try (FileChannel channel = new FileInputStream("./data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while(true) {
                // 读取channel  就是写入buffer
                int length = channel.read(buffer);
                log.info("读取到字节数{}", length);
                if(length == -1){
                    break;
                }
                // 切换至读模式
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    System.out.println((char) b);
                }
                // 切换为写模式
                buffer.clear();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
