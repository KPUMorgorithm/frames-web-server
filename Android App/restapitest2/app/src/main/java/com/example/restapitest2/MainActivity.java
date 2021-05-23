package com.example.restapitest2;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends Activity {
    private static final String TAG = "smstodb";
    private static String content;
    private static String receivedDate;
    private static String sender;
    //위험 권한 체크
    //manifest와 java에 둘 다 권한 허가받는 코드를 작성한다.
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.RECEIVE_SMS
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
//android.permission/ReCE
    private void processIntent(Intent intent) {
        if(intent != null){
            sender = intent.getStringExtra("sender");
            content = intent.getStringExtra("contents");
            receivedDate = intent.getStringExtra("receivedDate");
            Log.d(TAG, "(main) sender : " + sender);
            Log.d(TAG, "(main) content : " + content);
            Log.d(TAG, "(main) receivedDate : " + receivedDate);
            System.out.println("Message received!!!");
            System.out.println("(main) sender : " + sender);
            System.out.println("(main) content : " + content);
            System.out.println("(main) receivedDate : " + receivedDate);
             ExampleThread thread = new ExampleThread();
             thread.start();

           // savetodb task = new savetodb();
         //   task.execute(sender, content, receivedDate);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
        super.onNewIntent(intent);
    }
    //*******************************************************
    //*******************************************************
    //******************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkDangerousPermissions();
       // ExampleThread thread = new ExampleThread();
      //  thread.start();
    }


    private class ExampleThread extends Thread {

        public ExampleThread() {
            // 초기화 작업

            }
            public void run() {
            // 스레드에게 수행시킬 동작들 구현
                sendJsonDataToServer();
            }
        }

    static public String sendJsonDataToServer(){
        OutputStream os=null;
        InputStream is=null;
        ByteArrayOutputStream baos=null;
        HttpURLConnection conn=null;
        String response="";

        String json = "";

        // 3. build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("sender", sender);
            jsonObject.accumulate("content", content);
            jsonObject.accumulate("receivedDate", receivedDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // 4. convert JSONObject to JSON to String
        json = jsonObject.toString();
        System.out.println("json toString:"+json);
        try{
            URL url=new URL("http://15.164.144.129/sms");
            conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setReadTimeout(5*1000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cache-Control","no-cache");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            os=conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            int responseCode=conn.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK){
                is=conn.getInputStream();
                baos=new ByteArrayOutputStream();
                byte[] byteBuffer=new byte[1024];
                byte[] byteData=null;
                int nLength=0;

                while((nLength=is.read(byteBuffer, 0, byteBuffer.length))!=-1){
                    baos.write(byteBuffer,0,nLength);
                }
                byteData=baos.toByteArray();

                response=new String(byteData);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch(Exception e){
            return null;
        }
        return response;
    }

}