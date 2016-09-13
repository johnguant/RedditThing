package com.johnguant.redditthing;

import org.json.JSONException;
import org.json.JSONObject;

public class Post {

    String title;
    String author;
    int score;
    String linkFlairText;
    String subreddit;
    String domain;
    String previewImage;
    String kind;
    String id;
    String url;

    Post (JSONObject post){
        try {
            title = post.getJSONObject("data").getString("title");
            author = post.getJSONObject("data").getString("author");
            score = post.getJSONObject("data").getInt("score");
            linkFlairText = post.getJSONObject("data").getString("link_flair_text");
            subreddit = post.getJSONObject("data").getString("subreddit");
            domain = post.getJSONObject("data").getString("domain");
            previewImage = post.getJSONObject("data").getString("thumbnail");
            kind = post.getString("kind");
            id  = post.getJSONObject("data").getString("id");
            url  = post.getJSONObject("data").getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
