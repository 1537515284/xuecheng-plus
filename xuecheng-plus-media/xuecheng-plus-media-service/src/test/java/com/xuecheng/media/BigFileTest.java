package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 大文件处理测试
 */
public class BigFileTest {

    //测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("D:\\ls\\resource\\video\\test.mp4");
        //分块文件存储路径
        String chunkFilePath = "D:\\ls\\resource\\video\\chunk\\";
        //分块文件大小
        int chunkSize = 1024 * 1024 * 5;
        //分块文件个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //使用流从源文件读数据，向分块文件中写数据
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        //缓存区
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            //分块文件写入流
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len=raf_r.read(bytes))!=-1){
                raf_rw.write(bytes,0,len);
                if(chunkFile.length()>=chunkSize){
                    break;
                }
            }
            raf_rw.close();
        }
        raf_r.close();

    }

    //将分块进行合并
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("D:\\ls\\resource\\video\\chunk\\");
        //源文件
        File sourceFile = new File("D:\\ls\\resource\\video\\test.mp4");
        //合并后的文件
        File mergeFile = new File("D:\\ls\\resource\\video\\test_merge.mp4");

        //取出所有分块文件
        File[] files = chunkFolder.listFiles();
        //将数组转成list
        List<File> filesList = Arrays.asList(files);
        //对分块文件排序 （这里分块文件名都是数字）
        filesList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
        //向合并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        //缓存区
        byte[] bytes = new byte[1024];
        //遍历分块文件，向合并 的文件写
        for (File file : filesList) {
            //读分块的流
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len=raf_r.read(bytes))!=-1){
                raf_rw.write(bytes,0,len);
            }
            raf_r.close();

        }
        raf_rw.close();
        //合并文件完成后对合并的文件md5校验
        FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_source = new FileInputStream(sourceFile);
        String md5_merge = DigestUtils.md5Hex(fileInputStream_merge);
        String md5_source = DigestUtils.md5Hex(fileInputStream_source);
        if(md5_merge.equals(md5_source)){
            System.out.println("文件合并成功");
        }

    }


}
