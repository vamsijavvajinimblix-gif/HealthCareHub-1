package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import org.springframework.http.ResponseEntity;

public interface DoctorService {
    String registerDoctor(DoctorRegistrationRequest request);

    ResponseEntity<?> getDoctorDetails(Long doctorId, Long hospitalId);
}
