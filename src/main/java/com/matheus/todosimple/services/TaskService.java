package com.matheus.todosimple.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matheus.todosimple.models.Task;
import com.matheus.todosimple.models.User;
import com.matheus.todosimple.repositories.TaskRepository;

@Service
public class TaskService {

    // precisa do autowired
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    // procurar a task
    public Task findById(Long id) {
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
                "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()));
    }

    public List<Task> findAllByUserId(Long userId) {
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }

    @Transactional
    public Task create(Task obj) {
        // validou o usuario
        User user = this.userService.findById(obj.getUser().getId());

        // criar a task
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;

    }

    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(long id) {
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível deletar pois há entidades relacionadas");
        }
    }
}
