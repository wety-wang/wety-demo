package com.wety.demo.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.common.response.ResponseResult;
import com.wety.demo.core.misc.AppThreadLocal;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.io.IOException;

/**
 * @author wety
 */
public class StringHttpMessageConverterWrapper extends StringHttpMessageConverter {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
        if (Boolean.TRUE.equals(AppThreadLocal.getInstance().getHttpMessageConverterWrapping())) {
            ResponseResult responseResult = new ResponseResult();
            responseResult.setCode(GlobalReturnCodeEnum.SUCCESS.getCode());
            responseResult.setData(str);
            responseResult.setDuration(AppThreadLocal.getInstance().getProcessingDuration());

            // 如果业务代码里明确设置了code和message我们就取
            if (AppThreadLocal.getInstance().getReturnCode() != null) {
                responseResult.setCode(AppThreadLocal.getInstance().getReturnCode());
            }
            if (AppThreadLocal.getInstance().getReturnMessage() != null) {
                responseResult.setMessage(AppThreadLocal.getInstance().getReturnMessage());
            }

            super.writeInternal(mapper.writeValueAsString(responseResult), outputMessage);
        }
        else {
            super.writeInternal(str, outputMessage);
        }
    }
}
