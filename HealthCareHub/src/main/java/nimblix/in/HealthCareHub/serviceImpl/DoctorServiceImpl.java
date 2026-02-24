package nimblix.in.HealthCareHub.serviceImpl;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.exception.SlotNotFoundException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;


    @Override
    public DoctorAvailabilityResponse addDoctorTimeSlot(Long doctorId, DoctorAvailabilityRequest request) {


        if (request.getAvailableDate() == null || request.getAvailableDate().trim().isEmpty())
            throw new RuntimeException("Available date cannot be null or empty");

        if (request.getStartTime() == null || request.getStartTime().trim().isEmpty())
            throw new RuntimeException("Start time cannot be null or empty");

        if (request.getEndTime() == null || request.getEndTime().trim().isEmpty())
            throw new RuntimeException("End time cannot be null or empty");

        if (request.getIsAvailable() == null)
            throw new RuntimeException("Availability status cannot be null");

       Doctor doctor= doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Doctor not found with id: " + doctorId));

        LocalDate slotDate = LocalDate.parse(request.getAvailableDate());
        if(slotDate.isBefore(LocalDate.now())){
            throw new RuntimeException("Cannot add slot for past date");
        }
        LocalTime start = LocalTime.parse(request.getStartTime());
        LocalTime end = LocalTime.parse(request.getEndTime());

        if(!start.isBefore(end)){
            throw new RuntimeException("Start time must be before end time");
        }

        boolean exists = doctorAvailabilityRepository
                .existsByDoctor_IdAndAvailableDateAndStartTime(
                        doctorId,
                        request.getAvailableDate(),
                        request.getStartTime()
                );

        if (exists) {
            throw new RuntimeException(
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
            throw new RuntimeException("Time slot overlaps with existing slot");
        }
        DoctorAvailability slot = DoctorAvailability.builder()
                .doctor(doctor)
                .availableDate(request.getAvailableDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isAvailable(request.getIsAvailable())
                .build();

       DoctorAvailability saved = doctorAvailabilityRepository.save(slot);
       return mapToResponse(saved);
    }

    @Override
    public DoctorAvailabilityResponse updateDoctorTimeSlot(Long doctorId, Long slotId,
                                                   DoctorAvailabilityRequest request) {

        DoctorAvailability slot = doctorAvailabilityRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException(
                        "Time slot not found with id: " + slotId
                ));
        if(!slot.getDoctor().getId().equals(doctorId)){
            throw new RuntimeException("slot does not belong to this doctor");
        }

            if (request.getAvailableDate() != null) {
                LocalDate slotDate = LocalDate.parse(request.getAvailableDate());
                if (slotDate.isBefore(LocalDate.now())) {
                    throw new RuntimeException("Cannot update slot to past date");
                }
            }
        boolean noChange =
                (request.getAvailableDate() == null || request.getAvailableDate().equals(slot.getAvailableDate())) &&
                        (request.getStartTime() == null || request.getStartTime().equals(slot.getStartTime())) &&
                        (request.getEndTime() == null || request.getEndTime().equals(slot.getEndTime())) &&
                        (request.getIsAvailable() == null || request.getIsAvailable().equals(slot.isAvailable()));

        if (noChange) {
            throw new RuntimeException("No changes detected for update");
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

        LocalTime start = LocalTime.parse(finalStart);
        LocalTime end = LocalTime.parse(finalEnd);

        if (!start.isBefore(end)) {
            throw new RuntimeException("Start time must be before end time");
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
            throw new RuntimeException(
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
            throw new RuntimeException("Time slot overlaps with existing slot");
        }

        if (request.getAvailableDate() != null)
            slot.setAvailableDate(request.getAvailableDate());

        if (request.getStartTime() != null)
            slot.setStartTime(request.getStartTime());

        if (request.getEndTime() != null)
            slot.setEndTime(request.getEndTime());

        if (request.getIsAvailable() != null) {
            slot.setAvailable(request.getIsAvailable());
        }
        DoctorAvailability updated = doctorAvailabilityRepository.save(slot);

        return mapToResponse(updated);
    }

    private DoctorAvailabilityResponse mapToResponse(DoctorAvailability slot) {
        return DoctorAvailabilityResponse.builder()
                .id(slot.getId())
                .doctorId(slot.getDoctor().getId())
                .doctorName(slot.getDoctor().getName())
                .availableDate(slot.getAvailableDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .available(slot.isAvailable())
                .createdTime(slot.getCreatedTime())
                .updatedTime(slot.getUpdatedTime())
                .build();

    }


    @Override
    public String registerDoctor(DoctorRegistrationRequest request) {

        // Check if email already exists
        if (doctorRepository.findByEmailId(request.getDoctorEmail()).isPresent()) {
            return "Doctor already exists with this email";
        }

        // Fetch Hospital
        Hospital hospital = hospitalRepository.findByName(request.getHospitalName())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        // Fetch Specialization
        Specialization specialization = specializationRepository.findByName(request.getSpecializationName())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        Doctor doctor = new Doctor();

        doctor.setName(request.getDoctorName());
        doctor.setEmailId(request.getDoctorEmail());
        doctor.setPassword(request.getPassword());
        doctor.setPhone(request.getPhoneNo());

        // ✅ CORRECT WAY (Set Objects, not IDs)
        doctor.setHospital(hospital);
        doctor.setSpecialization(specialization);

        doctorRepository.save(doctor);

        return "Doctor Registered Successfully";
    }


    @Override
    public ResponseEntity<?> getDoctorDetails(Long doctorId, Long hospitalId) {
        return null;
    }

}

