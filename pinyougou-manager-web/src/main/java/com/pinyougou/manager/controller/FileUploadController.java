package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@Controller
@ResponseBody
public class FileUploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/fileUpload")
    public Result fileUpload(MultipartFile file){
        try {
            FastDFSClient client=new FastDFSClient("classpath:fileUpload/fdfs_client.conf");
            String exName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            String filId = client.uploadFile(file.getBytes(), exName);
            return new Result(true,FILE_SERVER_URL+filId);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }

}
