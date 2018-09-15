package com.alibaba.demo;

import org.csource.fastdfs.*;

import java.util.HashMap;

/**
 * 分布式文件管理系统入门Demo
 * 1.引入依赖
 * 2.引入配置文件 fdfs_client.conf
 *
 */
/*
 // 1、加载配置文件，配置文件中的内容就是 tracker 服务的地址。
        try {
            ClientGlobal.init("D:\\IDEAWorkSpace\\pinyougou-parent\\fastDFS\\src\\main\\resources\\fdfs_client.conf");  	 	// 2、创建一个 TrackerClient 对象。直接 new 一个。
            TrackerClient trackerClient = new TrackerClient();
            // 3、使用 TrackerClient 对象创建连接，获得一个 TrackerServer 对象。
            TrackerServer trackerServer = trackerClient.getConnection();
            // 4、创建一个 StorageServer 的引用，值为 null
            StorageServer storageServer = null;
            // 5、创建一个 StorageClient 对象，需要两个参数 TrackerServer 对象、StorageServer的引用
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            // 6、使用 StorageClient 对象上传图片。
            //扩展名不带“.”
            String[] strings = storageClient.upload_file("E:\\pic\\1.jpg", "jpg", null);
            // 7、返回数组。包含组名和图片的路径。
            for (String string : strings) {
                System.out.println(string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 */
public class FastDFSDemo {

    public static void main(String[] args) {
        try {
            //1.加载配置文件
            ClientGlobal.init("D:\\IDEAWorkSpace\\pinyougou-parent\\fastDFS\\src\\main\\resources\\fdfs_client.conf");
            //2.创建tracker客户端
            TrackerClient trackerClient=new TrackerClient();
            //3.利用tracker客户端创建连接,获取tracker服务端
            TrackerServer trackerServer = trackerClient.getConnection();
            //4.创建storage服务端
            StorageServer storageServer=null;
            //5.创建storage客户端,需要tracker服务端和storage服务端
            StorageClient storageClient=new StorageClient(trackerServer,storageServer);
            //6.使用storage客户端上传文件
            String[] strings = storageClient.upload_file("E:\\pic\\1.jpg", "jpg", null);//扩展名不带“.”
            for (String string : strings) {
                System.out.println(string);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
