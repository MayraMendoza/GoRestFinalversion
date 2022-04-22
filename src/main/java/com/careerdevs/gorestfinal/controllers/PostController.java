package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.validation.PostValidation;
import com.careerdevs.gorestfinal.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;


  /*
      Required Routes for GoRestSQL Final: complete for each resource; User, Post, Comment, Todo,

           * * GET route that returns one [resource] by ID from the SQL database
           * GET route that returns all [resource]s stored in the SQL database
           ** DELETE route that deletes one [resource] by ID from SQL database (returns the deleted SQL [resource] data)
           * DELETE route that deletes all [resource]s from SQL database (returns how many [resource]s were deleted)
           * POST route that queries one [resource] by ID from GoREST and saves their data to your local database (returns
           the SQL [resource] data)
           *POST route that uploads all [resource]s from the GoREST API into the SQL database (returns how many
           [resource]s were uploaded)
           *POST route that create a [resource] on JUST the SQL database (returns the newly created SQL [resource] data)
           *PUT route that updates a [resource] on JUST the SQL database (returns the updated SQL [resource] data)
    * */

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @GetMapping("/test")
    public String testRoute(){
        return "Testing!";
    }
    //* GET route that returns one [resource] by ID from the SQL database

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById (@PathVariable("id") String id){
        try {
            // check if id is a number or not
            if(ApiErrorHandling.isStrNan(id)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id +": is not a valid id");
            }
            long uID = Integer.parseInt(id);

            //instead of it returning null
            Optional<Post> foundPost = postRepository.findById(uID);
            if(foundPost.isEmpty()){
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "post with id: "+ uID + " not found");
            }
            return new ResponseEntity<>(foundPost, HttpStatus.OK);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }catch (Exception e){

            // this will catch any errors
            return ApiErrorHandling.genericApiError(e);
        }
    }


    //* GET route that returns all [resource]s stored in the SQL database
    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts(){

        try{

            Iterable<Post> allPosts = postRepository.findAll();
            return new ResponseEntity<>(allPosts, HttpStatus.OK);

        }catch (Exception e){
           return ApiErrorHandling.genericApiError(e);
        }
    }

    //* DELETE route that deletes one [resource] by ID from SQL database (returns the deleted SQL [resource] data)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostById (@PathVariable("id") String postId){
        try{
            if(ApiErrorHandling.isStrNan(postId)){ // checks if string is a number and if its null . if its not a number or null it will return true and throw exception.
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, postId + ": is not valid");
            }
            long uID = Integer.parseInt(postId);
            Optional<Post> deletePost = postRepository.findById(uID);

            if (deletePost.isEmpty()){
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Post with id: "+ postId+ "was not found.");

            }
            postRepository.deleteById(uID);
            return new ResponseEntity<>(deletePost,HttpStatus.OK);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //* DELETE route that deletes all [resource]s from SQL database (returns how many [resource]s were deleted)
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllPosts(@PathVariable)


    @PostMapping("/")
    public ResponseEntity<?> createPost(@RequestBody Post newPost){
        try{
            ValidationError errors = PostValidation.validatePost(newPost, postRepository, false);
            if(errors.hasError()){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJsonString());
            }
            Post createPost = postRepository.save(newPost);
            return new ResponseEntity<>(createPost, HttpStatus.CREATED);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(),e.getStatusCode());
        } catch (Exception e){
           return ApiErrorHandling.genericApiError(e);
        }
    }
}
