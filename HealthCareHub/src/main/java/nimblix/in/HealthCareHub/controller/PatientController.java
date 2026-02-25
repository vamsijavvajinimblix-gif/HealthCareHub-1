package nimblix.in.HealthCareHub.controller;


import nimblix.in.HealthCareHub.model.*;
import nimblix.in.HealthCareHub.service.InsuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final InsuranceService insuranceService;

    public PatientController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    // 1️⃣ Create Insurance Provider
    @PostMapping("/insurance/provider")
    public ResponseEntity<InsuranceProvider> addProvider(
            @RequestBody InsuranceProvider provider) {
        return ResponseEntity.ok(
                insuranceService.addProvider(provider)
        );
    }

    // 2️⃣ Assign Insurance to Patient
    @PostMapping("/{patientId}/insurance")
    public ResponseEntity<PatientInsurance> assignInsurance(
            @PathVariable Long patientId,
            @RequestBody PatientInsurance patientInsurance) {

        patientInsurance.setPatientId(patientId);

        return ResponseEntity.ok(
                insuranceService.assignInsurance(patientInsurance)
        );
    }

    // 3️⃣ Verify Eligibility
    @GetMapping("/{patientId}/verify")
    public ResponseEntity<String> verifyEligibility(
            @PathVariable Long patientId) {

        boolean eligible =
                insuranceService.verifyEligibility(patientId);

        return ResponseEntity.ok(
                eligible ? "Eligible" : "Not Eligible"
        );
    }

    // 4️⃣ Submit Claim
    @PostMapping("/{patientId}/claim")
    public ResponseEntity<InsuranceClaim> submitClaim(
            @PathVariable Long patientId,
            @RequestBody InsuranceClaim claim) {

        claim.setPatientId(patientId);

        return ResponseEntity.ok(
                insuranceService.submitClaim(claim)
        );
    }

    // 5️⃣ Track Claim Status
    @GetMapping("/{patientId}/claims")
    public ResponseEntity<List<InsuranceClaim>> getClaims(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                insuranceService.getClaimStatus(patientId)
        );
    }
}

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/patient")
@RequiredArgsConstructor
public class PatientController {
}

