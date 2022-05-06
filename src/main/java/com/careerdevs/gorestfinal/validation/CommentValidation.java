package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.repositories.PostRepository;

import java.util.Optional;

public class CommentValidation {
    public static ValidationError validateComment(Comment comment, CommentRepository commentRepo,PostRepository postRepo,  boolean isUpdating){
        ValidationError errors =new ValidationError();
        if(isUpdating){
            if(comment.getId() == 0 ) {
                errors.addError("id", "id cannot be left blank");
            }else {
                Optional<Comment> foundUser = commentRepo.findById(comment.getId());
                if(foundUser.isEmpty()){
                    errors.addError("id", "No comment found with the id: " + comment.getId());

                }
            }
        }

        long commentPostId = comment.getPost_id();
        String commentName = comment.getName();
        String commentEmail = comment.getEmail();
        String commentBody = comment.getBody();

        if (commentName == null || commentName.trim().equals("")){
            errors.addError("Name", "Name can not be left blank");
        }
        if (commentEmail == null || commentEmail.trim().equals("")){
            errors.addError("Email", "Email cannot be left blank");

        }if(commentBody == null || commentBody.trim().equals("")){
            errors.addError("Body", "body cannot be left blank");
        }
        if(commentPostId == 0 ){
            errors.addError("post_Id", "Post_Id cannot be left blank");

        }else{
            Optional<Post> foundPost = postRepo.findById(commentPostId);
            if(foundPost.isEmpty()){
                errors.addError("post_Id", "Post_Id is invalid because there is not post found with the post id: "+ commentPostId);
            }
        }


        return errors;
    }
}
