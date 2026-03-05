package nimblix.in.HealthCareHub.serviceImpl;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.exception.DoctorNotFoundException;
import nimblix.in.HealthCareHub.exception.UserNotFoundException;
import nimblix.in.HealthCareHub.model.Doctor;
import nimblix.in.HealthCareHub.repository.DoctorRepository;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.response.DoctorProfileResponse;
import nimblix.in.HealthCareHub.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nimblix.in.HealthCareHub.model.Hospital;
import nimblix.in.HealthCareHub.model.Specialization;
import nimblix.in.HealthCareHub.repository.HospitalRepository;
import nimblix.in.HealthCareHub.repository.SpecializationRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    public String registerDoctor(DoctorRegistrationRequest request) {
        try {
            // Check if email already exists
            if (doctorRepository.findByEmailId(request.getDoctorEmail()).isPresent()) {
                return HealthCareConstants.DOCTOR_ALREADY_EXISTS;
            }

            // Fetch Hospital
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new RuntimeException(HealthCareConstants.HOSPITAL_NOT_FOUND));

            // Fetch Specialization
            Specialization specialization = specializationRepository.findByName(request.getSpecializationName())
                    .orElseThrow(() -> new RuntimeException(HealthCareConstants.SPECIALIZATION_NOT_FOUND));

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

            return HealthCareConstants.DOCTOR_REGISTERED_SUCCESS;
        }catch (UserNotFoundException e){
            return  HealthCareConstants.USER_NOT_FOUND;
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
    @Override
    public DoctorProfileResponse getDoctorProfile(Long doctorId) {

        return doctorRepository.findDoctorProfileById(doctorId)
                .orElseThrow(() ->
                        new DoctorNotFoundException("Doctor not found with id: " + doctorId)
                );
    }


    public Specialization createSpecialization(Specialization specialization) {

        specializationRepository.findByName(specialization.getName())
                .ifPresent(existing -> {
                    throw new RuntimeException("Specialization already exists");
                });

        return specializationRepository.save(specialization);
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    public Specialization updateSpecialization(Long id, Specialization specialization) {

        Specialization existing = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        existing.setName(specialization.getName());

        return specializationRepository.save(existing);
    }

}
