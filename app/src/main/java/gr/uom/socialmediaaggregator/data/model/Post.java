package gr.uom.socialmediaaggregator.data.model;

import android.net.Uri;

import java.util.Date;

import gr.uom.socialmediaaggregator.data.Platform;

public class Post {

    private String user;
    private String body;
    private Date postedDate;
    private Uri mediaUri;
    private Platform platform;
    private Uri redirectUri = null;

    private Post() {}

    public static class Builder {

        private String user = "";
        private String body = "";
        private Date postedDate = new Date();
        private Platform platform = null;
        private Uri redirectUri = null;

        public Builder() {}

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setPostedDate(Date postedDate) {
            this.postedDate = postedDate;
            return this;
        }

        public Builder setPlatform(Platform platform) {
            this.platform = platform;
            return this;
        }

        public Builder setRedirectUri(Uri redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Post build() {
            Post post = new Post();
            post.user = this.user;
            post.body = this.body;
            post.postedDate = this.postedDate;
            post.platform = this.platform;
            post.redirectUri = this.redirectUri;
            return post;
        }

    }

    public String getUser() {
        return user;
    }

    public String getTruncatedBody(int length) {
        if (body.length() <= length)
            return body;
        return body.substring(0, length - 3) + "...";
    }

    public String getBody() {
        return body;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Uri getRedirectUri() {
        return redirectUri;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }


}
