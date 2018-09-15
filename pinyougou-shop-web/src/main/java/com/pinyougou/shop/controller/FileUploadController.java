package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@Controller//相当于controller和responseBody
@ResponseBody
public class FileUploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/fileUpload")
    //springMVC框架的文件上传:MultipartFile类多媒体,file:前端表单中表的名字
    public Result FileUpload(MultipartFile file) {
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fileUpload/fdfs_client.conf");
            String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            String fileIp = fastDFSClient.uploadFile(file.getBytes(), extName);//返回fileID
            String url = FILE_SERVER_URL + fileIp;//拼接服务器IP
            Result result = new Result(true,url);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
}
