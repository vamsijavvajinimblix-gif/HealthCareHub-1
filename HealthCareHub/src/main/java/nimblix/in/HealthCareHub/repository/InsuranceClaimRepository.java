package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
}