package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping// HTTP GET method
    public Iterable<UserDto> getAllUsers(
//            @RequestHeader(required = false, name = "x-auth-token") String accessToken,
            @RequestParam(required = false, defaultValue = "", name = "sort") String sort
    ){
//        System.out.println(accessToken);

       if(!Set.of("email", "name").contains(sort.toLowerCase())) {
           sort = "name";
       }


        return userRepository.findAll(Sort.by(sort)).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    //get data from the request body so we can create the new record

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterUserRequest request, UriComponentsBuilder uriBuilder) {
        var user = userMapper.toEntity(request);
        System.out.println(user);
        userRepository.save(user);
        var userDto = userMapper.toDto(user);
        var uri =uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    //update user Email & Name
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name="id") Long id, @RequestBody UpdateUserRequest request){
        var user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        userMapper.update(request, user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    //delete the user with the user id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }


    //Update the password with the user id
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable(name="id") Long id, @RequestBody ChangePasswordRequest request){
        var user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        if(!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }


    //Execption handeler

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var error = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach((errorField) -> {
            error.put(errorField.getField(), errorField.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(error);
    }

}
