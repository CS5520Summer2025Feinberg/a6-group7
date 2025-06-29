package edu.northeastern.a6group7;

public class Sticker {
    private String sender;
    private String recipient;
    private String stickerId;
    private String timeStamp;

    public Sticker(String sender, String recipient, String stickerId, String timeStamp){
        this.sender = sender;
        this.recipient = recipient;
        this.stickerId = stickerId;
        this.timeStamp = timeStamp;
    }

    public String getSender(){
        return this.sender;
    }
    public String getRecipient(){
        return this.recipient;
    }
    public String getStickerId(){
        return this.stickerId;
    }
    public String getTimeStamp(){
        return this.timeStamp;
    }


    public void setSender(String sender){
        this.sender = sender;
    }

    public void setRecipient(String recipient){
        this.recipient = recipient;
    }
    public void setStickerId(String stickerId){
        this.stickerId = stickerId;
    }

    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }

}
