package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.response.DoctorRegistrationResponse;
import org.springframework.http.ResponseEntity;

public interface DoctorService {

    String registerDoctor(DoctorRegistrationRequest request);
    ResponseEntity<?> getDoctorDetails(Long doctorId, Long hospitalId);

    String updateDoctorDetails(DoctorRegistrationRequest request);

    String deleteDoctorDetails(Long doctorId);

}
