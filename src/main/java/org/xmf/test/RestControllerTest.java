package org.xmf.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmf.framework.annotation.Autowired;
import org.xmf.framework.annotation.GetMapping;
import org.xmf.framework.annotation.RestController;
import org.xmf.framework.bean.Data;
import org.xmf.framework.bean.Param;

@RestController
public class RestControllerTest {

    private static Logger LOGGER = LoggerFactory.getLogger(RestControllerTest.class);

    @Autowired
    private HelloService helloService;

    @GetMapping("/testapi")
    public Data test(Param param){
        return new Data(new JsonData());
    }

    @GetMapping("/sayHello")public Data sayHello(Param param) {
        String hello = helloService.sayHello();
        return new Data(hello);
    }
}
