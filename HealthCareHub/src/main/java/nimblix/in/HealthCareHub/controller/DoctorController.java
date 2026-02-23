package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.model.DoctorAvailability;
import nimblix.in.HealthCareHub.request.DoctorAvailabilityRequest;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /*
    Json object:
    {
      "name": "tejaswini",
      "mobileNumber": "8937483454",
      "date": "10-05-2026"
    }
    */

    @PostMapping("/register")
    public String registerDoctor(
            @RequestBody DoctorRegistrationRequest doctorRegistrationRequest) {

        return doctorService.RegisterDoctor(doctorRegistrationRequest);
    }

    @PostMapping("/timeslot/add")
    public ResponseEntity<DoctorAvailability> addTimeSlot(
            @RequestBody DoctorAvailabilityRequest request) {

        DoctorAvailability saved =
                doctorService.addDoctorTimeSlot(request);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/timeslot/update/{slotId}")
    public ResponseEntity<DoctorAvailability> updateTimeSlot(
            @PathVariable Long slotId,
            @RequestBody DoctorAvailabilityRequest request) {

        DoctorAvailability updated =
                doctorService.updateDoctorTimeSlot(slotId, request);

        return ResponseEntity.ok(updated);
    }
}