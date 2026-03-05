package nimblix.in.HealthCareHub.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRegistrationRequest {

    private String doctorName;
    private String doctorEmail;
    private String password;
    private String qualification;
    private Long experience;
    private String description;
    private String phoneNo;
    private Long doctorId;
    private Long hospitalId;
    private String specializationName;
    private Double consultationFee;

}