package com.UniProject.Services;

import com.UniProject.DTO.DtoImpl;
import com.UniProject.DTO.TaskDto;
import com.UniProject.DTO.TaskParam;
import com.UniProject.DTO.UserDto;
import com.UniProject.Entities.CompletedTask;
import com.UniProject.Entities.User;
import com.UniProject.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DtoImpl dto;

    private TaskParam taskParam;

    @Autowired
    private CompletedTaskService taskService;



    public int saveUser(UserDto userDto){
        try{
            User newUser=userRepository.save(dto.convertUserDtoToUser(userDto));
            if(newUser!=null){
                return 1;//Success
            }
            else{
                return 2;//Failed saving
            }
        }catch (Exception e){
            return 0;//Internal server error
        }
    }
    public TaskDto forTask(String email){
        UserDto user=dto.convertUserToUserDto(userRepository.findByEmail(email));
        //setting the parameter to compute
        taskParam=new TaskParam(user.getAge(),user.getHeight(),
                user.getWeight(),user.getGoal(),
                user.getGender(), user.getActivity_level());

        TaskDto task=new TaskDto();
        task.setTarget_Calorie(computeTargetCalorie(taskParam));

        task.setExercises(ExerciseList(taskParam));
        //need to set here task
         Double foodIN=FoodIntake();
        return task;
    }
    @Transactional
    public void verifyUser(String email){
         userRepository.updateUserEnable(email);
    }
    public UserDto getUser(String email){
        return dto.convertUserToUserDto(userRepository.findByEmail(email));
    }

    public User checkUser(String email,String pass){
        return userRepository.findByEmailAndPassword(email,pass);
    }


    /**
     * For mail
     **/


    public boolean checkForDuplicateEmail(String email){
        User check=userRepository.findByEmail(email);
        return check==null;
    }
    public User findEmail(String email){
        return userRepository.findByEmail(email);
    }
    public boolean checkEmailVerification(String email){
        return userRepository.findByEmail(email).isEnabled();
    }
    public int getVerCode(String toEmail){
        return emailService.sendVerEmail(toEmail);
    }


    /**
     *For Calorie computation
     */

    public double getBmr(TaskParam taskParam){
        double a,b,c,d;
        if(taskParam.getGender().equals("male")){
            a=88.362;
            b=13.397;
            c=4.799;
            d=5.677;
        }
        else{
            a=447.593;
            b=9.247;
            c=3.098;
            d=4.330;
        }
        double bmr=a+(b*taskParam.getWeight())+(c*taskParam.getHeight())-(d*taskParam.getAge());
        return preciseCal(bmr*activityMult(taskParam));
    }
    public double computeTargetCalorie(TaskParam taskParam){
        //calculate bmr
        double a,b,c,d;
        if(taskParam.getGender().equals("male")){
            a=88.362;
            b=13.397;
            c=4.799;
            d=5.677;
        }
        else{
            a=447.593;
            b=9.247;
            c=3.098;
            d=4.330;
        }
        double bmr=a+(b*taskParam.getWeight())+(c*taskParam.getHeight())-(d*taskParam.getAge());
        int goal=0;
        if(taskParam.getGoal().equals("gain") || taskParam.getGoal().equals("muscle")){
            goal+=200;
        }
        else if(taskParam.getGoal().equals("loss")) {
            goal-=200;
        }
        return preciseCal((bmr*activityMult(taskParam))+goal);
    }
    public double activityMult(TaskParam taskParam){
        if(taskParam.getActivity_level().equals("lazy")){
            return 1.2;
        }
        else if(taskParam.getActivity_level().equals("low")){
            return 1.375;
        }
        else if(taskParam.getActivity_level().equals("moderate")){
            return 1.55;
        }
        else if(taskParam.getActivity_level().equals("high")){
            return 1.725;
        }
        else if(taskParam.getActivity_level().equals("extreme")){
            return 1.9;
        }
        return 0;
    }

    /**
     *Calorie burn in per repetition for specific exercise
     */
   public Map<String,Double> ExerciseList(TaskParam taskParam)
   {
       Map<String,Double>exerciseCaloriesMap = new HashMap<>();
       Double Push_up_Cal,Pull_up_Cal,Jumping_jacks_Cal,Squats_Cal,Plank_Cal
               ,Sit_up_Cal,Knee_Pushup_Cal;

       Double height=taskParam.getHeight();
       Double weight=taskParam.getWeight();

       Push_up_Cal= (3.8* weight*(3.0/3600))*3.5;

       Pull_up_Cal=(3.0*weight*(3.0/3600))*3.5;

       Jumping_jacks_Cal=(8.0*weight*(1.5/3600))*3.5;

       Squats_Cal=(3.0*weight*(3.0/3600))*3.5;

       Plank_Cal=(2.0*weight*(45.0/3600))*3.5;

       Sit_up_Cal=(3.0*weight*(2.0/3600))*3.5;

       Knee_Pushup_Cal=(3.0*weight*(3.0/3600))*3.5;

       exerciseCaloriesMap.put("Push_up", preciseCal(Push_up_Cal));
       exerciseCaloriesMap.put("Pull_up", preciseCal(Pull_up_Cal));
       exerciseCaloriesMap.put("Jumping_jacks", preciseCal(Jumping_jacks_Cal));
       exerciseCaloriesMap.put("Squats", preciseCal(Squats_Cal));
       exerciseCaloriesMap.put("Plank", preciseCal(Plank_Cal));
       exerciseCaloriesMap.put("Sit_up", preciseCal(Sit_up_Cal));
       exerciseCaloriesMap.put("Knee_Pushup", preciseCal(Knee_Pushup_Cal));
       //exerciseCaloriesMap.put("Food Intake", FoodIntake());



       return exerciseCaloriesMap;
   }

    /**
     * Precising all value into 2 decimal point
     */
    public Double preciseCal(Double x)
   {
       String formattedX = String.format("%.2f", x);
       Double result = Double.valueOf(formattedX);
       return result;
   }

    /**
     * Food Intake Calculation
     */

    public Double FoodIntake()
    {

        // In this initializer take User input as gram measurement Except Egg
        // Egg Should be taken as number per egg is on avearge 50gm
        Double Tot_In_Cal=100.0;
        Double Rice_in=100.0;
        Double Roti_in=100.0;
        Double Chicken_in=100.0;
        int Egg_in=2;
        Double Milk_in=100.0;
        Double Salad_in=100.0;


        Double Rice_cal=((Rice_in*1.0)/100.0)*130.0;

        Double Roti_cal=((Roti_in*1.0)/100.0)*300.0;

        Double Chicken_cal=((Chicken_in*1.0)/100.0)*239.0;

        double Egg_Cal=(((Egg_in*50.0))/100)*155.0;

        Double Milk_cal=((Milk_in*1.0)/100)*44.0;

        Double Salad_cal=((Salad_in*1.0)/100.0)*17.0;

        Tot_In_Cal=Rice_cal+Roti_cal+Chicken_cal+Egg_Cal+Milk_cal+Salad_cal;

        return  preciseCal(Tot_In_Cal);


    }

    int getPoint(String email){
        return userRepository.getPoint(email);
    }


    @Transactional
    public void givePoint(String email){
        int point=getPoint(email);
        int reward=0;

        CompletedTask complete=taskService.getTask(email, LocalDate.now().toString());
        UserDto user=dto.convertUserToUserDto(userRepository.findByEmail(email));
        //setting the parameter to compute
        taskParam=new TaskParam(user.getAge(),user.getHeight(),
                user.getWeight(),user.getGoal(),
                user.getGender(), user.getActivity_level());

        double bmr=getBmr(taskParam);
        if(computeTargetCalorie(taskParam)<=((bmr+complete.getCalorie_burn())-complete.getCalorie_intake())
        && !complete.isUpdated()){
      
            taskService.updateUpdate(true,email,LocalDate.now().toString());
            reward=50;
        }
        point+=reward;
        userRepository.updatePoint(email,point);
    }

    /**
     * Creating the LeaderBord
     */
    public List<UserDto> getLeaderboard() {
        List<UserDto>leaderboard=new ArrayList<>();
        List<User>users= (List<User>) userRepository.findAll();
        for(User user:users){
            leaderboard.add(dto.convertUserToUserDto(user));
        }
        leaderboard.sort(new Comparator<UserDto>() {
            @Override
            public int compare(UserDto user1, UserDto user2) {
                return Integer.compare(user2.getPoint(), user1.getPoint());
            }
        });

        return leaderboard;
    }




}