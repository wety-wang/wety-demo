package com.wety.demo.module.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 **/
@Schema(name = "com.wety.demo.module.common.dto.EditPasswordDTO", description = "修改密码传入参数")
public class EditPasswordDTO {

    /**
     * 当前密码
     */
    @Schema(description = "当前密码")
    private String currentPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码")
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "EditPasswordDTO{"+
                "currentPassword='"+currentPassword+'\''+
                ", newPassword='"+newPassword+'\''+
                '}';
    }
}
