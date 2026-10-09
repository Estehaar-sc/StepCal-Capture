package com.UniProject.Controller;

import com.UniProject.DTO.TaskDto;
import com.UniProject.DTO.UserDto;
import com.UniProject.Entities.CompletedTask;
import com.UniProject.Services.CompletedTaskService;
import com.UniProject.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompletedTaskService taskService;

    @GetMapping("/message")
    public ResponseEntity<String> showMessage() {
        return ResponseEntity.ok("This is for User");
    }

    @GetMapping("/profile")
    public UserDto showProfile(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return userService.getUser(email);
    }

    @PostMapping("/add-task")
    public ResponseEntity<String> saveTask(
            @RequestBody CompletedTask task,
            HttpServletRequest request) {
        try {
            task.setEmail((String) request.getAttribute("email"));
            task.setDate(LocalDate.now().toString());
            taskService.saveTask(task);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Failed");
        }

        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/task-today")
    public CompletedTask showCompletedTask(HttpServletRequest request) {
        return taskService.getTask(
                (String) request.getAttribute("email"),
                LocalDate.now().toString()
        );
    }

    @GetMapping("/task-history")
    public List<CompletedTask> showTaskHistory(HttpServletRequest request) {
        return taskService.taskHistory(
                (String) request.getAttribute("email")
        );
    }

    @GetMapping("/task")
    public TaskDto sendTask(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return userService.forTask(email);
    }

    @PutMapping("/update-point")
    public ResponseEntity<String> updatePoint(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");

        try {
            userService.givePoint(email);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("failed");
        }

        return ResponseEntity.ok("saved");
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserDto>> getLeaderboard() {
        List<UserDto> leaderboard = userService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}