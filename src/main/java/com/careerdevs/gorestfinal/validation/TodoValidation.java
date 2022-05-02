package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.Todo;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.repositories.TodoRepository;

public class TodoValidation {
    public static ValidationError validateTodo(Todo todo, TodoRepository todoRepo, boolean isUpdating){
        // need to validate data for post

        ValidationError errors =new ValidationError();
        return errors;
    }
}
