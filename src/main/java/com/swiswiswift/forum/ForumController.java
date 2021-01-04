package com.swiswiswift.forum;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class ForumController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String healthCheck() {
        return "success";
    }
}
