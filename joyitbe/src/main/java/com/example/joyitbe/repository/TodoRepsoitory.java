package com.example.joyitbe.repository;

import com.example.joyitbe.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepsoitory extends JpaRepository<TodoEntity, String> {
    List<TodoEntity> findByUserId(String userId);
}
