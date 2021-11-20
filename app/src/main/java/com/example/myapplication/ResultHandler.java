package com.example.myapplication;

import java.io.IOException;

public interface ResultHandler {
    void onSuccess(String response);
    void onFail(IOException error);
}