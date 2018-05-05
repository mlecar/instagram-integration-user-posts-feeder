#### Instagram user posts feeder
Service that given instagram user returns a RSS document with recent posts from that user

#### How to start the app?
java -jar instagram-user-post-reader-1.0.0-SNAPSHOT.jar

#### How to get posts by user?
Just open your browser and go to:

    http://localhost:8080/instagram/user/metallica/posts/atom

OR

    http://localhost:8080/instagram/user/metallica/posts/rss

### You can do that with curl too
    curl http://localhost:8080/instagram/user/metallica/posts/atom

OR

    curl http://localhost:8080/instagram/user/metallica/posts/rss

