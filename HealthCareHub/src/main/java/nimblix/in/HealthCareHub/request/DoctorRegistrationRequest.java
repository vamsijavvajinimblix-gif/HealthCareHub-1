package nimblix.in.HealthCareHub.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRegistrationRequest {

    private String doctorName;
    private String doctorEmail;
    private String password;
    private String phoneNo;
    private String hospitalName;
    private String specializationName;
}