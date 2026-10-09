package com.UniProject.Repository;

import com.UniProject.Entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserRepository extends CrudRepository<User,Long> {
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    @Modifying
    @Query("Update User u set u.isEnabled=true where u.email=:email")
    void updateUserEnable(@Param("email") String email);

    @Query("select u.point from User u where u.email =:email")
    int getPoint(@Param("email") String email);

    @Modifying
    @Query("Update User u set u.point=:point where u.email=:email")
    void updatePoint(@Param("email") String email,@Param("point") int point);

}
