package com.wety.demo.core.http;

import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.core.misc.AppThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 全局Controller异常解析器，封装ResponseResult对象并填充业务层可能设置的异常
 *
 * @author wety
 */
@Component
public class GlobalHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
    private static final Logger log = LoggerFactory.getLogger(GlobalHandlerExceptionResolver.class);

    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String DURATION = "duration";

    /**
     * 自定义HandlerExceptionResolver排在第一位
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public ModelAndView resolveException(
          HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex
    ) {
        log.error("拦截到异常", ex);
        // model and view
        ModelAndView mv = new ModelAndView();
        ModelMap model = mv.getModelMap();
        //
        if (AppThreadLocal.getInstance().getReturnCode() != null) {
            model.put(CODE, AppThreadLocal.getInstance().getReturnCode());
        }
        else {
            model.put(CODE, GlobalReturnCodeEnum.INTERNAL_ERROR.getCode());
        }
        if (AppThreadLocal.getInstance().getReturnMessage() != null) {
            model.put(MESSAGE, AppThreadLocal.getInstance().getReturnMessage());
        }
        else {
            if (ex.getCause() == null) {
                model.put(MESSAGE, ex.getMessage());
            }
            else {
                model.put(MESSAGE, ex.getCause().getMessage());
            }
        }

        model.put(DURATION, AppThreadLocal.getInstance().getProcessingDuration());

        mv.setView(new MappingJackson2JsonView());
        return mv;
    }

}
