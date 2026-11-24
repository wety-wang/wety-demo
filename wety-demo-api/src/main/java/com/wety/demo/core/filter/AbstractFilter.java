package com.wety.demo.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.common.response.ResponseResult;
import com.wety.demo.common.util.CloseableUtil;
import com.wety.demo.core.misc.AppThreadLocal;
import org.apache.commons.io.IOUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Base Filter to be implemented by Filters in the applicaiton, supplying
 * writeObjectJosn() as a utility.
 *
 * @author wety
 */
public abstract class AbstractFilter implements Filter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void renderFailure(GlobalReturnCodeEnum returnCodeEnum, ServletResponse response) throws IOException {
        renderFailure(returnCodeEnum.getMessage(), returnCodeEnum.getCode(), response);
    }

    protected void renderFailure(String message, String errorCode, ServletResponse response) throws IOException {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(errorCode);
        responseResult.setDuration(AppThreadLocal.getInstance().getProcessingDuration());
        responseResult.setMessage(message);
        writeObjectJosn(objectMapper.writeValueAsString(responseResult), response);
    }

    /**
     * 讲对象json化并写入response的OutputStream流中
     *
     * @param object
     * @param response
     * @throws IOException
     */
    protected void writeObjectJosn(Object object, ServletResponse response) throws IOException {
        String jsonResult = objectMapper.writeValueAsString(object);
        if (jsonResult.startsWith("\"")) {
            jsonResult = jsonResult.substring(1);
            jsonResult = jsonResult.substring(0, jsonResult.length() - 1);
            jsonResult = jsonResult.replace("\\", "");
        }
        OutputStream outputStream = response.getOutputStream();
        response.setContentType("application/json");
        IOUtils.write(jsonResult, response.getOutputStream(),StandardCharsets.UTF_8);
        CloseableUtil.closeIgnoreException(outputStream);
    }
}
