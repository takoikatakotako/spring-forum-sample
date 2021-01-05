package com.swiswiswift.forum;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class ForumController {

    private final PostRepository postRepository;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String healthCheck() {
        return "index";
    }

    @ResponseBody
    @RequestMapping(value = "/api/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ArrayList<Post> getPostList() {
        return new ArrayList<>(postRepository.findAll());
    }

    @ResponseBody
    @RequestMapping(value = "/api/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String addPost(@RequestBody PostRequestBean postRequestBean) {
        Post post = new Post();
        post.setNickname(postRequestBean.getNickname());
        post.setMessage(postRequestBean.getMessage());
        post.setPostDatetime(LocalDateTime.now());
        postRepository.save(post);
        return "Success";
    }
}
