package nimblix.in.HealthCareHub.serviceImpl;

import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl {
    @Autowired
    private PatientRepository repository;

//    public List<Patient> getAllPatients() {
//
//        return repository.findByIsDeletedFalse();
//    }

    public String softDeletePatient(Long id) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

//        patient.setDeleted();   //  Mark as deleted
        repository.save(patient);

        return "Patient soft deleted successfully";
    }

    public Patient savePatient(Patient patient) {
        // TODO Auto-generated method stub
        return repository.save(patient);
    }

}
