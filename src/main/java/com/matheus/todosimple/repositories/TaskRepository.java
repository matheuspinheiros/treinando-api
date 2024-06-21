package com.matheus.todosimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matheus.todosimple.models.Task;
import com.matheus.todosimple.models.projection.TaskProjection;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<TaskProjection> findByUser_Id(Long id);

    // @Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
    // List<Task> findByUser_Id(@Param("id") Long id);

}
