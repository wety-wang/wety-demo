package com.wety.demo.module.file.vo;

import com.wety.demo.common.entity.GeneralResourceEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

/**
 * @author wety
 */
@ApiModel(value = "GeneralResourceVO", description = "文件资源VO")
public class GeneralResourceVO {

    @Column(name = "code")
    @ApiModelProperty(value = "资源code")
    private String code;

    @Column(name = "name")
    @ApiModelProperty(value = "资源名称(eg:background.png)")
    private String name;

    @Column(name = "uri")
    @ApiModelProperty(value = "资源访问路径，在前面加上域名就能直接访问的，以/开头")
    private String uri;

    @Column(name = "original")
    @ApiModelProperty(value = "原始文件名")
    private String original;

    @ApiModelProperty(value = "资源访问url", example = "https://photo.16pic.com/00/24/45/16pic_2445172_b.jpg")
    private String url;

    public GeneralResourceVO() {
    }

    public GeneralResourceVO(GeneralResourceEntity entity, String serverHost) {
        this.code = entity.getCode();
        this.name = entity.getName();
        this.uri = entity.getUri();
        this.original = entity.getOriginal();
        this.url = serverHost + entity.getUri();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "GeneralResourceVO{"+
                "code='"+code+'\''+
                ", name='"+name+'\''+
                ", uri='"+uri+'\''+
                ", original='"+original+'\''+
                ", url='"+url+'\''+
                '}';
    }
}
