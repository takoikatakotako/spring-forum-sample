package com.swiswiswift.forum;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class ForumController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String healthCheck() {
        return "success";
    }


    @RequestMapping(value = "/api/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ArrayList<String> getPostList(HttpServletResponse response) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Hello");
        return list;
    }

    @RequestMapping(value = "/api/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String addPost(HttpServletResponse response) {
        return "Hello";
    }
}
