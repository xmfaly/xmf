package org.xmf.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmf.framework.annotation.Controller;
import org.xmf.framework.annotation.RestController;
import org.xmf.framework.bean.Data;
import org.xmf.framework.bean.Handler;
import org.xmf.framework.bean.Param;
import org.xmf.framework.bean.View;
import org.xmf.framework.helper.BeanHelper;
import org.xmf.framework.helper.ConfigHelper;
import org.xmf.framework.helper.ControllerHelper;
import org.xmf.framework.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    private static Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        // 初始化 Helper 类
        HelperLoader.init();


        // 获取 ServletContext 对象（用于注册 Servlet ）
        ServletContext servletContext = servletConfig.getServletContext();

        // 注册处理 JSP 的 Servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");

        // 注册处理静态资源的的默认 Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");

    }


    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 获取请求方法和路径
        String requestMethod = request.getMethod().toLowerCase();
        String requestPath = request.getPathInfo();

        // 获取处理器
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);

        if (handler != null) {
            // 获取Controller类及其Bean实例
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);

            // 创建请求参数对象
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                paramMap.put(paramName, paramValue);
            }
            String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));

            if (StringUtil.isNotEmpty(body)) {
                String[] params = StringUtil.splitString(body, "&");
                if (ArrayUtil.isNotEmpty(params)) {
                    for (String param : params) {
                        String[] array = StringUtil.splitString(param, "=");
                        if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName, paramValue);
                        }
                    }
                }
            }

            Param param = new Param(paramMap);


            //调用Action
            Method actionMethod = handler.getActionMethod();


            Object result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);


            //返回json
            if (controllerBean.getClass().isAnnotationPresent(RestController.class)) {
                Data data = (Data) result;
                Object model = data.getModel();
                if (model != null) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTf-8");
                    PrintWriter writer = response.getWriter();
                    String json = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }

            } else if (controllerBean.getClass().isAnnotationPresent(Controller.class)) {
                View view = (View) result;
                String path = view.getPath();


                if (StringUtil.isNotEmpty(path)) {
                    if (path.startsWith("/")) {
                        response.sendRedirect(request.getContextPath() + path);
                    } else {
                        Map<String, Object> model = view.getModel();
                        for (Map.Entry<String, Object> entry : model.entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }

                        LOGGER.error(ConfigHelper.getAppJspPath() + path);

                        request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);



                    }
                }
            }
        }
    }
}
