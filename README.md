# Instagram user posts feeder
Service that given instagram user returns a RSS document with recent posts from that user

### How to start the app?
java -jar instagram-user-post-reader-1.0.0-SNAPSHOT.jar

### Service Description

#### Atom

Request: **/instagram/user/{userId}/posts/atom**

Response Status: **200 OK** 

Response Content Type: **application/atom+xml**

#### RSS

Request: **/instagram/user/{userId}/posts/rss**

Response Status: **200 OK** 

Response Content Type: **application/rss+xml**

*Just substitute the path variable {userId} for the userId on instagram and posts will come to you*
*It is interesting using a RSS/Atom client tool to see the posts*

### How to get posts by user?
Just open your browser and go to:

    http://localhost:8080/instagram/user/metallica/posts/atom

OR

    http://localhost:8080/instagram/user/metallica/posts/rss

### You can do that with curl too
    curl http://localhost:8080/instagram/user/metallica/posts/atom

OR

    curl http://localhost:8080/instagram/user/metallica/posts/rss

