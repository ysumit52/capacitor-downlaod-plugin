package com.knight.plugin.download;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONException;
import org.json.JSONObject;

@NativePlugin
public class DownloadPlugin extends Plugin implements ProcessFinish {

    Context context;
    private ProcessFinish _process;
    PluginCall _call;
    JSObject jsObject;
    JSONObject jsonObject;
    private static final String PARAM_FILENAME = "fileName";
    private static final String PARAM_INSTALL = "downloadAndInstall";
    private static final String PARAM_FOLDER_NAME = "folderName";
    private static final String PARAM_CONTENT_TYPE = "contentType";
    private static final String PARAM_URL = "url";
    @PluginMethod()
    public void download(PluginCall call) {
        _call = call;
        jsObject = new JSObject();
//        String url_value = call.getString("url");
//        String _path = call.getString("folderName");
//        String _file_name = call.getString("fileName");
//        Boolean _downLoadAndInstall = call.getBoolean("downloadAndInstall");
        String _file_name = ConfigUtils.getCallParam(String.class, _call, PARAM_FILENAME);
        if (_file_name == null || _file_name.length() == 0) {
            returnIfError("File name not present");
        }

        Boolean _downLoadAndInstall = ConfigUtils.getCallParam(Boolean.class, _call, PARAM_INSTALL);
        if (_downLoadAndInstall == null) {
            returnIfError("Parameter want to install apk not present");
        }

        String _pathFolder = ConfigUtils.getCallParam(String.class, _call, PARAM_FOLDER_NAME);
        if (_pathFolder == null || _pathFolder.length() == 0) {
            returnIfError("Folder name not present");
        }

        String contentType = ConfigUtils.getCallParam(String.class, _call, PARAM_CONTENT_TYPE);
//        if (contentType == null || contentType.length() == 0) {
//            returnIfError("Content Type parameter not present");
//        }

        String url = ConfigUtils.getCallParam(String.class, _call, PARAM_URL);
        if (_pathFolder == null || _pathFolder.length() == 0) {
            returnIfError("URL parameter not present");
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put(PARAM_FILENAME, _file_name);
            obj.put(PARAM_FOLDER_NAME, _pathFolder);
            obj.put(PARAM_CONTENT_TYPE, contentType);
            obj.put(PARAM_INSTALL, _downLoadAndInstall);
            obj.put(PARAM_URL, url);

            context = getContext();
            _process = this;
            PermissionService permissionService = new PermissionService(_process, context, _call);
            permissionService.requestStoragePermission(obj);
        } catch (JSONException e) {
            returnIfError(e.getMessage());
        }
    }

    @PluginMethod()
    public void checkFilePresentOrNot(PluginCall call) {
        jsObject = new JSObject();
        String filePath = call.getString("fileNamePath");
       try{
        File fileName = getContext().getExternalFilesDir(null);
        File file = new File(fileName, '/' + filePath);
        if(file.exists()){
            jsObject.put("Message", "File Already Present");
            jsObject.put("Success", true);
        }else{
            jsObject.put("Message", "File Not Found");
            jsObject.put("Success", false);
        }
       } catch(Exception error) {
        jsObject.put("Message", "Permission not available");
        jsObject.put("Success", false);
       }
        call.success(jsObject);
    }


    @Override
    public void processFinished(Boolean success, String output, String file_name) {
        jsObject.put("Message", output);
        jsObject.put("Success", success);
        jsObject.put("Data", file_name);
        _call.success(jsObject);
    }

    public JSONObject getJSONData(){
        JSONObject obj = new JSONObject();
        try {
            obj.put(PARAM_FILENAME, "foo");
            obj.put(PARAM_FOLDER_NAME, new Integer(100));
            obj.put(PARAM_CONTENT_TYPE, new Double(1000.21));
            obj.put(PARAM_INSTALL, new Boolean(true));
        } catch (JSONException e) {
            returnIfError(e.getMessage());
            return null;
        }
        return obj;
    }

    public void returnIfError(String message){
        jsObject.put("Message", message);
        jsObject.put("Success", false);
        _call.success(jsObject);
    }
}

