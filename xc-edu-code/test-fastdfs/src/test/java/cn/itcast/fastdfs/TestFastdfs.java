package cn.itcast.fastdfs;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastdfs {
    //文件上传
    @Test
    public void uploadTest(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);
            //创建客户端
            TrackerClient tc = new TrackerClient();
            //连接tracker Server
            TrackerServer ts = tc.getConnection();
            if (ts == null) {
                System.out.println("getConnection return null");
                return;
            }
            //获取一个storage server
            StorageServer ss = tc.getStoreStorage(ts);
            if (ss == null) {
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 sc1 = new StorageClient1(ts, ss);
            NameValuePair[] meta_list = null; //new NameValuePair[0];
            String item = "D:/45.jpg";
            String fileid;
            fileid = sc1.upload_file1(item, "png", meta_list);
            System.out.println("Upload local file " + item + " ok, fileid=" + fileid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        group1/M00/00/01/wKgZmV1rOfqANr5NAAG_AU5QNvI285.png

    }

    //文件下载
    @Test
    public void downloadTest() throws Exception{
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 storageClient1 = new StorageClient1(trackerServer,
                storageServer);
        byte[] result =
                storageClient1.download_file1("group1/M00/00/01/wKgZmV1rOfqANr5NAAG_AU5QNvI285.png");
        File file = new File("C:\\Users\\Administrator\\Desktop\\1.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(result);
        fileOutputStream.close();
    }

    //查询文件
    @Test
    public void findTest() throws Exception{
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer,
                storageServer);
        FileInfo fileInfo = storageClient.query_file_info("group1",
                "M00/00/01/wKgZmV1txxuAIpV5AAG_AU5QNvI845.jpg");
        System.out.println(fileInfo);
    }

    @Test
    public void deleteTest(){
        int group1 = deleteFile("group1", "M00/00/01/wKgZmV1rOfqANr5NAAG_AU5QNvI285.png");
        System.out.println(group1);
    }

    public int deleteFile (String group, String filePath) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;

        //利用fastdfs客户端，实现文件上传到fastdfs服务器上
        try {
            //代码是模板式的
            //1、加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");

            //2、创建一个tracker的客户端
            TrackerClient trackerClient = new TrackerClient();

            //3、通过trackerClient获取一个连接，连接到Tracker，得到一个TrackerServer
            trackerServer = trackerClient.getConnection();

            //4、通过trackerClient获取一个存储节点的StorageServer
            storageServer = trackerClient.getStoreStorage(trackerServer);

            //5、通过trackerServer和storageServer构造一个Storage客户端
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);

            //fastdfs删除文件
            return storageClient.delete_file(group, filePath);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭服务，释放资源
                if (null != storageServer) {
                    storageServer.close();
                }
                if (null != trackerServer) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


}
