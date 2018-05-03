package com.mlc.instagram.user;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource(value = "classpath:application-test.properties")
public class FeedGeneratorTest {

    @InjectMocks
    private FeedGenerator feedGenerator;

    @MockBean
    private ContentReader contentReader;

    @Value("${content.test}")
    private String jsonContent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(feedGenerator, "instagramUrl", "a.url.com/{userId}");

        when(contentReader.search(anyString())).thenReturn(jsonContent);
    }

    @Test
    public void successFeed() throws FeedException {

        String feedString = feedGenerator.generateFeed("some text", "marcelolecar", "rss_2.0");

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new StringReader(feedString));

        assertEquals(feed.getFeedType(), "rss_2.0");
        assertEquals(feed.getTitle(), "Instagram posts by marcelolecar");
        assertEquals(feed.getLink(), "a.url.com/marcelolecar");
        assertEquals(feed.getDescription(), "Instagram posts by marcelolecar");
    }

}
