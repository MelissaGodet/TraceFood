package hr.algebra.tracefood.backend;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//Single Responsibility Principle (SRP) The methods have unique and specific purpose (management of crud operations Create, Read, Update, Delete)

@Controller
public abstract class AbstractStorableController<T> {

    protected AbstractStorableService<T> service;

    public AbstractStorableController(AbstractStorableService<T> service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("{id}")
    public T getById(@PathVariable final Long id) {
        return service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<T> getAll() {
        return service.getAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public T create(@RequestBody T newStorable) {
        return service.create(newStorable);
    }

}
