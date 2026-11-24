package com.wety.demo.module.common.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.core.annotation.NoLogin;
import com.wety.demo.module.common.dto.EditPasswordDTO;
import com.wety.demo.module.common.dto.LoginDTO;
import com.wety.demo.module.common.service.LoginService;
import com.wety.demo.module.common.vo.AccountVo;
import com.wety.demo.module.user.service.UserService;
import com.wety.demo.module.user.vo.AccountDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wety
 */
@RestController
@RequestMapping("/bac/common")
@Api(tags = "00-01-00 登录接口", produces = "application/json", consumes = "application/json")
public class LoginController {

    @Resource
    private LoginService loginService;

    @Resource
    private UserService userService;

    @NoLogin
    @RequestMapping(value = "/getVerifyImage", method = RequestMethod.GET)
    @ApiOperation(value = "00-01-01 获取图片验证码", notes = "登录前先获取验证码用于校验")
    public void getVerifyImage(HttpServletRequest req, HttpServletResponse res) {
        loginService.getVerifyImage(req, res);
    }

    @NoLogin
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "00-01-02 登录", notes = "传递登录名和密码：loginName、password；密码使用SM2加密")
    @ApiResponses(@ApiResponse(code = 200, message = "登录成功返回一个带TOKEN的用户信息"))
    public AccountVo login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        return loginService.login(loginDTO, session);
    }

    @PostMapping(value = "/queryAccount")
    @ApiOperation(value = "00-01-03 查询当前账户信息", notes = "查询账户详情及其角色权限信息，无需参数，登录后请求带签名即可")
    public AccountDetailVO queryAccount() {
        return userService.queryAccount();
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    @ApiOperation(value = "00-01-04 退出登录", notes = "退出账户并清理会话")
    @ApiResponses(@ApiResponse(code = 200, message = "退出成功"))
    public String logout() {
        loginService.logout();
        return GlobalConst.SUCCESS;
    }

    @RequestMapping(value = "/editPassword", method = RequestMethod.POST)
    @ApiOperation(value = "00-01-05 修改密码", notes = "传入旧密码及新密码")
    @ApiResponses(@ApiResponse(code = 200, message = "修改成功返回success"))
    public String editPassword(@RequestBody EditPasswordDTO editPasswordDTO) {
        loginService.editPassword(editPasswordDTO);
        return GlobalConst.SUCCESS;
    }

}
