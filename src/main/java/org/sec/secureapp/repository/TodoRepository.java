package org.sec.secureapp.repository;

import org.sec.secureapp.entity.Post;
import org.sec.secureapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Integer> {

}
