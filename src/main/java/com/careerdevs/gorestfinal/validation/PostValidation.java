package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;

import java.util.Optional;

public class PostValidation {
    public static ValidationError validatePost(Post post, PostRepository postRepo, UserRepository userRepository, boolean isUpdating){
        // need to validate data for post

        ValidationError errors =new ValidationError();
        if(isUpdating){
            if(post.getId() == 0){
                errors.addError("id", "id cannot be left blank");
            }else {
                Optional<Post> foundUser = postRepo.findById(post.getId());
                if (foundUser.isEmpty()){
                    errors.addError("id", "No user found with the ID: "  + post.getId());

                }
            }
        }

        String postTitle =post.getTitle();
        String postBody = post.getBody();
        long postUserId = post.getUser_id();

        if(postTitle == null || postTitle.trim().equals("")){
            errors.addError("title", "title can not be left blank");
        }
        if(postBody == null || postBody.trim().equals("")){
            errors.addError("body", "body can not be left blank");
        }
        if( postUserId == 0 ) {
            errors.addError("user_Id ", "user_Id can not be left blank");
        }else{

            // is this postUserId connected to an existing user
            //import user from models
            Optional<User> foundUser = userRepository.findById(postUserId);
            if (foundUser.isEmpty()){
                errors.addError("user_id", "user_Id is invalid because there is no user found with the id:" + postUserId);
            }


        }
        return errors;
    }
}
