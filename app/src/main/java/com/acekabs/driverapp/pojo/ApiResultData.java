package com.acekabs.driverapp.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Adee09 on 4/20/2017.
 */

public class ApiResultData {
    @SerializedName("result")
    private String result;
    @SerializedName("info")
    private String info;
    @SerializedName("code")
    private String code;

    public ApiResultData() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
