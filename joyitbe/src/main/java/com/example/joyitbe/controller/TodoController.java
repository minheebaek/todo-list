package com.example.joyitbe.controller;

import com.example.joyitbe.dto.ResponseDTO;
import com.example.joyitbe.dto.TodoDTO;
import com.example.joyitbe.entity.TodoEntity;
import com.example.joyitbe.service.TodoService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo(){
        String str = service.testService();
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok().body(response);
    }
    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
        try {

            //1)TodoEntity로 변환
            TodoEntity entity = TodoDTO.todoEntity(dto);

            //2)id를 null로 초기화 생성 당시에는 id가 없어야함
            entity.setId(null);

            //3)Authentication Bearer Token을 통해 받은 userId를 넘긴다
            entity.setUserId(userId);

            //4)서비스를 이용해 Todo 엔티티 생성
            List<TodoEntity> entities = service.create(entity);

            //5)자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환함
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            //6)변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화함
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //7)ResponseDTO를 리턴함
            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            //예외 발생 시 dto 대신 error 에 메세지를 넣어 리턴

            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);

        }
    }
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId){ //검색

        //1)서비스 메서드의 retrieve 메서드를 사용해 todo리스트를 가져옴
        List<TodoEntity> entities = service.retrieve(userId);

        //2)자바 스트림을 통해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환함
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        //6)변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화함
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        //7) ResponseDTO를 리턴함
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){

        //1)dto를 entity로 변환
        TodoEntity entity = TodoDTO.todoEntity(dto);

        //2)id를 temporaryUserId로 초기화
        entity.setUserId(userId);

        //3)서비스를 이용해 entity를 업데이트
        List<TodoEntity> entities = service.update(entity);

        //4)자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환함
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        //5)변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화함
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        //6)ResponseDTO를 리턴함
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
        try {

            //1)TodoEntity로 변환
            TodoEntity entity = TodoDTO.todoEntity(dto);

            //2)임시 유저 아이디
            entity.setUserId(userId);

            //3)entity 삭제
            List<TodoEntity> entities = service.delete(entity);

            //4)자바스트림 이용해 리턴된 엔티티 리스트->TodoDTO 리스트로
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            //5)변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //6)ResponseDTO를 리턴
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            //예외 발생시 dto 대신 error 메시지 넣어 리턴
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
