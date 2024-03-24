package com.example.notesplugin.perf;

import com.example.notesplugin.Debug;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

public class Perf {

    public static void perf(String actionName, Action action) {
        Date start = new Date();
        action.doAction();
        Date end = new Date();
        if (Debug.debug) {
            System.out.println("Action = [" + actionName + "] took = [" + Duration.between(start.toInstant(), end.toInstant()).toMillis() + "] ms ");
        }
    }

    public static  <T> T perf(String actionName, ActionR<T> action) {
        Date start = new Date();
        Response<T> tResponse = action.doAction();
        Date end = new Date();
        if (Debug.debug) {
            System.out.println("Action = [" + (Optional.ofNullable(actionName).orElseGet(tResponse::getActionName)) + "] took = [" + Duration.between(start.toInstant(), end.toInstant()).toMillis() + "] ms ");
        }
        return tResponse.getObj();
    }


}
