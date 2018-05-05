package com.mlc.instagram.user;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/* Just a test to try the solution*/
@RunWith(SpringRunner.class)
@ContextConfiguration
public class UserPostReaderTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void readPosts() throws IOException {

        Resource resource = resourceLoader.getResource("classpath:instagram.user.metallica.source.page.html");

        Scanner scanner = new Scanner(resource.getInputStream());

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains("window._sharedData = ")) {
                String userPosts = line.replaceFirst("<script type=\"text/javascript\">window._sharedData = ", "").replace(";</script>", "");

                JsonElement element = new Gson().fromJson(userPosts, JsonElement.class);

                JsonObject user = element.getAsJsonObject().get("entry_data").getAsJsonObject().get("ProfilePage").getAsJsonArray().get(0).getAsJsonObject().get("graphql").getAsJsonObject().get("user").getAsJsonObject();

                JsonObject media = user.getAsJsonObject().get("edge_owner_to_timeline_media").getAsJsonObject();
                int postsQt = media.get("count").getAsInt();

                JsonArray posts = media.get("edges").getAsJsonArray();

                for (JsonElement post : posts) {
                    String title = "Instagram posts by " + user.get("username").getAsString();
                    String link = post.getAsJsonObject().get("node").getAsJsonObject().get("display_url").getAsString();
                    String description = post.getAsJsonObject().get("node").getAsJsonObject().get("edge_media_to_caption").getAsJsonObject().get("edges").getAsJsonArray().get(0).getAsJsonObject().get("node").getAsJsonObject().get("text").getAsString();
                    Date date = new Date(post.getAsJsonObject().get("node").getAsJsonObject().get("taken_at_timestamp").getAsLong());
                }
            }
        }

        scanner.close();
    }

}
