package com.Day29.IMSfinal;

class SMSNotifier implements Notifier {
    public void send(String msg) {
        System.out.println("[SMS] " + msg);
    }
}