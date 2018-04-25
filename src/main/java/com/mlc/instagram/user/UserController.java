package com.mlc.instagram.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

@RestController
@RequestMapping(value = { "/instagram", "/instagram/" })
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Value("${instagram.url}")
    private String instagramUrl;

    @ResponseBody
    @GetMapping(value = { "/user/{userId}/posts/rss", "/user/{userId}/posts/rss/" }, produces = { MediaType.APPLICATION_RSS_XML_VALUE })
    public String getUserPosts(@PathVariable String userId) throws FeedException {

        ResponseEntity<String> userContent = restTemplate.getForEntity(instagramUrl, String.class, userId);

        Scanner scanner = new Scanner(userContent.getBody());

        SyndFeed feed = new SyndFeedImpl();

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains("window._sharedData = ")) {
                String userPosts = line.replaceFirst("<script type=\"text/javascript\">window._sharedData = ", "").replace(";</script>", "");

                JsonElement element = new Gson().fromJson(userPosts, JsonElement.class);

                JsonObject user = element.getAsJsonObject().get("entry_data").getAsJsonObject().get("ProfilePage").getAsJsonArray().get(0).getAsJsonObject().get("graphql").getAsJsonObject().get("user").getAsJsonObject();

                JsonObject media = user.getAsJsonObject().get("edge_owner_to_timeline_media").getAsJsonObject();
                int postsQt = media.get("count").getAsInt();

                JsonArray posts = media.get("edges").getAsJsonArray();

                feed.setFeedType("rss_2.0");

                feed.setTitle("Instagram posts by " + userId);
                feed.setLink(instagramUrl.replace("{userId}", userId));
                feed.setDescription("Instagram posts by " + userId);

                SyndImage profilePic = new SyndImageImpl();
                profilePic.setTitle("Profile Picture");
                profilePic.setUrl(user.get("profile_pic_url_hd").getAsString());
                profilePic.setLink(user.get("profile_pic_url_hd").getAsString());
                feed.setImage(profilePic);

                SyndImage icon = new SyndImageImpl();
                icon.setTitle("Profile Icon");
                icon.setUrl(user.get("profile_pic_url").getAsString());
                icon.setLink(user.get("profile_pic_url").getAsString());
                feed.setIcon(icon);
                List<SyndEntry> entries = new ArrayList<>();

                for (JsonElement post : posts) {

                    SyndEntry entry;
                    SyndContent description;

                    entry = new SyndEntryImpl();
                    entry.setTitle(post.getAsJsonObject().get("node").getAsJsonObject().get("edge_media_to_caption").getAsJsonObject().get("edges").getAsJsonArray().get(0).getAsJsonObject().get("node").getAsJsonObject().get("text").getAsString());
                    entry.setLink(post.getAsJsonObject().get("node").getAsJsonObject().get("display_url").getAsString());
                    entry.setPublishedDate(Date.from(Instant.ofEpochSecond(post.getAsJsonObject().get("node").getAsJsonObject().get("taken_at_timestamp").getAsLong())));
                    description = new SyndContentImpl();
                    description.setType("text/plain");
                    description.setValue("Initial release of ROME");
                    entry.setDescription(description);
                    entries.add(entry);

                    String title = "Instagram posts by " + user.get("username").getAsString();
                    String link = post.getAsJsonObject().get("node").getAsJsonObject().get("display_url").getAsString();
                    String descr = post.getAsJsonObject().get("node").getAsJsonObject().get("edge_media_to_caption").getAsJsonObject().get("edges").getAsJsonArray().get(0).getAsJsonObject().get("node").getAsJsonObject().get("text").getAsString();
                    Instant date = Instant.ofEpochSecond(post.getAsJsonObject().get("node").getAsJsonObject().get("taken_at_timestamp").getAsLong());
                    System.out.println(date + " - " + title + " - " + link + " - " + descr);
                }

                feed.setEntries(entries);
            }
        }

        scanner.close();

        return new SyndFeedOutput().outputString(feed);
    }

}
