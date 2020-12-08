package com.knight.plugin.download;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.getcapacitor.PluginCall;
import com.knight.plugin.download.capacitordownloadplugin.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

class NewDownloadService extends AsyncTask<String, String, File> {

    private String _pathFolder = "";
    private String _random__pathFolder = "";
    private String _url = "";
    private String _file_name = "";
    private ProcessFinish _processFinish;
    public Context _context;
    private ProgressDialog pd;
    private String exception = null;
    private Boolean isException = false;
    private Boolean _downloadAndInstall ;
    private File myExternalFile;
    private PluginCall _call;
    private static final String PARAM_FILENAME = "fileName";
    private static final String PARAM_INSTALL = "downloadAndInstall";
    private static final String PARAM_FOLDER_NAME = "folderName";
    private static final String PARAM_URL = "url";
    private static final String PARAM_RANDOM_PATH = "DownloadPlugin";
    private JSONObject _jsonObject;

    public NewDownloadService(ProcessFinish processFinish, Context context, PluginCall call, JSONObject jsonObject){
        this._processFinish = processFinish;
        this._context = context;
        this._call = call;
        this._jsonObject = jsonObject;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(_context, R.style.MyAlertDialogStyle);
        pd.setTitle("Processing...");
        pd.setMessage("Please wait.");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
        try {
            _file_name = _jsonObject.getString(PARAM_FILENAME);
            _downloadAndInstall = _jsonObject.getBoolean(PARAM_INSTALL);
            _pathFolder = _jsonObject.getString(PARAM_FOLDER_NAME);
            _random__pathFolder = _jsonObject.getString(PARAM_RANDOM_PATH);
            _url = _jsonObject.getString(PARAM_URL);

        } catch (JSONException e) {
           cancel(true);
        }
    }

    @Override
    protected File doInBackground(String... f_url) {
        int count;
        try {
            pd.setTitle("Downloading ...");
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());

            myExternalFile = new File(_context.getExternalFilesDir(_random__pathFolder), _file_name);
//            _file_path = _pathFolder + '/' + _file_name;
            FileOutputStream output = new FileOutputStream(myExternalFile);

            byte[] data = new byte[1024];
            float total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                if (total > 0){
                    float i = total/(1024 * 1024);
                    BigDecimal numberBigDecimal = new BigDecimal(i);
                    numberBigDecimal  = numberBigDecimal .setScale(2, BigDecimal.ROUND_HALF_UP);
                    pd.setMessage("Downloaded: " + numberBigDecimal + " MB");
                }

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            exception = e.getMessage();
            isException = true;
        }

        return myExternalFile;
    }
    @Override
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        super.onProgressUpdate(progress);
        pd.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(File file_url) {
        if (pd!=null) {
            pd.dismiss();
        }

        String file_name = "";
        if(isCancelled()){
            _processFinish.randomProcessFinished(false,"Cancelled" , _file_name,_pathFolder, _random__pathFolder);
        }
        else if(isException){
            _processFinish.processFinished(false, exception, _file_name);
        }
        else{
            if(_downloadAndInstall){
                InstallService installService = new InstallService(_context, _processFinish, _call);
                installService.new_install(file_url, _jsonObject);
            }else{
                Log.e("LenovoGAO Message :" , "Downloaded and installed");
                _processFinish.randomProcessFinished(true,"Done",_file_name, _pathFolder, _random__pathFolder);
            }

        }
    }

}
