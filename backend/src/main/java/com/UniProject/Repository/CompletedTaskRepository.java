package com.UniProject.Repository;

import com.UniProject.Entities.CompletedTask;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
public interface CompletedTaskRepository extends CrudRepository<CompletedTask,Long> {
    CompletedTask findByEmailAndDate(String email, String date);
    @Modifying
    @Query("UPDATE CompletedTask task SET task.calorie_burn = :burn " +
            "WHERE task.email = :email AND task.date = :date")
    void updateCalorie_burn(@Param("burn") double burn,
                                        @Param("email") String email,
                                        @Param("date") String date);
    @Modifying
    @Query("UPDATE CompletedTask task SET task.calorie_intake = :intake " +
            "WHERE task.email = :email AND task.date = :date")
    void updateCalorie_intake(@Param("intake") double intake,
                                        @Param("email") String email,
                                        @Param("date") String date);
    @Query("SELECT task FROM CompletedTask task WHERE task.email = :email")
    List<CompletedTask> findAllByEmail(@Param("email") String email);

    @Modifying
    @Query("Update CompletedTask task set task.updated=:update where task.email=:email AND task.date=:date")
    void updateIsUpdate(@Param("update")boolean update,@Param("email") String email,@Param("date") String date);
}
