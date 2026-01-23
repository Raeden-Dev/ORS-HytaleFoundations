package com.raeden.hytale.modules.chat;

import com.raeden.hytale.HytaleEssentials;

public class MailManager {
    private final HytaleEssentials hytaleEssentials;

    public MailManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
    }

    public static class Mail {
        private final String from;
        private final String to;
        private final String title;
        private final String timeSent;
        private final String message;
        private boolean read;

        public Mail(String from, String to, String title, String timeSent, String message) {
            this.from = from;
            this.to = to;
            this.title = title;
            this.timeSent = timeSent;
            this.message = message;
            this.read = false;
        }

        public String getFrom() {return from;}
        public String getTo() {return to;}
        public String getTitle() {return title;}
        public String getTimeSent() {return timeSent;}
        public String getMessage() {return message;}

        public boolean isRead() {return read;}
        public void setRead(boolean read) {this.read = read;}
    }
}
