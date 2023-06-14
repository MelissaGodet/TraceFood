package hr.algebra.tracefood.backend.classicdb.service;

import hr.algebra.tracefood.backend.AbstractStorableService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/*
Liskov Substitution Principle (LSP): Subclasses can be used instead of their base classes without altering the expected behavior.
 For example, the CertificationTypeService class inherits from AbstractClassicDBStorableService, and it can be used wherever AbstractClassicDBStorableService is expected,
 without introducing unwanted behavior.
 */


@Service
public abstract class AbstractClassicDBStorableService<T> extends AbstractStorableService<T> {

    public AbstractClassicDBStorableService(JpaRepository<T, Long> repository) {
        super(repository);
    }

    public T update(T updatedClassicDBStorable) {
        return repository.save(updatedClassicDBStorable);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
