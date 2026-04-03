package com.Day29.IMSfinal;

class EmailNotifier implements Notifier {
    public void send(String msg) {
        System.out.println("[EMAIL] " + msg);
    }
}