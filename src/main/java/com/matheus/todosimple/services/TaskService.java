package com.matheus.todosimple.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matheus.todosimple.models.Task;
import com.matheus.todosimple.models.User;
import com.matheus.todosimple.models.enums.ProfileEnum;
import com.matheus.todosimple.models.projection.TaskProjection;
import com.matheus.todosimple.repositories.TaskRepository;
import com.matheus.todosimple.security.UserSpringSecurity;
import com.matheus.todosimple.services.exceptions.AuthorizationException;
import com.matheus.todosimple.services.exceptions.DataBindingViolationException;
import com.matheus.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {

    // precisa do autowired
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    // procurar a task
    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso Negado");

        return task;
    }

    public List<TaskProjection> findAllByUser() {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado!");

        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task obj) {

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado!");

        User user = this.userService.findById(userSpringSecurity.getId());
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
            throw new DataBindingViolationException("Não é possível deletar pois há entidades relacionadas");
        }
    }

    // verificar se a task é de um usuário
    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
