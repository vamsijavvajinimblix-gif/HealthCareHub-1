package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.response.DoctorRegistrationResponse;
import nimblix.in.HealthCareHub.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
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


