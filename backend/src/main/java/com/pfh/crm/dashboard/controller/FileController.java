package com.pfh.crm.dashboard.controller;

import com.pfh.crm.dashboard.common.annotaition.SelfLimiter;
import com.pfh.crm.dashboard.common.model.Result;
import com.pfh.crm.dashboard.common.util.AliyunOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("file")
@Slf4j
public class FileController {

    @SelfLimiter
    @PostMapping("upload")
    public Result upload(@RequestParam("file") MultipartFile file){
        AliyunOSSUtils.uploadImg(file);
        return Result.ok();
    }

    @SelfLimiter
    @PostMapping("img/upload")
    public Result uploadImg(@RequestParam("file") MultipartFile file){
        String fileId = AliyunOSSUtils.uploadImg(file);
        log.info("upload file to oss");
        return Result.ok(fileId);
    }
}
