package com.careerdevs.gorestfinal.models;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Post {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    private long user_id;

    private String title;


    @Column(length =512)
    private String body;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", user_id=" + user_id +
                ", body='" + body + '\'' +
                '}';
    }
}