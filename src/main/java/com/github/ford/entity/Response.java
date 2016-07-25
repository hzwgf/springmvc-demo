package com.github.ford.entity;

import java.io.Serializable;

public class Response<Result> implements Serializable {

    private static final long serialVersionUID = 5099154241595257653L;
    private int code;
    private Result result;

    public Response() {
    }

    public Response(int status) {
        this.code = status;
    }

    public Response(int status, Result result) {
        this.code = status;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int status) {
        this.code = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
