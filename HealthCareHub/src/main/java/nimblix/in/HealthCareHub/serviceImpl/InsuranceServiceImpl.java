package nimblix.in.HealthCareHub.serviceImpl;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.model.*;
import nimblix.in.HealthCareHub.repository.*;
import nimblix.in.HealthCareHub.request.*;
import nimblix.in.HealthCareHub.response.ClaimStatusResponse;
import nimblix.in.HealthCareHub.service.InsuranceService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceProviderRepository providerRepository;
    private final PatientInsuranceRepository patientInsuranceRepository;
    private final InsuranceClaimRepository claimRepository;

    @Override
    public String createProvider(InsuranceProviderRequest request) {

        InsuranceProvider provider = InsuranceProvider.builder()
                .name(request.getName())
                .policyTypes(request.getPolicyTypes())
                .contactInfo(request.getContactInfo())
                .build();

        providerRepository.save(provider);

        return "Insurance Provider Created";
    }

    @Override
    public String assignInsurance(AssignInsuranceRequest request) {

        PatientInsurance patientInsurance = PatientInsurance.builder()
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .policyNumber(request.getPolicyNumber())
                .validTill(request.getValidTill())
                .build();

        patientInsuranceRepository.save(patientInsurance);

        return "Insurance Assigned to Patient";
    }

    @Override
    public boolean verifyEligibility(Long patientId) {

        return patientInsuranceRepository.findByPatientId(patientId).isPresent();
    }

    @Override
    public String submitClaim(ClaimSubmissionRequest request) {

        InsuranceClaim claim = InsuranceClaim.builder()
                .patientId(request.getPatientId())
                .billAmount(request.getBillAmount())
                .claimStatus("PENDING")
                .build();

        claimRepository.save(claim);

        return "Claim Submitted";
    }

    @Override
    public ClaimStatusResponse trackClaim(Long claimId) {

        InsuranceClaim claim = claimRepository.findById(claimId).orElseThrow();

        return new ClaimStatusResponse(
                claim.getId(),
                claim.getClaimStatus()
        );
    }
}