package com.knight.plugin.download;

public interface ProcessFinish {
    void processFinished(Boolean success, String output, String file_name);
}
