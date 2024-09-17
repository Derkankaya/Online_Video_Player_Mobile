package com.example.videp_v1.model;

public class ContentModel {
    private String id, publisher,   video_description,
            videoUrl, thumbnailUrl,
             views, video_title, date;

    public ContentModel(String id, String publisher,
                        String video_description, String videoUrl, String thumbnailUrl,
                         String views, String video_title, String date) {





        this.id = id;
        this.publisher = publisher;
        this.thumbnailUrl = thumbnailUrl;


        this.video_description = video_description;
        this.videoUrl = videoUrl;
        this.views = views;
        this.video_title = video_title;
        this.date = date;
    }

    // Boş constructor kaldırıldı

    // Getter ve setter metotları eklendi


    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }




    public String getVideo_description() {
        return video_description;
    }

    public void setVideo_description(String video_description) {
        this.video_description = video_description;
    }



    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
