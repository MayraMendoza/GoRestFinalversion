package com.careerdevs.gorestfinal.controllers;

/*
    Required Routes for GoRestSQL Final: complete for each resource; User, Post, Comment, Todo,

         ** GET route that returns one [resource] by ID from the SQL database
         **GET route that returns all [resource]s stored in the SQL database
         **DELETE route that deletes one [resource] by ID from SQL database (returns the deleted SQL [resource] data)
         ** DELETE route that deletes all [resource]s from SQL database (returns how many [resource]s were deleted)
         * * POST route that queries one [resource] by ID from GoREST and saves their data to your local database (returns
         the SQL [resource] data)
         **POST route that uploads all [resource]s from the GoREST API into the SQL database (returns how many
         [resource]s were uploaded)
         **POST route that create a [resource] on JUST the SQL database (returns the newly created SQL [resource] data)
         *PUT route that updates a [resource] on JUST the SQL database (returns the updated SQL [resource] data)
  * */

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.UserRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.validation.UserValidation;
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

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/test")
    public String testRoute(){
        return "Hello User!";
    }

    //* GET route that returns one [resource] by ID from the SQL database
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String userId){
        try {
            if (ApiErrorHandling.isStrNan(userId)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, userId + ": is not a valid id");
            }
            long uID = Long.parseLong(userId);
            Optional<User> foundUser = userRepository.findById(uID);
            if (foundUser.isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User with id: " + uID + " not found");

            }
            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }

    }
    //**GET route that returns all [resource]s stored in the SQL database
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        try {
            Iterable<User> allUsers = userRepository.findAll();
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //**DELETE route that deletes one [resource] by ID from SQL database (returns the deleted SQL [resource] data)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById (@PathVariable("id") String userId){
        try {
            if (ApiErrorHandling.isStrNan(userId)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, userId + ": is not valid");

            }
            long uID = Long.parseLong(userId);
            Optional<User> deleteUser = userRepository.findById(uID);

            if (deleteUser.isEmpty()){
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User with id: "+ userId+ " was not found.");

            }
            userRepository.deleteById(uID);
            return new ResponseEntity<>(deleteUser, HttpStatus.OK);

        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //** DELETE route that deletes all [resource]s from SQL database (returns how many [resource]s were deleted)
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllUsers(){
        try {
            long totalUsers = userRepository.count();
            userRepository.deleteAll();
            return new ResponseEntity<>("Users deleted: "+ totalUsers, HttpStatus.OK);

        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);

        }
    }

    //* POST route that queries one [resource] by ID from GoREST and saves their data to your local database (returns
    //         the SQL [resource] data)

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadById(@PathVariable("id") String userId ,
                                        RestTemplate restTemplate){
        try{
            if(ApiErrorHandling.isStrNan(userId)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, userId + ": is not a valid id");

            }
            long uId = Long.parseLong(userId);

            String url = "https://gorest.co.in/public/v2/users/" +uId;
            User foundUser = restTemplate.getForObject(url, User.class);

            if(foundUser == null){
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User data is null");

            }
            User saveUser = userRepository.save(foundUser);

            return new ResponseEntity<>(saveUser, HttpStatus.CREATED);

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }

    }

    //*POST route that uploads all [resource]s from the GoREST API into the SQL database (returns how many
    //         [resource]s were uploaded)
    @PostMapping("/upload/all")
    public ResponseEntity uploadAll (RestTemplate restTemplate){
        try {
            String url = "https://gorest.co.in/public/v2/users";
            ResponseEntity<User[]> response = restTemplate.getForEntity(url, User[].class);
            User[] firstPageUsers = response.getBody();
            assert firstPageUsers != null;
            if(firstPageUsers ==null){
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get first page of go rest post");

            }
            ArrayList<User> allUser = new ArrayList<>(Arrays.asList(firstPageUsers));
            HttpHeaders responseHeader = response.getHeaders();
            String totalPages = Objects.requireNonNull(responseHeader.get("X-Pagination-Pages").get(0));

            int totalPgNum =Integer.parseInt(totalPages);

            for( int i = 2; i<= totalPgNum; i++) {
                String tempUrl = url + "?=page=" + i;
                User[] pagePost = restTemplate.getForObject(tempUrl, User[].class);
                if (pagePost == null) {
                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get page " + i + "of posts");
                }
                allUser.addAll(Arrays.asList(firstPageUsers));
            }

            userRepository.saveAll(allUser);

            return new ResponseEntity("posts added " + allUser.size(),HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        }
        //*POST route that create a [resource] on JUST the SQL database (returns the newly created SQL [resource] data)
    @PostMapping("/create/new")
    public ResponseEntity<?> createNewUser(@RequestBody() User newUser){
        try {
            ValidationError error= UserValidation.validateUser(newUser, userRepository, false);
            if (error.hasError()){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, error.toJsonString());

            }
            User createUser = userRepository.save(newUser);
            return new ResponseEntity<>(createUser, HttpStatus.CREATED);

        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);

        }

    }
    //*PUT route that updates a [resource] on JUST the SQL database (returns the updated SQL [resource] data)
    @PutMapping("/")
    public ResponseEntity<?> updateUser(@RequestBody User updateUser){
        try {
            ValidationError newUserErrors = UserValidation.validateUser(updateUser, userRepository, true);
            if(newUserErrors.hasError()){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, newUserErrors.toString());


            }
            User saveUser = userRepository.save(updateUser);
            return new ResponseEntity<>(saveUser, HttpStatus.OK);

        }catch (HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }
    }




