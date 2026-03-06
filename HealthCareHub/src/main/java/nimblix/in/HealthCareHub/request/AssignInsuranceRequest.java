package nimblix.in.HealthCareHub.request;

import lombok.Data;

@Data
public class AssignInsuranceRequest {

    private Long patientId;

    private Long providerId;

    private String policyNumber;

    private String validTill;
}