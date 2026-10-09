package com.UniProject.DTO;

import com.UniProject.Entities.User;
import org.springframework.stereotype.Component;

@Component
public class DtoImpl {
    public User convertUserDtoToUser(UserDto userDto){
        User user=new User();
        user.setFirst_name(userDto.getFirst_name());
        user.setLast_name(userDto.getLast_name());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setPhone_no(userDto.getPhone_no());
        user.setAge(userDto.getAge());
        user.setHeight(userDto.getHeight());
        user.setGoal(userDto.getGoal());
        user.setWeight(userDto.getWeight());
        user.setGender(userDto.getGender());
        user.setActivity_level(userDto.getActivity_level());
        user.setRole("USER");

        return user;
    }

    public UserDto convertUserToUserDto(User user){
        UserDto userDto=new UserDto();
        userDto.setFirst_name(user.getFirst_name());
        userDto.setLast_name(user.getLast_name());
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        userDto.setPhone_no(user.getPhone_no());
        userDto.setAge(user.getAge());
        userDto.setGender(user.getGender());
        userDto.setHeight(user.getHeight());
        userDto.setGoal(user.getGoal());
        userDto.setWeight(user.getWeight());
        userDto.setActivity_level(user.getActivity_level());
        userDto.setPoint(user.getPoint());
        return userDto;
    }
}
