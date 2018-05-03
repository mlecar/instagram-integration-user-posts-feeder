package com.mlc.instagram.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

@Component
public class FeedGenerator {

    @Autowired
    @Value("${instagram.url}")
    private String instagramUrl;

    @Autowired
    private ContentReader contentReader;

    public String generateFeed(String content, String userId, String feedType) throws FeedException {

        String jsonContent = contentReader.search(content);

        SyndFeed feed = new SyndFeedImpl();

        JsonElement element = new Gson().fromJson(jsonContent, JsonElement.class);

        JsonObject user = element.getAsJsonObject().get("entry_data").getAsJsonObject().get("ProfilePage").getAsJsonArray().get(0).getAsJsonObject().get("graphql").getAsJsonObject().get("user").getAsJsonObject();

        JsonObject media = user.getAsJsonObject().get("edge_owner_to_timeline_media").getAsJsonObject();

        JsonArray posts = media.get("edges").getAsJsonArray();

        feed.setFeedType(feedType);

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

            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(post.getAsJsonObject().get("node").getAsJsonObject().get("edge_media_to_caption").getAsJsonObject().get("edges").getAsJsonArray().get(0).getAsJsonObject().get("node").getAsJsonObject().get("text").getAsString());
            entry.setLink(post.getAsJsonObject().get("node").getAsJsonObject().get("display_url").getAsString());
            entry.setPublishedDate(Date.from(Instant.ofEpochSecond(post.getAsJsonObject().get("node").getAsJsonObject().get("taken_at_timestamp").getAsLong())));

            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue("Instagram posts by user");
            entry.setDescription(description);

            entries.add(entry);
        }

        feed.setEntries(entries);

        return new SyndFeedOutput().outputString(feed);

    }

}
