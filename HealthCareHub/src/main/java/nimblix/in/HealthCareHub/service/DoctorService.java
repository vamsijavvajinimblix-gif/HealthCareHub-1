package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.model.DoctorAvailability;
import nimblix.in.HealthCareHub.request.DoctorAvailabilityRequest;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.response.DoctorAvailabilityResponse;
import org.springframework.http.ResponseEntity;

public interface DoctorService {


    DoctorAvailabilityResponse addDoctorTimeSlot(DoctorAvailabilityRequest request);

    DoctorAvailabilityResponse updateDoctorTimeSlot(DoctorAvailabilityRequest request);

    String registerDoctor(DoctorRegistrationRequest request);

    ResponseEntity<?> getDoctorDetails(Long doctorId, Long hospitalId);



    String updateDoctorDetails(DoctorRegistrationRequest request);

    String deleteDoctorDetails(Long doctorId);
}

