package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
   private Logger logger=LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
     String fileName=file.getOriginalFilename();
     //获取扩展名，比如输入abc。jpg可以获取到jpg
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensionName;
       logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);
       //生成目录文件,存放路径
        File fileDir=new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            //如果你想根据File里的路径名建立文件夹（当你不知道此文件夹是否存在，也不知道父文件夹存在），就可用此方法.
            fileDir.mkdirs();
        }
        File targetFile=new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件已经上传成功了
            //todo 将targetfile上传到FTP服务器上面
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //todo 上传完之后删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
       logger.error("上传文件异常",e);
       return null;
        }
        return targetFile.getName();
    }

}
