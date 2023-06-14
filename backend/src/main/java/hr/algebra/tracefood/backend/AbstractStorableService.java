package hr.algebra.tracefood.backend;

import hr.algebra.tracefood.backend.blockchaindb.model.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
Open/Closed Principle (OCP) : provides a base implementation for CRUD operations that can be extended by specific service classes.
This allows new functionality to be added by creating new subclasses without modifying existing code.
*/
/*
Interface Segregation Principle (ISP): Interfaces are specific to customer needs.
In this case, the AbstractStorableService abstract class defines an interface to the CRUD operations needed for storage services.
*/
/*
Dependency Inversion Principle (DIP): Dependencies are inverted to facilitate extensibility and flexibility.
For example, the AbstractClassicDBStorableService class depends on an abstraction (JpaRepository) instead of a concrete implementation.
This makes it easy to change the underlying implementation without modifying the code of dependent classes.
*/
@Service
public abstract class AbstractStorableService<T> {

    protected JpaRepository<T, Long> repository;

    public AbstractStorableService(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public T create(T newStorable) {
        return repository.save(newStorable);
    }

    public Optional<T> getById(Long id) {
        return repository.findById(id);
    }

    public List<T> getAll() {
        return repository.findAll();
    }

    public abstract T update(T updatedStorable);

    public abstract void deleteById(Long id);

}
