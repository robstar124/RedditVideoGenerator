package com.example.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RedditProcessor API = new RedditProcessor();

    
        try{
            API.setUrl("https://www.reddit.com/r/AmItheAsshole/top/.json?t=month");
            API.processStories(2);

            Story story = API.getStories().get(0);
            System.out.println(story.getStory());
            story.createAudioFile();
            
            File outputFile = new File("output.mp3");
            
            try(OutputStream out = new FileOutputStream(outputFile)) {
                out.write(story.getAudiofile().toByteArray());
            }
            catch(Exception e){
                System.out.println(e);
                
            }
            
            
        }
        catch(Exception e){                                                                                                                                                                                                                                                                                     
            System.out.println(e);
        }
                
    
    }
}
