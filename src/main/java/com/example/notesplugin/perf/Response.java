package com.example.notesplugin.perf;

public class Response<T> {
    String actionName;
    T obj;

    public Response(String actionName, T obj) {
        this.actionName = actionName;
        this.obj = obj;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
