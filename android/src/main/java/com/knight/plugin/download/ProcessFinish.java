package com.knight.plugin.download;
import java.io.File;


public interface ProcessFinish {
    void processFinished(Boolean success, String output, String file_name);
    void randomProcessFinished(Boolean success, String output, String file_name, String a_folder_path, String r_folder_path);
}
