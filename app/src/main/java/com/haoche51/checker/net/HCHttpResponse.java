package com.haoche51.checker.net;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class HCHttpResponse {
    private int errno;
    private String errmsg;
    private String data;

    private String result;


    public HCHttpResponse() {
        errno = -100;
        errmsg = "网络不给力，操作失败!";
    }

    public HCHttpResponse(int errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public HCHttpResponse(String result) {
        this.result = result;
        if (TextUtils.isEmpty(result)) {
            errno = -2;
        } else {
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.has("errno")) {
                    errno = obj.getInt("errno");
                }
                if (obj.has("errmsg")) {
                    errmsg = obj.getString("errmsg");
                }
                if (obj.has("data")) {
                    data = obj.getString("data");
                }
            } catch (JSONException e) {

            }
        }

    }

    public int getErrno() {
        return errno;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public <T> T getData(Type type) {
        return (new Gson()).fromJson(this.data, type);
    }

    public String getErrmsg() {
        return errmsg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
