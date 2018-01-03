package org.xmf.framework;

import org.xmf.framework.helper.BeanHelper;
import org.xmf.framework.helper.ClassHelper;
import org.xmf.framework.helper.ControllerHelper;
import org.xmf.framework.helper.IocHelper;
import org.xmf.framework.util.ClassUtil;

/**
 * 加载相应的 Helper
 */
public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}
