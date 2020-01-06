package com.file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 文件拆分合并
 *
 * @date:2019/12/27 17:53
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * 常用的长度
     */
    public static final int LEN_1_KB = 1024 * 5;
    public static final int LEN_5_KB = 1024 * 5;
    public static final int LEN_10_KB = 1024 * 10;
    public static final int LEN_20_KB = 1024 * 20;
    public static final int LEN_1_MB = 1024 * 1024;


    /**
     * 切文件
     *
     * @param fileName  文件
     * @param delimiter 分割大小
     * @return 字节数组
     * @throws Exception 异常
     */
    public static List<byte[]> cuttingFile(File fileName, int delimiter) throws IOException {

        // try - with - resource
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r"); FileChannel channel = file.getChannel()) {
            // 总长度
            long size = file.length();

            // 需要拆多少个包 , 防止数组拷贝
            int block = (int) (size % delimiter == 0 ? size / delimiter : (size / delimiter) + 1);

            // 新建数组, 防止数组拷贝
            ArrayList<byte[]> list = new ArrayList<>(block);


            // 1. 起始位置
            long position = channel.position();

            // 2. 只有大于他才执行
            while (size > delimiter) {

                ByteBuffer buffer = ByteBuffer.allocate(delimiter);
                channel.read(buffer, position);

                // 我们采用的是堆内存 , 不是直接内存的原因是因为我们要做数组拷贝 , 没必要
                byte[] array = buffer.array();

                // 添加进去
                list.add(array);

                // size 每次减小
                size = size - delimiter;

                // 位置每次 增加
                position = position + delimiter;
            }

            // 最后一次绝对不满 / 一开始就小于
            ByteBuffer buffer = ByteBuffer.allocate(delimiter);

            channel.read(buffer, position);

            byte[] bytes = new byte[(int) size];
            buffer.flip();

            // 写到未满的数组里
            buffer.get(bytes);

            // 添加进去
            list.add(bytes);

            return list;
        } catch (IOException e) {
            throw e;
        }
    }


    /**
     * 合并文件
     *
     * @param fileName 文件路径
     * @param bytes    字节流
     * @throws Exception 中途异常
     */
    public static void mergingFile(File fileName, byte[] bytes) throws IOException {
        try (RandomAccessFile upload = new RandomAccessFile(fileName, "rw"); FileChannel channel = upload.getChannel()) {
            // 起始位置 - 总长度
            long length = upload.length();
            channel.position(length);

            // 写入
            upload.write(bytes);
        } catch (IOException e) {
            // 抛出异常
            throw e;
        }
    }


    /**
     * 实时读取文件
     *
     * @throws IOException
     */
    private static void consumeLogFile(final String fileName, Consumer<String> consumer) throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
        FileChannel channel = file.getChannel();
        ByteBuffer allocate = ByteBuffer.allocate(128);
        // 开始长度
        int start = 0;

        while (true) {

            // 1. 清空
            allocate.clear();

            // 2. 读
            int read = channel.read(allocate, start);


            // 3. 如果读取数据为-1 返回
            if (read == -1) continue;


            // 4. start=start+读取长度
            start += read;


            // 变成数组 -> 由于需要读取不需要0拷贝
            byte[] array = allocate.array();


            // 5.读取日志
            String log = new String(array, 0, read, Charset.forName("utf8"));


            // 消费数据
            consumer.accept(log);
        }
    }


    /**
     * 复制文件
     *
     * @param source 源文件
     * @param dest   目的文件
     * @throws IOException 异常
     */
    public static void copyFile(String source, String dest) throws IOException {
        try (RandomAccessFile sourceFile = new RandomAccessFile(new File(source), "r");
             RandomAccessFile destFile = new RandomAccessFile(new File(dest), "rw");
             FileChannel destChannel = destFile.getChannel();
             FileChannel channel = sourceFile.getChannel();) {
            long length = sourceFile.length();
            channel.transferTo(0, length, destChannel);
        } catch (IOException e) {
            throw e;
        }
    }


    /**
     * 记录文件夹文件 , 递归实现, 也可以用栈来实现
     *
     * @param dir    文件夹
     * @param filter 过滤器
     * @param files  保存文件位置
     */
    public static void recordFile(File dir, Predicate<File> filter, List<File> files) {
        if (dir.isDirectory()) {
            File[] file = dir.listFiles();
            for (File f : file) {
                recordFile(f, filter, files);
            }
        } else {
            if (filter.test(dir)) {
                files.add(dir);
            }
        }
    }
}