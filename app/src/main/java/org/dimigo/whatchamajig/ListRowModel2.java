package org.dimigo.whatchamajig;

/**
 * Created by nitin on 9/14/2015.
 */
public class ListRowModel2 {
    private String id;
    private String title;
    private String date;



    public ListRowModel2(String id, String title, String date) {

        this.id = id;
        this.title = title;

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



    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {

        return title;
    }



    public String getDate() {
        return date;
    }
}