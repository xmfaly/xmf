package org.xmf.test;

import org.xmf.framework.annotation.Controller;
import org.xmf.framework.annotation.GetMapping;
import org.xmf.framework.annotation.PostMapping;
import org.xmf.framework.bean.Param;
import org.xmf.framework.bean.View;

@Controller
public class ControllerTest {

    @GetMapping("/hello")
    public View helloGet(Param param) {
        String name = param.getString("name");
        return new View("hello.jsp").addModel("name",name);
    }

    @PostMapping("/hello")
    public View helloPost(Param param) {
        return new View("phello.jsp");
    }
}
