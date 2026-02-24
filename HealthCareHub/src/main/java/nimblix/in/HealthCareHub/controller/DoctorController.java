package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.model.DoctorAvailability;
import nimblix.in.HealthCareHub.request.DoctorAvailabilityRequest;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.response.DoctorAvailabilityResponse;
import nimblix.in.HealthCareHub.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/register")
    public String registerDoctor(@RequestBody DoctorRegistrationRequest request) {
        return doctorService.registerDoctor(request);

    }

    @GetMapping("/getDoctorDetails")
    public ResponseEntity<?> getDoctorDetails(@RequestParam Long  doctorId,@RequestParam Long  hospitalId){
        return  doctorService.getDoctorDetails(doctorId,hospitalId);
    }

    @PostMapping("/{doctorId}/timeslots")
    public ResponseEntity<DoctorAvailabilityResponse> addTimeSlot(
            @PathVariable Long doctorId,
            @RequestBody DoctorAvailabilityRequest request) {

        DoctorAvailabilityResponse response =
                doctorService.addDoctorTimeSlot(doctorId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{doctorId}/timeslots/{slotId}")
    public ResponseEntity<DoctorAvailabilityResponse> updateTimeSlot(
            @PathVariable Long doctorId,
            @PathVariable Long slotId,
            @RequestBody DoctorAvailabilityRequest request) {

        DoctorAvailabilityResponse response =
                doctorService.updateDoctorTimeSlot(doctorId, slotId, request);

        return ResponseEntity.ok(response);
    }
}