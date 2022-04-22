package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.PostRepository;

public class PostValidation {
    public static ValidationError validatePost(Post post, PostRepository postRepo, boolean isUpdating){
        // need to validate data for post

        ValidationError errors =new ValidationError();
        return errors;
    }
}
