package com.wety.demo.core.http;

import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.common.response.ResponseResult;
import com.wety.demo.core.misc.AppThreadLocal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 定制HttpMessageConverter，用于把业务层Controller的响应包装成公共的响应对象ResponseResult<br>
 * 参见WebMvcConfigurer来了解ResponseWrappingHttpMessageConverter是如何替换默认的MappingJackson2HttpMessageConverter的
 *
 * @author wety
 */
public class MappingJackson2HttpMessageConverterWrapper extends MappingJackson2HttpMessageConverter {

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
          throws IOException, HttpMessageNotWritableException {
        if (Boolean.TRUE.equals(AppThreadLocal.getInstance().getHttpMessageConverterWrapping())) {
            Object wrappedObject;
            // 分页响应统一转换下，与前端一致起来
            if (object instanceof Page<?>) {
                wrappedObject = customizePageData((Page<?>) object);
            }else {
                wrappedObject = object;
            }
            ResponseResult responseResult = new ResponseResult();
            responseResult.setCode(GlobalReturnCodeEnum.SUCCESS.getCode());
            responseResult.setData(wrappedObject);
            responseResult.setDuration(AppThreadLocal.getInstance().getProcessingDuration());

            // 如果业务代码里明确设置了code和message我们就取
            if (AppThreadLocal.getInstance().getReturnCode() != null) {
                responseResult.setCode(AppThreadLocal.getInstance().getReturnCode());
            }
            if (AppThreadLocal.getInstance().getReturnMessage() != null) {
                responseResult.setMessage(AppThreadLocal.getInstance().getReturnMessage());
            }

            super.writeInternal(responseResult, type, outputMessage);
        }
        else {
            super.writeInternal(object, type, outputMessage);
        }
    }

    /**
     * 把分页信息转成浏览器需要的格式定制一下
     *
     * @param pageInfo
     *
     * @return
     */
    private Object customizePageData(Page<?> pageInfo) {
        Map<String, Object> pageInfoMap = new HashMap<>(3);
        pageInfoMap.put("content", pageInfo.getContent());
        //
        Pageable pageable = pageInfo.getPageable();
        Map<String, Object> pageableMap = new HashMap<>(9);
        pageableMap.put("pageSize", pageable.getPageSize());
        pageableMap.put("pageNumber", pageable.getPageNumber());
        pageableMap.put("offset", pageable.getOffset());
        //
        pageableMap.put("totalRow", pageInfo.getTotalElements());
        pageableMap.put("totalPages", pageInfo.getTotalPages());
        pageableMap.put("numberOfElements", pageInfo.getNumberOfElements());
        pageInfoMap.put("pageable", pageableMap);
        return pageInfoMap;
    }
}
