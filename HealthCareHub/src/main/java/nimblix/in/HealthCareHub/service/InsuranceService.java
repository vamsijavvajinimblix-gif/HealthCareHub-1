package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.request.*;
import nimblix.in.HealthCareHub.response.ClaimStatusResponse;

public interface InsuranceService {

    String createProvider(InsuranceProviderRequest request);

    String assignInsurance(AssignInsuranceRequest request);

    boolean verifyEligibility(Long patientId);

    String submitClaim(ClaimSubmissionRequest request);

    ClaimStatusResponse trackClaim(Long claimId);
}