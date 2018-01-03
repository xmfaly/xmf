package org.xmf.framework.helper;

import org.xmf.framework.annotation.GetMapping;
import org.xmf.framework.annotation.PostMapping;
import org.xmf.framework.bean.Handler;
import org.xmf.framework.bean.Request;
import org.xmf.framework.util.ArrayUtil;
import org.xmf.framework.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 */
public final class ControllerHelper {

    /**
     * 用于存放请求和处理器的映射关系
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获取所有的 Controller 类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            //遍历这些 Controller 类
            for (Class<?> controllerClass : controllerClassSet) {
                //获取 Controller 类中的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {

                    String requestMethod = "",requestPath = "";

                    //遍历 Controller 类中的方法
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(GetMapping.class) ) {
                            GetMapping get = method.getAnnotation(GetMapping.class);
                            requestMethod = "get";
                            requestPath = get.value();
                        }else if(method.isAnnotationPresent(PostMapping.class)){
                            PostMapping get = method.getAnnotation(PostMapping.class);
                            requestMethod = "post";
                            requestPath = get.value();
                        }
                        Request request = new Request(requestMethod, requestPath);
                        Handler handler = new Handler(controllerClass, method);
                        // 初始化 Action Map
                        ACTION_MAP.put(request, handler);
                    }
                }
            }
        }
    }

    /**
     * 获取 Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
