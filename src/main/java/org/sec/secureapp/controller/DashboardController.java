package org.sec.secureapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.PostDto;
import org.sec.secureapp.entity.Post;
import org.sec.secureapp.service.DashboardService;
import org.sec.secureapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final DashboardService dashboardService;

    @GetMapping(value = "/dashboard")
    public String showDashboard(Model model) {
        List<Post> posts = dashboardService.getAllPosts();
        model.addAttribute("posts", posts);
        return "dashboard";
    }

    @GetMapping(value = "/dashboard/new")
    public String showNewPostCreatePage(Model model) {
        model.addAttribute("post", new Post());
        return "dashboard/create_post";
    }

    @RequestMapping(value= {"/dashboard/save"}, method = RequestMethod.POST)
    public String createPost(Model model, @Valid @ModelAttribute("post") PostDto postDto) {
        dashboardService.savePost(postDto);
        return "redirect:/dashboard";
    }

    @GetMapping(value = "/posts/{id}")
    public String showPostDetail(Model model, @PathVariable String id) {
        Post post = dashboardService.getPostById(Integer.parseInt(id));
        model.addAttribute("post", post);
        return "dashboard/post_detail";
    }

    @GetMapping(value = {"/posts/remove/{id}"})
    public String removePost(@PathVariable String id) {
        dashboardService.removePostById(Integer.parseInt(id));
        return "dashboard";
    }
}
