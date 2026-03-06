package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.PatientInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientInsuranceRepository extends JpaRepository<PatientInsurance, Long> {

    Optional<PatientInsurance> findByPatientId(Long patientId);
}