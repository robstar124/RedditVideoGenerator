package com.example.app;

import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class RedditProcessor {
    private URL url;
    private ArrayList<Story> stories = new ArrayList<>();


    
    public void setUrl(String url) throws IOException {
        this.url = URI.create(url).toURL();
    }

    public URL getUrl() {
        return url;
    }
   public ArrayList<Story> getStories() {
        return stories;
    }


    /*
     * This gets 25 responses from the reddit API
     */
    public ArrayList<Story> getStoriesFromURL(URL url) throws Exception{
        InputStream in = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line;

        ArrayList<Story> stories = new ArrayList<>();

        while ((line = br.readLine()) != null){
            sb.append(line);
        }
        br.close();
        
        JSONObject response = new JSONObject(sb.toString());
        JSONArray storiesList = response.getJSONObject("data").getJSONArray("children");


        for (int i = 0; i < storiesList.length(); i++){

            String name = storiesList.getJSONObject(i).getJSONObject("data").getString("name");
            String author = storiesList.getJSONObject(i).getJSONObject("data").getString("author");
            String title = storiesList.getJSONObject(i).getJSONObject("data").getString("title");
            String story = storiesList.getJSONObject(i).getJSONObject("data").getString("selftext");


            Story newStory = new Story(name, author, title, story);
            stories.add(newStory);
        } 


        return stories;
    }

    /*
     * Get top 100 posts in the last month for a subreddit
     * 
     * Reddit Json API returns 25 posts by default so 4 api requests are made per subreddit.
     */
    public void processStories(int numOfPosts) throws Exception{

        int limit = (int) Math.ceil((double) numOfPosts / 25);

        System.out.println(limit);
        

        String after;  
        
        for(int i = 0; i < limit; i++){
            if (this.stories.isEmpty()){
                this.stories.addAll(getStoriesFromURL(this.url));
            }
            else{
                after = stories.get(stories.size() - 1).getName();
                URL currUrl = new URI(this.url.toString() + "&after=" + after).toURL();
                this.stories.addAll(getStoriesFromURL(currUrl));
            }
        }
        
    }

}

