package com.pweb.backend.controller;


import com.pweb.backend.model.Message;
import com.pweb.backend.model.TestEntity;
import com.pweb.backend.model.TestEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")

public class APIController {
    @Autowired
    private TestEntityRepository testEntityRepository;

    @GetMapping(value = "/public")
    public Message publicEndpoint() {
        return new Message("All good. You DO NOT need to be authenticated to call /api/public.");
    }

    @GetMapping(value = "/private")
    public Message privateEndpoint() {
        return new Message("All good. You can see this because you are Authenticated.");
    }

    @GetMapping(value = "/private-scoped")
    public Message privateScopedEndpoint() {
        return new Message("All good. You can see this because you are Authenticated with a Token granted the 'read:messages' scope");
    }



    @GetMapping(value = "/test-db/set")
    public Message testDbSetter(@RequestParam(name = "value") String value) {
        TestEntity testEntity = new TestEntity(value);
        testEntityRepository.save(testEntity);
        return new Message("OK");
    }
    @GetMapping(value = "/test-db/get")
    public Message testDbGetter() {
        return new Message(testEntityRepository.findAll().toString());
    }



}
