package com.vcs.mygit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ResourceController {
    @GetMapping("/")
    public String getIndex() {
        return "index.html";
    }
}
