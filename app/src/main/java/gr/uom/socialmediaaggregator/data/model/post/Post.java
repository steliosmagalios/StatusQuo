package gr.uom.socialmediaaggregator.data.model.post;

import android.net.Uri;

import java.util.Date;

public class Post {

    private String user;
    private String body;
    private Date postedDate;
    private Platform platform;
    private Uri redirectUri = null;

    private Post() {}

    public String getUser() {
        return user;
    }

    public String getTruncatedBody(int length) {
        if (body.length() <= length)
            return body;
        return body.substring(0, length - 3) + "...";
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Uri getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(Uri redirectUri) {
        this.redirectUri = redirectUri;
    }

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

}
