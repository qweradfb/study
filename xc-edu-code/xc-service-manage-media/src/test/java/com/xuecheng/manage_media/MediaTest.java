package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MediaTest {
    //测试分块
    @Test
    public void chunkTest() throws Exception {
        long start = System.currentTimeMillis();
        String source = "F:\\video\\lucene.avi";
        String chuck = "F:\\video\\chunk\\";
        File chuckFile = new File(chuck);
        if (!chuckFile.exists()) {
            chuckFile.mkdirs();
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(source));
        byte[] bys = new byte[1*1024*1024];
        int i = 0;
        int number = 0;
        while ((i=bufferedInputStream.read(bys))!=-1){
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(chuck+number));
            bufferedOutputStream.write(bys,0,i);
            bufferedOutputStream.close();
            number++;
        }
        bufferedInputStream.close();
        long end = System.currentTimeMillis();
        System.out.println(end-start);//129
    }
    //测试文件的合并
    @Test
    public void mergeTest() throws Exception {
        long start = System.currentTimeMillis();
        String chuck = "F:\\video\\chunk\\";
        String source = "F:\\video\\luceneMerge.avi";
        File chuckFile = new File(chuck);
        File mergeFile = new File(source);
        RandomAccessFile write = new RandomAccessFile(source,"rw");
        write.seek(0);
        if (mergeFile.exists()){
            mergeFile.delete();
        }
        mergeFile.createNewFile();
        File[] files = chuckFile.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int name1 = Integer.parseInt(o1.getName());
                int name2 = Integer.parseInt(o2.getName());
                return name1-name2;
            }
        });
        byte[] bys = new byte[1024];
        int len = 0;
        for (File file : files) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            while((len=bufferedInputStream.read(bys))!=-1){
                write.write(bys,0,len);
            }
            bufferedInputStream.close();
        }
        write.close();
        long end = System.currentTimeMillis();
        System.out.println(end-start);//162
    }

    //测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        long start = System.currentTimeMillis();
        File sourceFile = new File("F:\\video\\lucene.avi");
        String chunkPath = "F:\\video\\chunk\\";
        File chunkFolder = new File(chunkPath);
        if(!chunkFolder.exists()){
            chunkFolder.mkdirs();
        }
//分块大小
        long chunkSize = 1024*1024*1;
//分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize );
        if(chunkNum<=0){
            chunkNum = 1;
        }
//缓冲区大小
        byte[] b = new byte[1024];
//使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
//分块
        for(int i=0;i<chunkNum;i++){
//创建分块文件
            File file = new File(chunkPath+i);
            boolean newFile = file.createNewFile();
            if(newFile){
//向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while((len = raf_read.read(b))!=-1){
                    raf_write.write(b,0,len);
                    if(file.length()>chunkSize){
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
        long end = System.currentTimeMillis();
        System.out.println(end-start);//2105
    }
    //测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        long start = System.currentTimeMillis();
//块文件目录
        File chunkFolder = new File("F:\\video\\chunk\\");
//合并文件
        File mergeFile = new File("F:\\video\\luceneMerge.avi");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
//创建新的合并文件
        mergeFile.createNewFile();
//用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
//指针指向文件顶端
        raf_write.seek(0);
//缓冲区
        byte[] b = new byte[1024];
//分块列表
        File[] fileArray = chunkFolder.listFiles();
// 转成集合，便于排序
        List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
// 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                }
                return 1;
            }
        });
//合并文件
        for(File chunkFile:fileList){
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"rw");
            int len = -1;
            while((len=raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
        long end = System.currentTimeMillis();
        System.out.println(end-start);//273
    }
}
