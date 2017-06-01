package csd.hx.com.smsmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sober_philer on 2017/6/1.
 * 从短信内容中提取验证码
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("sober_philer", "receive");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            // 判断是否有数据
            if (bundle != null) {
                // 通过pdus可以获得接收到的所有短信消息
                Object[] pdus = (Object[]) bundle.get("pdus");
                // 构建短信对象array,并依据收到的对象长度来创建array的大小
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                String displayMessageBody = messages[0].getDisplayMessageBody();
                if (displayMessageBody.contains("码")) {
                    String result = null;
                    Pattern p = Pattern.compile("\\d{4,}");//这个2是指连续数字的最少个数
//                    String u = "abc435345defsfsaf564565fsabad5467755fewfadfgea";
                    Matcher m = p.matcher(displayMessageBody);
                    while (m.find()) {
                        if (result == null) {
                            result = m.group();
                        }
                        if (result.length() == 6) {
                            break;
                        }
                    }
                    if (result != null) {
                        Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        copy(result, context);
                    }
                }
            }
        }
    }

    /**
     * 实现文本复制功能
     * add by wangqianzhou
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
}
