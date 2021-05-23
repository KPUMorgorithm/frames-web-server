package com.example.restapitest2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.restapitest2.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    //연-월-일 시:분:초 형태로 출력하게끔 정하는 메서드
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "sms 리시버 실행 되었음");

        //intent의 내용을 bundle에 넣는다.
        Bundle bundle = intent.getExtras();

        //sms 메세지가 한 개가 아니므로 배열로 만든다.
        SmsMessage[] messages = parseSmsMessage(bundle);

        //메세지 내용이 있을 경우 작동
        if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Log.d(TAG, "onReceive: SMS를 수신하였습니다");

            //보낸 사람
            String sender = messages[0].getOriginatingAddress();
            Log.d(TAG, "onReceive: sender:" + sender);

            //받은 날짜
            Date receivedDate = new Date(messages[0].getTimestampMillis());
            Log.d(TAG, "onReceive: receivedDate: " + receivedDate);

            //내용
            String content = messages[0].getMessageBody();
            Log.d(TAG, "onReceive: contents: " + content);

            sendToActivity(context, sender, content, dateFormat.format(receivedDate));
            Log.d(TAG, "pass");
        }
    }
    private void sendToActivity(Context context, String sender, String content, String receivedDate){

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("sender", sender);
        intent.putExtra("contents", content);
        intent.putExtra("receivedDate", receivedDate);
        System.out.println("Message received!!! MyBroadCastReceiver");
        System.out.println("(main) sender : " + sender);
        System.out.println("(main) content : " + content);
        System.out.println("(main) receivedDate : " + receivedDate);
        context.startActivity(intent);

    }
    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        //pdus에 메세지가 담겨있다.
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i = 0; i < objs.length; i++) {
            //M버젼 (API 23 마시멜로우)이상일 때와 아닐때의 메세지 저장 형식 지정
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }
        return messages;
    }
}
