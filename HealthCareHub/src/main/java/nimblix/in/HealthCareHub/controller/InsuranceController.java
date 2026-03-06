package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.request.*;
import nimblix.in.HealthCareHub.response.ClaimStatusResponse;
import nimblix.in.HealthCareHub.service.InsuranceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    @PostMapping("/provider")
    public String createProvider(@RequestBody InsuranceProviderRequest request) {
        return insuranceService.createProvider(request);
    }

    @PostMapping("/assign")
    public String assignInsurance(@RequestBody AssignInsuranceRequest request) {
        return insuranceService.assignInsurance(request);
    }

    @GetMapping("/verify/{patientId}")
    public boolean verifyInsurance(@PathVariable Long patientId) {
        return insuranceService.verifyEligibility(patientId);
    }

    @PostMapping("/claim")
    public String submitClaim(@RequestBody ClaimSubmissionRequest request) {
        return insuranceService.submitClaim(request);
    }

    @GetMapping("/claim/{claimId}")
    public ClaimStatusResponse trackClaim(@PathVariable Long claimId) {
        return insuranceService.trackClaim(claimId);
    }
}