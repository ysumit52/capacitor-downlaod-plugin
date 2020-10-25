package com.knight.plugin.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;


import androidx.core.content.FileProvider;

import com.getcapacitor.PluginCall;

import org.json.JSONObject;

import java.io.File;

public class InstallService {

    Context _context;
    ProcessFinish _processFinish;
    PluginCall _call;
    private static final String PARAM_CONTENT_TYPE = "contentType";
    private static final String PARAM_FILE_PATH = "filePath";

    public InstallService(Context context, ProcessFinish processFinish, PluginCall call){
        this._context = context;
        this._processFinish = processFinish;
        this._call = call;
    }

    public void new_install(File file_doc, JSONObject jsonParam){
        try{
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String fileProviderName = _context.getPackageName() + ".capacitordownloadplugin.fileprovider";
//            File cachedFile = new File(getCacheDir(), file_doc);
            String contentType = jsonParam.getString(PARAM_CONTENT_TYPE);
            Uri data = DownloadFileProvider.getUriForFile(_context, fileProviderName,file_doc);
//            intent.setDataAndType(data,"application/vnd.android.package-archive");
            intent.setDataAndType(data, contentType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
            _processFinish.processFinished(true,"Done", "Completed");
        }catch (Exception e){
            _processFinish.processFinished(false, e.getMessage(), "Error In Installation");
            Log.e("New Function :" , e.getMessage());
        }
    }

}
