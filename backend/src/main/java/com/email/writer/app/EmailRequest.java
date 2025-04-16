package com.email.writer.app;

//@Data
public class EmailRequest {

    //Content of the email that you are crafting response for
    private String emailContent;

    //Tone of how you want the response to be. We could make it Professional, casual, sarcastic
    private String tone;

    public String getTone(){
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getEmailContent(){
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }
}

