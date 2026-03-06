package nimblix.in.HealthCareHub.request;

import lombok.Data;

@Data
public class ClaimSubmissionRequest {

    private Long patientId;

    private Double billAmount;
}