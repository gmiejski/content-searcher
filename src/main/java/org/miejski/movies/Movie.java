package org.miejski.movies;


import java.util.List;

public class Movie {

    private String id;
    private String title;
    private List<String> tags;
    private String link;

    public Movie(String id, String title, List<String> tags, String link) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.link = link;
    }

    public Movie() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getLink() {
        return link;
    }
}

