package com.careerdevs.gorestfinal.models;


import javax.persistence.*;



// @Entity represents persistent data stored in a relational database automatically using container-managed persistence.
@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private long id;
    private long user_id;
    private String title;
    private String due_on;
    private String status;

    public long getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDue_on() {
        return due_on;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Todos{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", due_on='" + due_on + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
