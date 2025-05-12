package com.example.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.google.cloud.texttospeech.v1.*;
import com.google.cloud.texttospeech.v1beta1.SsmlVoiceGender;
import com.google.protobuf.ByteString;

public class Story {
    private String name;
    private String author;
    private String title;
    private String story;
    private ByteString audiofile;

    public Story(String name, String author,String title, String story){
        this.name = name;
        this.title = title;
        this.author = author;
        this.story = story;
    }


    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getStory() {
        return story;
    }

    public ByteString getAudiofile() {
        return audiofile;
    }

    public String getTitle() {
        return title;
    }

    public void sanitiseStory() {
        
    }


//generate the story audio using google tts api
public void createAudioFile() throws Exception{
    try(TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {

        SynthesisInput input = SynthesisInput.newBuilder().setText(story).build();

        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("en-GB").build();

        AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

        ByteString audioContents = response.getAudioContent();
        System.out.println(audioContents.toString());

        this.audiofile = audioContents;
    }
}

public boolean saveAudioFile() throws Exception{
    try{
        File audioFile = new File(this.title);

        OutputStream out = new FileOutputStream(audioFile);
        out.write(this.audiofile.toByteArray());

        return true;
    }
    catch(Exception e){
        System.out.println(e);
        return false;
    }
}
}
 