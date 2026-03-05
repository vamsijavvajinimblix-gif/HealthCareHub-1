package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.model.Specialization;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.response.DoctorRegistrationResponse;
import nimblix.in.HealthCareHub.service.DoctorService;
import nimblix.in.HealthCareHub.serviceImpl.DoctorServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {


    private final DoctorService doctorService;


    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorRegistrationRequest request) {

        if (request == null ||
                request.getDoctorName() == null ||
                request.getDoctorEmail() == null ||
                request.getHospitalId() == null ||
                request.getConsultationFee() == null) {

            return ResponseEntity.badRequest().body("Required fields are missing");
        }

        return ResponseEntity.ok(doctorService.registerDoctor(request)
        );
    }
    }


