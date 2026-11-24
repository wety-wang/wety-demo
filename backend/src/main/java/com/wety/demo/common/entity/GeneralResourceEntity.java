package com.wety.demo.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wety
 */
@Entity
@Table(name = "general_resource")
@Schema(description = "文件资源表，用于统一记录系统中的资源，例如上传的图片")
public class GeneralResourceEntity implements Serializable {

    private static final long serialVersionUID = 2586769562079131282L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "数据主键")
    private Long id;

    @Column(name = "code", nullable = false, length = 40)
    @Schema(description = "资源code")
    private String code;

    @Column(name = "name")
    @Schema(description = "资源名称(eg:background.png)")
    private String name;

    @Column(name = "uri", length = 1000)
    @Schema(description = "资源访问路径，在前面加上域名就能直接访问的，以/开头")
    private String uri;

    @Column(name = "original")
    @Schema(description = "原始文件名")
    private String original;

    @Column(name = "path", length = 1000)
    @Schema(description = "资源路径，该路径是相对于应用根目录的路径")
    private String path;

    @Column(name = "absolute_path", length = 1000)
    @Schema(description = "绝对路径，该路径是文件相对于服务器的绝对路径")
    private String absolutePath;

    @Column(name = "owner_code", length = 40)
    @Schema(description = "资源拥有者编号，不同的业务自己设置和查询，一个业务记录对应多个资源时借助该编号")
    private String ownerCode;

    @Column(name = "create_time")
    @Schema(description = "创建时间")
    private Date createTime;

    @Column(name = "update_time")
    @Schema(description = "修改时间")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "GeneralResourceEntity{"+
                "id="+id+
                ", code='"+code+'\''+
                ", name='"+name+'\''+
                ", uri='"+uri+'\''+
                ", original='"+original+'\''+
                ", path='"+path+'\''+
                ", absolutePath='"+absolutePath+'\''+
                ", ownerCode='"+ownerCode+'\''+
                ", createTime="+createTime+
                ", updateTime="+updateTime+
                '}';
    }
}
