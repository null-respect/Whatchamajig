package org.dimigo.whatchamajig;

/**
 * Created by nitin on 9/14/2015.
 */
public class ListRowModel {
    private String id;
    private String title;
    private String content;
    private String date;



    public ListRowModel(String id, String title, String content, String date) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {

        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}