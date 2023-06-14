package hr.algebra.tracefood.backend.classicdb.repository;

import hr.algebra.tracefood.backend.classicdb.model.CertificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/*

Single Responsibility Principle (SRP): The CertificationTypeRepository interface has a single responsibility,
which is to define persistence operations specific to the CertificationType entity. This ensures a clear separation
of responsibilities between the persistence and domain layers.
*/
/*
Segregation Principle (ISP) interface: The CertificationTypeRepository interface exposes only the persistence operations necessary for the CertificationType entity.
 This allows customers to use only the methods they need, avoiding the burden of depending on unused features.
*/
/*
Dependency Inversion Principle (DIP): The CertificationTypeRepository interface represents an abstraction of the data repository specific to the CertificationType entity.
Classes that need to access CertificationType data depend on this interface rather than a concrete implementation.
This makes it easy to replace or modify the underlying implementation without impacting dependent classes.
 */
@Repository
public interface CertificationTypeRepository extends JpaRepository<CertificationType, Long> {}
