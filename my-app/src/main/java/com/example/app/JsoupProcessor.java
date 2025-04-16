package com.example.app;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupProcessor {
    private String url;
    private ArrayList<String> postUrls = new ArrayList<>();
    private Document doc;

    public JsoupProcessor(String url) { 
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void getHTMLDoc(){
        try{
            this.doc = Jsoup.connect(url).get();
            //System.out.println(this.doc.html());
        }
        catch(IOException e){
            System.out.println("IO exception from URL parsing");
            e.printStackTrace();
        }
    }

    

    public void getRedditFeed(){

        Elements blocks = this.doc.select("shreddit-post");
        System.out.println(blocks.size());


        for(Element post : blocks){

            String link = post.attr("content-href");
            System.out.println(link);
        }


        //System.out.println(feed.html());
    }
    
}
