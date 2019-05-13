package com.moverzp.wenda.controller;

import com.moverzp.wenda.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller
public class IndexController {
    @RequestMapping(path = {"/", "index"})
    @ResponseBody
    public String index() {
        return "Hello moverzp.";
    }

//    @RequestMapping(path = {"/profile/{userId}"})
//    @ResponseBody
//    public String profile(@PathVariable("userId") int userId,
//                          @RequestParam(value = "type", defaultValue = "0") int type,
//                          @RequestParam(value = "key", defaultValue = "default key", required = false) String key) {
//        return String.format("Profile Page of %d, type:%d, key:%s", userId, type, key);
//    }

    @RequestMapping("/hello")
    public String hello(Model model, @RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        model.addAttribute("name", name);

        List<String> colors = Arrays.asList(new String[]{"red", "green", "blue"});
        model.addAttribute("colors", colors);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }
        model.addAttribute("map", map);

        model.addAttribute("user", new User("moverzp"));

        return "hello";
    }
}
