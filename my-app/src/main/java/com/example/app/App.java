package com.example.app;

import java.io.IOException;

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
            API.processStories(100);
        }
        catch(Exception e){                                                                                                                                                                                                                                                                                     
            System.out.println(e);
        }
                
    
    }
}
