package com.wety.demo.module.file.controller;

import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.module.file.service.GeneralResourceService;
import com.wety.demo.module.file.vo.GeneralResourceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wety
 */
@RestController
@RequestMapping("/bac/common/file")
@Api(tags = "00-02-00 文件相关接口", produces = "application/json", consumes = "application/json")
public class GeneralResourceController {

    @Resource
    private GeneralResourceService generalResourceService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation(value = "00-02-01 上传文件")
    public GeneralResourceVO upload(@RequestPart("file") MultipartFile file) {
        return generalResourceService.upload(file);
    }

    @RequestMapping(value = "/uploadRichText", method = RequestMethod.POST)
    @ApiOperation(value = "00-02-02 富文本上传文件")
    public GeneralResourceVO uploadRichText(@RequestPart("file") MultipartFile file) {
        return generalResourceService.upload(file);
    }

    @PostMapping(path = "/download")
    @ApiOperation(value = "00-02-03 下载文件")
    public void download(HttpServletResponse res, @RequestBody UniversalCodeDTO codeDTO) throws Exception {
        generalResourceService.download(res, codeDTO.getCode());
    }
}
