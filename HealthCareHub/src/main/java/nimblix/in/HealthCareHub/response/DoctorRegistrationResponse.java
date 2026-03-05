package nimblix.in.HealthCareHub.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DoctorRegistrationResponse {

    private Long doctorId;
    private String doctorName;
    private String doctorEmail;
    private Double consultationFee;
    private String Specialization;
    private String hospitalName;
    private Long hospitalId;
    private String qualification;
    private Long experienceYears;
    private String message;


}
