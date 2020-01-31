package com.tallate.cgh.controller;

public class Response<T> {

    private int code;

    private String message;

    private T data;

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.data = data;
        response.code = 0;
        return response;
    }

    public static <T> Response<T> fail(int code, String msg) {
        return new Response<>()
                .setCode(code)
                .setMessage(msg);
    }

    public int getCode() {
        return code;
    }

    public Response setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Response<T> setData(T data) {
        this.data = data;
        return this;
    }
}
