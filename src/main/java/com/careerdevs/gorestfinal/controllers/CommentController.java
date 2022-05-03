package com.careerdevs.gorestfinal.controllers;


import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.validation.CommentValidation;
import com.careerdevs.gorestfinal.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/*
    Required Routes for GoRestSQL Final: complete for each resource; User, Post, Comment, Todo,

         * GET route that returns one [resource] by ID from the SQL database
         * GET route that returns all [resource]s stored in the SQL database
         * DELETE route that deletes one [resource] by ID from SQL database (returns the deleted SQL [resource] data)
         *DELETE route that deletes all [resource]s from SQL database (returns how many [resource]s were deleted)
         * POST route that queries one [resource] by ID from GoREST and saves their data to your local database (returns
         the SQL [resource] data)
         *POST route that uploads all [resource]s from the GoREST API into the SQL database (returns how many
         [resource]s were uploaded)
         *POST route that create a [resource] on JUST the SQL database (returns the newly created SQL [resource] data)
         *PUT route that updates a [resource] on JUST the SQL database (returns the updated SQL [resource] data)
  * */
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    // * * GET route that returns one [resource] by ID from the SQL database
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable("id") String id){
        try{
            if (ApiErrorHandling.isStrNan(id)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid id");

            } long uID = Integer.parseInt(id);

            Optional<Comment> foundComment = commentRepository.findById(uID);
            if(foundComment.isEmpty()){
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Comment with id is not found: " + id);

            }
            return new ResponseEntity<>(foundComment, HttpStatus.OK);

        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //**GET route that returns all [resource]s stored in the SQL database

    @GetMapping("/all")
    public ResponseEntity<?> getAllComments(){
        try {
            Iterable<Comment> allComments = commentRepository.findAll();
            return new ResponseEntity<>(allComments, HttpStatus.OK);

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //** DELETE route that deletes one [resource] by ID from SQL database (returns the deleted SQL [resource] data)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentById(@PathVariable("id") String deleteComment){
        try{
            if(ApiErrorHandling.isStrNan(deleteComment)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, deleteComment +": is not a valid comment id");

            }
            long uID = Integer.parseInt(deleteComment);
            Optional<Comment> foundComment = commentRepository.findById(uID);

            if (foundComment.isEmpty()){
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user id not found" + deleteComment);
            }
            commentRepository.deleteById(uID);
            return new ResponseEntity<>(foundComment, HttpStatus.OK);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }

    }
    // ** DELETE route that deletes all [resource]s from SQL database (returns how many [resource]s were deleted)
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteALlComments (){
        try{
            long totalCommentPost = commentRepository.count();
            commentRepository.deleteAll();
            return new ResponseEntity<>("Total Comments deleted " + totalCommentPost , HttpStatus.OK);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //** POST route that queries one [resource] by ID from GoREST and saves their data to your local database (returns
    //         the SQL [resource] data)

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> postCommentById(@PathVariable("id") String commentId,
                                             RestTemplate restTemplate){
        try{
            if (ApiErrorHandling.isStrNan(commentId)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, commentId+ ": is not a valid comment id");


            }
            int cId = Integer.parseInt(commentId);
            String url = "https://gorest.co.in/public/v2/comments/" + cId;
            Comment foundComment = restTemplate.getForObject(url, Comment.class);
            System.out.println("found comment");
            System.out.println(foundComment);

            if (foundComment == null){
                throw  new HttpClientErrorException(HttpStatus.NOT_FOUND, " data is null");
            }
            Comment savedComment = commentRepository.save(foundComment);
            return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        }
        catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }
//**POST route that uploads all [resource]s from the GoREST API into the SQL database (returns how many
//         [resource]s were uploaded)
    @PostMapping("/uploadAll")
    public ResponseEntity uploadAll (RestTemplate restTemplate) {
        try {
            String url = "https://gorest.co.in/public/v2/comments/";

            ResponseEntity<Comment[]> response = restTemplate.getForEntity(url, Comment[].class);
            Comment[] firstPageComments = response.getBody();
            //

            assert  firstPageComments != null;
            if(firstPageComments == null){
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

            }
            ArrayList<Comment> allComments = new ArrayList<>(Arrays.asList(firstPageComments));
            HttpHeaders responseHeader = response.getHeaders();
            String totalPages = Objects.requireNonNull(responseHeader.get("X-Pagination-Pages").get(0));

            int totalPageNum = Integer.parseInt(totalPages);

            for( int i = 2; i<= totalPageNum; i++){
                String tempUrl = url + "?=page=" +i;
                Comment[] pageComment = restTemplate.getForObject(tempUrl, Comment[].class);
                if (pageComment == null){
                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get page" + i+ "of comments");

                }
                allComments.addAll(Arrays.asList(firstPageComments));

            }
            commentRepository.saveAll(allComments);
            return new ResponseEntity("Comments added" + allComments.size(), HttpStatus.OK);





        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    //**POST route that create a [resource] on JUST the SQL database (returns the newly created SQL [resource] data)
    @PostMapping("/addNew")
    public ResponseEntity<?> createNewComment(@RequestBody  Comment newComment){
        try {
            ValidationError error = CommentValidation.validateComment(newComment, commentRepository , false);
            if(error.hasError()){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, error.toJsonString());

            }
            Comment createComment = commentRepository.save(newComment);
            return new ResponseEntity<>(createComment, HttpStatus.CREATED);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //PUT route that updates a [resource] on JUST the SQL database (returns the updated SQL [resource] data)
    @PutMapping("/")
    public ResponseEntity<?> updatingComment(@RequestBody Comment updateComment){
        try{
            ValidationError newCommentErrors = CommentValidation.validateComment(updateComment, commentRepository, true);
            if(newCommentErrors.hasError()){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, newCommentErrors.toString());

            }
            Comment savedComment = commentRepository.save(updateComment);
            return new ResponseEntity<>(savedComment, HttpStatus.OK);
        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

}
