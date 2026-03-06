package nimblix.in.HealthCareHub.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimStatusResponse {

    private Long claimId;

    private String claimStatus;
}