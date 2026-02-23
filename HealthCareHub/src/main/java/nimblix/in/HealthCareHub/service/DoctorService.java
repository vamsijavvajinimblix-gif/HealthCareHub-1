package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.model.DoctorAvailability;
import nimblix.in.HealthCareHub.request.DoctorAvailabilityRequest;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;

public interface DoctorService {

    String RegisterDoctor(DoctorRegistrationRequest doctorRegistrationRequest);

    DoctorAvailability addDoctorTimeSlot(DoctorAvailabilityRequest request);

    DoctorAvailability updateDoctorTimeSlot(Long slotId,
                                            DoctorAvailabilityRequest request);
}