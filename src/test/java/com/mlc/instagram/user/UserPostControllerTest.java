package com.mlc.instagram.user;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String baseContext = "/instagram/user/{userId}/posts";

    @MockBean
    private FeedGenerator feedGenerator;

    @MockBean
    private RestTemplate restTemplate;

    @Value("classpath:instagram.user.marcelolecar.source.page.html")
    private Resource resource;

    @Test
    public void successRss() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenReturn(ResponseEntity.ok("<!DOCTYPE html>test"));

        when(feedGenerator.generateFeed(anyString(), anyString(), anyString())).thenReturn("rss response");

        this.mockMvc.perform(get(baseContext + "/rss", "marcelolecar")).andExpect(status().isOk()).andExpect(content().contentType("application/rss+xml;charset=ISO-8859-1"));
    }

    @Test
    public void successAtom() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenReturn(ResponseEntity.ok("<!DOCTYPE html>test"));

        when(feedGenerator.generateFeed(anyString(), anyString(), anyString())).thenReturn("rss response");

        this.mockMvc.perform(get(baseContext + "/atom", "marcelolecar")).andExpect(status().isOk()).andExpect(content().contentType("application/atom+xml;charset=ISO-8859-1"));
    }

    @Test
    public void notFound() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenThrow(new RestClientResponseException("Not Found", HttpStatus.NOT_FOUND.value(), "404 Not Found", null, "Not Found".getBytes(), Charset.defaultCharset()));

        when(feedGenerator.generateFeed(anyString(), anyString(), anyString())).thenReturn("rss response");

        this.mockMvc.perform(get(baseContext + "/rss", "unexistent_instagram_page")).andExpect(status().isBadRequest());
    }

    @Test
    public void resourceAccessException() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenThrow(new ResourceAccessException("socket timeout"));

        when(feedGenerator.generateFeed(anyString(), anyString(), anyString())).thenReturn("rss response");

        this.mockMvc.perform(get(baseContext + "/rss", "unexistent_instagram_page")).andExpect(status().isBadGateway());
    }

}
