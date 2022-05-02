package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;

import java.util.Optional;

public class UserValidation {
    public static ValidationError validateUser(User user, UserRepository userRepo, boolean isUpdating){
        ValidationError errors =new ValidationError();
        if(isUpdating){
            if (user.getId() == 0){
                errors.addError("id", "id cannot be left blank");
            }else{
                Optional<User> foundUser = userRepo.findById(user.getId());

                if(foundUser.isEmpty()){
                    errors.addError("id", "No user with the id" + user.getId());
                }
            }
        }
        String userName = user.getName();
        String userEmail = user.getEmail();
        String userGender = user.getGender();
        String userStatus = user.getGender();


        if(userName == null || userName.trim().equals("")){
            errors.addError("name", "Name cannot be left bllank");
        }
        if(userEmail == null|| userEmail.trim().equals("")){
            errors.addError("email", "email can not be left blank");
        }
        if(userGender == null || userGender.trim().equals("")){
            errors.addError("gender", "Gender cannot be left blank");

        }else if(!(userGender.equals("male")|| userGender.equals("female")|| userGender.equals(("other")))){
            errors.addError("gender", "Gender must be female, male or other");

        }

        if (userStatus == null || userStatus.trim().equals("")) {
            errors.addError("status", "Status can nor be left blank ");
        }
//        }else if(!(userStatus.equals("active") || userStatus.equals("inactive"))){
//            // this is not working ???/
//            errors.addError("status", "status must be active or inactive");
//        }
        return errors;
    }
}
