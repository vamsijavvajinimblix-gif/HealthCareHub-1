package nimblix.in.HealthCareHub.serviceImpl;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.exception.SlotNotFoundException;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.exception.UserNotFoundException;
import nimblix.in.HealthCareHub.model.Doctor;
import nimblix.in.HealthCareHub.model.DoctorAvailability;
import nimblix.in.HealthCareHub.model.Hospital;
import nimblix.in.HealthCareHub.model.Specialization;
import nimblix.in.HealthCareHub.repository.DoctorAvailabilityRepository;
import nimblix.in.HealthCareHub.repository.DoctorRepository;
import nimblix.in.HealthCareHub.repository.HospitalRepository;
import nimblix.in.HealthCareHub.repository.SpecializationRepository;
import nimblix.in.HealthCareHub.request.DoctorAvailabilityRequest;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.response.DoctorAvailabilityResponse;
import nimblix.in.HealthCareHub.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private String getTodayDateIST() {
        return java.time.LocalDate.now(IST_ZONE).toString(); // yyyy-MM-dd
    }

    private String getCurrentTimeIST() {
        return java.time.LocalTime.now(IST_ZONE)
                .withSecond(0)
                .withNano(0)
                .toString()
                .substring(0, 5); // HH:mm
    }
    private void validateDateFormat(String date) {
        try {
            LocalDate.parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    private void validateTimeFormat(String time) {
        try {
            LocalTime.parse(time);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm");
        }
    }

    @Override
    public DoctorAvailabilityResponse addDoctorTimeSlot(DoctorAvailabilityRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        Long doctorId = request.getDoctorId();
        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("Invalid doctor id");
        }
        if (request.getAvailableDate() == null || request.getAvailableDate().trim().isEmpty())
            throw new IllegalArgumentException("Available date cannot be null or empty");

        if (request.getStartTime() == null || request.getStartTime().trim().isEmpty())
            throw new IllegalArgumentException("Start time cannot be null or empty");

        if (request.getEndTime() == null || request.getEndTime().trim().isEmpty())
            throw new IllegalArgumentException("End time cannot be null or empty");

        if (request.getIsAvailable() == null)
            throw new IllegalArgumentException("Availability status cannot be null");

        validateDateFormat(request.getAvailableDate());
        validateTimeFormat(request.getStartTime());
        validateTimeFormat(request.getEndTime());

       Doctor doctor= doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Doctor not found with id: " + doctorId));

        String today = getTodayDateIST();
        String currentTime = getCurrentTimeIST();

        String slotDate = request.getAvailableDate();
        String start = request.getStartTime();
        String end = request.getEndTime();

        if (slotDate.compareTo(today) < 0) {
            throw new IllegalStateException("Cannot add slot for past date");
        }

        if (slotDate.equals(today) && start.compareTo(currentTime) <= 0) {
            throw new IllegalStateException("Cannot add slot for past time");
        }

        if (start.compareTo(end) >= 0) {
            throw new IllegalStateException("Start time must be before end time");
        }
//duplicate check
        boolean exists = doctorAvailabilityRepository
                .existsByDoctor_IdAndAvailableDateAndStartTime(
                        doctorId,
                        request.getAvailableDate(),
                        request.getStartTime()
                );

        if (exists) {
            throw new IllegalStateException(
                    "Time slot already exists for doctor on " +
                            request.getAvailableDate() + " at " + request.getStartTime()
            );
        }
        boolean overlap = doctorAvailabilityRepository
                .existsOverlappingSlot(
                        doctorId,
                        request.getAvailableDate(),
                        request.getStartTime(),
                        request.getEndTime()
                );

        if (overlap) {
            throw new IllegalStateException("Time slot overlaps with existing slot");
        }
        DoctorAvailability slot = DoctorAvailability.builder()
                .doctor(doctor)
                .availableDate(request.getAvailableDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isAvailable(request.getIsAvailable())
                .build();

       DoctorAvailability saved = doctorAvailabilityRepository.save(slot);
        return doctorAvailabilityRepository.getSlotResponseById(saved.getId());
    }

    @Override
    public DoctorAvailabilityResponse updateDoctorTimeSlot(DoctorAvailabilityRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        Long doctorId = request.getDoctorId();
        Long slotId = request.getSlotId();

        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("Invalid doctor id");
        }
        if (slotId == null || slotId <= 0) {
            throw new IllegalArgumentException("Invalid slot id");
        }
        DoctorAvailability slot = doctorAvailabilityRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException(
                        "Time slot not found with id: " + slotId
                ));
        if(!slot.getDoctor().getId().equals(doctorId)){
            throw new SlotNotFoundException("Time slot not found for this doctor");
        }

        // Determine final values (existing or updated)
        String finalDate = request.getAvailableDate() != null
                ? request.getAvailableDate()
                : slot.getAvailableDate();

        String finalStart = request.getStartTime() != null
                ? request.getStartTime()
                : slot.getStartTime();

        String finalEnd = request.getEndTime() != null
                ? request.getEndTime()
                : slot.getEndTime();
        boolean finalAvailability = request.getIsAvailable() != null
                ? request.getIsAvailable()
                : slot.isAvailable();

        if (finalDate.equals(slot.getAvailableDate()) &&
                finalStart.equals(slot.getStartTime()) &&
                finalEnd.equals(slot.getEndTime()) &&
                ( finalAvailability == slot.isAvailable())) {
            throw new IllegalStateException("No changes detected for update");
        }
        validateDateFormat(finalDate);
        validateTimeFormat(finalStart);
        validateTimeFormat(finalEnd);

        String today = getTodayDateIST();
        String currentTime = getCurrentTimeIST();

        if (finalDate.compareTo(today) < 0) {
            throw new IllegalStateException("Cannot update slot to past date");
        }

        if (finalDate.equals(today) && finalStart.compareTo(currentTime) <= 0) {
            throw new IllegalStateException("Cannot update slot to past time");
        }

        if (finalStart.compareTo(finalEnd) >= 0) {
            throw new IllegalStateException("Start time must be before end time");
        }
//  Duplicate check using final values
        boolean exists = doctorAvailabilityRepository
                .existsByDoctor_IdAndAvailableDateAndStartTimeAndIdNot(
                        doctorId,
                        finalDate,
                        finalStart,
                        slotId
                );

        if (exists) {
            throw new IllegalStateException(
                    "Time slot already exists for doctor on " +
                            finalDate + " at " + finalStart
            );
        }
//  Overlap check using final values
        boolean overlap = doctorAvailabilityRepository
                .existsOverlappingSlotForUpdate(
                        doctorId,
                        finalDate,
                        finalStart,
                        finalEnd,
                        slotId
                );
        if (overlap) {
            throw new IllegalStateException("Time slot overlaps with existing slot");
        }

        slot.setAvailableDate(finalDate);
        slot.setStartTime(finalStart);
        slot.setEndTime(finalEnd);
        slot.setAvailable(finalAvailability);

        DoctorAvailability saved = doctorAvailabilityRepository.save(slot);
        return doctorAvailabilityRepository.getSlotResponseById(saved.getId());
    }

    @Override
    public String registerDoctor(DoctorRegistrationRequest request) {
     try {
         // Check if email already exists
         if (doctorRepository.findByEmailId(request.getDoctorEmail()).isPresent()) {
             return "Doctor already exists with this email";
         }

         // Fetch Hospital
         Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                 .orElseThrow(() -> new RuntimeException("Hospital not found"));

         // Fetch Specialization
         Specialization specialization = specializationRepository.findByName(request.getSpecializationName())
                 .orElseThrow(() -> new RuntimeException("Specialization not found"));

         // Create Doctor
         Doctor doctor = new Doctor();

         doctor.setName(request.getDoctorName());
         doctor.setEmailId(request.getDoctorEmail());
         doctor.setPassword(request.getPassword());
         doctor.setPhone(request.getPhoneNo());
         doctor.setQualification(request.getQualification());
         doctor.setExperienceYears(request.getExperience());
         doctor.setDescription(request.getDescription());
         doctor.setHospital(hospital);

         // ✅ CORRECT WAY (Set Objects, not IDs)
         doctor.setHospital(hospital);
         doctor.setSpecialization(specialization);

         doctorRepository.save(doctor);

         return "Doctor Registered Successfully";
      }catch (UserNotFoundException e){
         return  "User not found";
     }
    }

    @Override
    public ResponseEntity<?> getDoctorDetails(Long doctorId, Long hospitalId) {

        Doctor doctor = doctorRepository
                .findByIdAndHospitalId(doctorId, hospitalId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found in this hospital"));

        return ResponseEntity.status(HttpStatus.OK).body(doctor);
    }

    @Override
    public String updateDoctorDetails(DoctorRegistrationRequest request) {

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctorRepository.findByEmailId(request.getDoctorEmail())
                .filter(existingDoctor -> !existingDoctor.getId().equals(doctor.getId()))
                .ifPresent(existingDoctor -> {
                    throw new RuntimeException("Email already used by another doctor");
                });

        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        Specialization specialization = specializationRepository
                .findByName(request.getSpecializationName())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        doctor.setName(request.getDoctorName());
        doctor.setEmailId(request.getDoctorEmail());
        doctor.setPassword(request.getPassword());
        doctor.setPhone(request.getPhoneNo());
        doctor.setQualification(request.getQualification());
        doctor.setExperienceYears(request.getExperience());
        doctor.setDescription(request.getDescription());

        doctor.setHospital(hospital);
        doctor.setSpecialization(specialization);

        doctorRepository.save(doctor);

        return "Doctor Updated Successfully";
    }


//    @Override
//    public String deleteDoctorDetails(Long doctorId) {
//
//        Doctor doctor = doctorRepository.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
//
//        doctorRepository.delete(doctor);
//
//        return "Doctor deleted successfully (Hard Delete)";
//    }


    @Override
    public String deleteDoctorDetails(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        doctor.setIsActive(HealthCareConstants.IN_ACTIVE);
        doctorRepository.save(doctor);

        return "Doctor deleted successfully (Hard Delete)";
    }



}

