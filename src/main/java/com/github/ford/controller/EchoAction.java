/**
 * 
 */
package com.github.ford.controller;

import com.github.ford.entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author ford
 * 
 */
@Controller
@RequestMapping("/echo")
public class EchoAction {

    private static final Logger logger= LoggerFactory.getLogger(EchoAction.class);

    private AtomicInteger reqCount=new AtomicInteger();

    @RequestMapping("/hello")
    @ResponseBody
    public Response<Serializable> hello(HttpServletRequest request) throws InterruptedException {
        logger.info("req count:"+reqCount.incrementAndGet());
        synchronized (reqCount){
            reqCount.wait();
        }
        Response<Serializable> response = new Response<Serializable>();
        String data = request.getParameter("data");
        response.setCode(0);
        response.setResult("hello nginx.");
        return response;
    }


}
