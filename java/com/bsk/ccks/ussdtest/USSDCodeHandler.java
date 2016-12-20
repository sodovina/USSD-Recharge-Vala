package ussdtranscom.bsk.transferokredit;

/**
 * Created by bsk' on 12/11/2016.
 */

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class USSDCodeHandler extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getClassName().equals("android.app.AlertDialog")) {
            String text = event.getText().toString();
            AccessibilityNodeInfo node = null;
            for (int i = 0; i < event.getSource().getChildCount(); i++)
                if (event.getSource().getChild(i).getClassName().toString().equals("android.widget.EditText"))
                    node = event.getSource().getChild(i);
            if (node != null) {
                if (text.toLowerCase().contains("Shtypni 1 per gjendjen e llogarise kryesore, 2 per gjendjen e nenllogarive".toLowerCase())) {
                    perforActionEditText(node, "1");
                    findAndPerformActionButton("Send");
                } else if (text.toLowerCase().contains("Ne llogarine e juaj kryesore keni".toLowerCase())) {
                    String a = event.getText().toString();
                    findAndPerformActionButton("Cancel");
                    a = a.substring(0, a.indexOf("euro")).trim();
                    a = a.substring(a.lastIndexOf(" ")).trim();
                    a = a.substring(0, a.length() - 1).replaceAll(",", ".").trim();
                    Intent intent = new Intent("com.times.transfero.action.REFRESH");
                    intent.putExtra("message", a);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } else if (text.toLowerCase().contains("Gjendja e llogarise suaj kryesore eshte zero".toLowerCase())) {
                    perforActionEditText(node, "2");
                    findAndPerformActionButton("Send");
                    findAndPerformActionButton("OK");
                    Intent intent = new Intent("com.times.transfero.action.REFRESH");
                    intent.putExtra("message", "0");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } else if (text.toLowerCase().contains("Keni kerkuar transaksionin e kredise prej".toLowerCase())) {
                    perforActionEditText(node, "1");
                    findAndPerformActionButton("Send");
                    findAndPerformActionButton("OK");
                }
            }
            findAndPerformActionButton("OK");
            Intent intent = new Intent("com.times.transfero.action.REFRESH");
            intent.putExtra("message", text);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void findAndPerformActionButton(String text) {
        if (getRootInActiveWindow() == null)
            return;
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void perforActionEditText(AccessibilityNodeInfo node, String text) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        System.out.println("mos e luej");
        Log.d("output:", "onServiceConnected");
        AccessibilityServiceInfo info = this.getServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        setServiceInfo(info);
    }
}