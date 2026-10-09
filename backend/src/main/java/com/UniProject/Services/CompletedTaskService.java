package com.UniProject.Services;

import com.UniProject.Entities.CompletedTask;
import com.UniProject.Repository.CompletedTaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompletedTaskService {

    @Autowired
    private CompletedTaskRepository completedTaskRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveTask(CompletedTask task) {
        CompletedTask completedTask =
                completedTaskRepository.findByEmailAndDate(
                        task.getEmail(), task.getDate()
                );

        if (completedTask == null) {
            completedTaskRepository.save(task);
        } else {
            double burn =
                    completedTask.getCalorie_burn() + task.getCalorie_burn();
            double intake =
                    completedTask.getCalorie_intake() + task.getCalorie_intake();

            completedTaskRepository.updateCalorie_burn(
                    burn, task.getEmail(), task.getDate()
            );
            completedTaskRepository.updateCalorie_intake(
                    intake, task.getEmail(), task.getDate()
            );
        }

        entityManager.flush();
    }

    public CompletedTask getTask(String email, String date) {
        return completedTaskRepository.findByEmailAndDate(email, date);
    }

    public List<CompletedTask> taskHistory(String email) {
        return completedTaskRepository.findAllByEmail(email);
    }

    @Transactional
    public void updateUpdate(boolean update, String email, String date) {
        completedTaskRepository.updateIsUpdate(update, email, date);
    }
}