package com.mlc.instagram.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rometools.rome.feed.rss.Channel;

@RestController
@RequestMapping(value = { "/instagram/user", "/instagram/user" })
public class UserController {

    @ResponseBody
    @GetMapping(value = { "/rss/", "/rss" }, produces = { "application/json" })
    public Channel getUserPosts() {
        return new Channel();
    }

}
