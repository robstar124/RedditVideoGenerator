package com.example.app;

import java.io.File;
import com.google.cloud.texttospeech.v1.*;

public class Story {
    private String name;
    private String author;
    private String title;
    private String story;
    private File audiofile;

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

    public File getAudiofile() {
        return audiofile;
    }

    public void sanitiseStory() {
        
    }

public void createAudioFile() throws Exception{

}
}
