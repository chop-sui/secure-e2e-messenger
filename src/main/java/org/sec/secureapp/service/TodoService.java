package org.sec.secureapp.service;

import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.PostDto;
import org.sec.secureapp.entity.Category;
import org.sec.secureapp.entity.Post;
import org.sec.secureapp.entity.Todo;
import org.sec.secureapp.entity.User;
import org.sec.secureapp.repository.PostRepository;
import org.sec.secureapp.repository.TodoRepository;
import org.sec.secureapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    public void addTodo(User user, String todo) {
        Todo t = new Todo();
        t.setContent(todo);
        t.setUser(user);
        user.getTodos().add(t);
        userRepository.save(user);
    }

    public void removeTodo(Integer id) {
        todoRepository.deleteById(id);
    }

}
