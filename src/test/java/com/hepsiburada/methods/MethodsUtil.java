package com.hepsiburada.methods;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MethodsUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public void waitByMilliSeconds(long milliSeconds){

        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitBySeconds(long seconds){

        waitByMilliSeconds(seconds*1000);
    }

}