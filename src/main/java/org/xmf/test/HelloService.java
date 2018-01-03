package org.xmf.test;

import org.xmf.framework.annotation.Service;

@Service
public class HelloService {

    public String sayHello(){
        return "hello";
    }
}
