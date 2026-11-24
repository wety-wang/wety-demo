package com.wety.demo.module.file.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.GeneralResourceEntity;
import com.wety.demo.common.entity.GeneralResourceEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.module.file.vo.GeneralResourceVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author wety
 */
@Service
public class GeneralResourceService {
    private static final Logger log = LoggerFactory.getLogger(GeneralResourceService.class);

    @Resource
    private GeneralResourceEntityRepository generalResourceEntityRepository;

    @Value("${application.upload.host}")
    private String serverHost;

    public GeneralResourceVO upload(MultipartFile file) {
        //保存上传的资源文件路径，路径在部署jar包同级目录。
        String path = System.getProperty("user.dir") + "/static/upload/";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        String subdir = simpleDateFormat.format(new Date());

        File dir = new File(path + subdir);
        // 如果不存在则创建目录
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String original = file.getOriginalFilename();
        String fileName = UUIDGenerator.generatorUUID() + "." + getSuffixFileName(file.getOriginalFilename());
        log.warn("[文件上传][file name:{}]", fileName);
        File targetFile = new File(dir, fileName);
        log.warn("[文件上传][准备上传文件][file:{}]", targetFile.getAbsolutePath());
        if (!targetFile.exists()) {
            try {
                file.transferTo(targetFile);
            } catch (Exception e) {
                log.error("[文件上传][上传失败][file:" + targetFile.getAbsolutePath() + "]", e);
                throw new CustomException(e);
            }
        }
        log.warn("[文件上传][保存完成][file:{}]", targetFile.getAbsolutePath());
        String uri = "/upload/" + subdir + "/" + fileName;
        GeneralResourceEntity entity = new GeneralResourceEntity();
        entity.setUri(uri);
        entity.setAbsolutePath(targetFile.getAbsolutePath());
        entity.setCode(UUIDGenerator.generatorUUID());
        entity.setName(fileName);
        entity.setCreateTime(new Date());
        entity.setPath("static/upload/" + subdir + "/" + fileName);
        entity.setUpdateTime(new Date());
        entity.setOriginal(original);
        generalResourceEntityRepository.save(entity);

        return new GeneralResourceVO(entity, serverHost);
    }

    /**
     * 文件绑定
     *
     * @param ownerCode    归属者编号
     * @param resourceList 文件集合 取消绑定传null
     * @throws Exception
     */
    public String rebindResource(String ownerCode, List<String> resourceList) throws Exception {
        generalResourceEntityRepository.updateToNullByOwnerCode(ownerCode);
        return bindResource(ownerCode, resourceList);
    }

    public String bindResource(String ownerCode, List<String> resourceList) throws Exception {
        if (CollectionUtils.isNotEmpty(resourceList)) {
            for (String resourceCode : resourceList) {
                if (resourceCode == null) {
                    continue;
                }
                Optional<GeneralResourceEntity> optional = generalResourceEntityRepository.findByCode(resourceCode);
                if (optional.isPresent()) {
                    GeneralResourceEntity entity = optional.get();
                    entity.setOwnerCode(ownerCode);
                    generalResourceEntityRepository.save(entity);
                } else {
                    throw new Exception("资源不存在");
                }
            }
        }
        return GlobalConst.SUCCESS;
    }

    public static String getSuffixFileName(File file) {
        if (null == file) {
            return null;
        }
        String name = file.getName();
        int idx = name.lastIndexOf(".");
        if (idx != -1) {
            name = name.substring(idx + 1);
        }
        return name;
    }

    /**
     * 获取文件后缀名
     *
     * @param file
     * @return
     */
    public static String getSuffixFileName(String file) {
        return getSuffixFileName(new File(file));
    }

    /**
     * 下载
     * @param res
     * @param dto
     */
    public void download(HttpServletResponse res, String code) throws UnsupportedEncodingException {
        Optional<GeneralResourceEntity> optional = generalResourceEntityRepository.findByCode(code);
        String filePath;
        String fileName;
        if (optional.isPresent()) {
            try {
                filePath = optional.get().getPath();
                fileName = optional.get().getName();
            } catch (Exception e) {
                throw new CustomException("文件生成失败");
            }
        } else {
            throw new CustomException("资源不存在[" + code + "]");
        }

        // 返回流
        res.setContentType("text/html;charset=UTF-8");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        // 返回流
        try (OutputStream outputStream = res.getOutputStream(); InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            log.error("文件下载错误", e);
            throw new CustomException("文件下载失败");
        }
    }

}
