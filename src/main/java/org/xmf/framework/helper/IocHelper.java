package org.xmf.framework.helper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmf.framework.annotation.Autowired;
import org.xmf.framework.util.ArrayUtil;
import org.xmf.framework.util.CollectionUtil;
import org.xmf.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 依赖注入助手类
 */
public final class IocHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(IocHelper.class);


    static {

        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            //遍历bean map
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                //从BeanMap中获取Bean类与Bean实例
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();

                //获取bean类定义的所有成员变量
                Field[] beanFields = beanClass.getDeclaredFields();

                if (ArrayUtil.isNotEmpty(beanFields)) {
                    //遍历 bean field
                    for (Field beanField : beanFields) {

                        //判断是否带有 Autowired 注解
                        if (beanField.isAnnotationPresent(Autowired.class)) {
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            LOGGER.error(String.valueOf(beanFieldInstance.getClass()));
                            LOGGER.error(String.valueOf(beanField.getClass()));
                            if (beanFieldInstance != null) {
                                //通过反射初始化 beanField 的值
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }

                    }
                }

            }
        }
    }
}
