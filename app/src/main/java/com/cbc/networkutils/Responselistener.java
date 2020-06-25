package com.cbc.networkutils;

public interface Responselistener {
    void Start();

    void OnSucess(String Responseobject);

    void OnError(String Error);

    void OnLogicError(int Code, String response, String msg);
}
