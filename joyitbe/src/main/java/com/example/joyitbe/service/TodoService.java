package com.example.joyitbe.service;

import com.example.joyitbe.entity.TodoEntity;
import com.example.joyitbe.repository.TodoRepsoitory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {
    @Autowired
    private TodoRepsoitory repository;

    public String testService(){
        //TodoEntity 생성
        TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
        //TodoEntity 저장
        repository.save(entity);
        //TodoEntity 검색
        TodoEntity savedEntity = repository.findById(entity.getId()).get();
        return savedEntity.getTitle();

    }

    public List<TodoEntity> create(final TodoEntity entity){
        //validations
        validate(entity);

        repository.save(entity);

        log.info("Entity Id : {} is saved.", entity.getId());
        log.info("Entity title : {} is saved.", entity.getTitle());

        return repository.findByUserId(entity.getUserId());
    }

    public List<TodoEntity> update(final TodoEntity entity){
        //1)저장할 엔티티가 유효한지 확인
        validate(entity);

        //2)넘겨받은 엔티티 id를 이용해 TodoEntity를 가져옴, 존재하지 않는 엔티티 업데이트 x
        final Optional<TodoEntity> original = repository.findById(entity.getId());

        original.ifPresent(todo->{
            //3)반환된 TodoEntity가 존재하면 값을 새 entity의 값으로 덮어 씌운다.
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());

            //4)db에 새 값 저장
            repository.save(todo);
        });

        //유저의 모든 Todo 리스트 리턴
        return retrieve(entity.getUserId());
    }

    public List<TodoEntity> delete(final TodoEntity entity){
        //1)저장할 엔티티 유효성 확인
        validate(entity);

        try{
            //2)엔티티 삭제
            repository.delete(entity);
        }catch(Exception e){
            //3)exception 발생시 id와 exception 로깅
            log.error("error deleting entity", entity.getId(),e);

            //4)컨트롤러로 exception을 날린다. 데이터베이스 내부 로직을 캡슐화하기 위해 e를 리턴하지 않고 새 exception 오브젝트로 리턴
            throw new RuntimeException("error deleting entity "+entity.getId());
        }
            //5)새 Todo 리스트를 가져와 리턴한다.
        return retrieve(entity.getUserId());
    }

    //리팩토링
    private void validate(final TodoEntity entity){
        if(entity == null){
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if(entity == null){
            log.warn("Unknown.user");
            throw new RuntimeException("Unknown user");
        }
    }
    public List<TodoEntity> retrieve(final String userId){
        return repository.findByUserId(userId);
    }
}
