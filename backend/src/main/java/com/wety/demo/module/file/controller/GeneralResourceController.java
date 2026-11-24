package com.wety.demo.module.file.controller;

import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.module.file.service.GeneralResourceService;
import com.wety.demo.module.file.vo.GeneralResourceVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author wety
 */
@RestController
@RequestMapping("/bac/common/file")
@Tag(name = "00-02-00 文件相关接口")
public class GeneralResourceController {

    @Resource
    private GeneralResourceService generalResourceService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @Operation(summary = "00-02-01 上传文件")
    public GeneralResourceVO upload(@RequestPart("file") MultipartFile file) {
        return generalResourceService.upload(file);
    }

    @RequestMapping(value = "/uploadRichText", method = RequestMethod.POST)
    @Operation(summary = "00-02-02 富文本上传文件")
    public GeneralResourceVO uploadRichText(@RequestPart("file") MultipartFile file) {
        return generalResourceService.upload(file);
    }

    @PostMapping(path = "/download")
    @Operation(summary = "00-02-03 下载文件")
    public void download(HttpServletResponse res, @RequestBody UniversalCodeDTO codeDTO) throws Exception {
        generalResourceService.download(res, codeDTO.getCode());
    }
}
