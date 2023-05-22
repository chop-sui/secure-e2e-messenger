package org.sec.secureapp.service;

import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.PostDto;
import org.sec.secureapp.dto.UserDto;
import org.sec.secureapp.entity.Category;
import org.sec.secureapp.entity.Post;
import org.sec.secureapp.entity.Role;
import org.sec.secureapp.entity.User;
import org.sec.secureapp.repository.PostRepository;
import org.sec.secureapp.repository.RoleRepository;
import org.sec.secureapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void savePost(PostDto postDto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setTitle(postDto.title());
        post.setContent(postDto.content());

        post.setCategory(Category.QNA);
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Integer id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cannot find post with id " + id));
    }
}
