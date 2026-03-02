package nimblix.in.HealthCareHub.serviceImpl;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.model.Hospital;
import nimblix.in.HealthCareHub.model.Medicine;
import nimblix.in.HealthCareHub.repository.HospitalRepository;
import nimblix.in.HealthCareHub.repository.MedicineRepository;
import nimblix.in.HealthCareHub.request.HospitalRegistrationRequest;
import nimblix.in.HealthCareHub.request.MedicineAddRequest;
import nimblix.in.HealthCareHub.response.RoomResponse;
import nimblix.in.HealthCareHub.service.HospitalService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final MedicineRepository medicineRepository;
    @Override
    public String registerHospital(HospitalRegistrationRequest request) {


        if (hospitalRepository.findByName(request.getName()).isPresent()) {
            return "Hospital already exists";
        }

        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .phone(request.getPhone())
                .email(request.getEmail())
                .totalBeds(request.getTotalBeds())
                .build();

        hospitalRepository.save(hospital);

        return "Hospital Registered Successfully";
    }
    @Override
    public String addMedicine(MedicineAddRequest request){

        //-edge cases---
        //--Check Hospital Exists--
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("Hospital Not Found"));

        if (request.getPrice()==null || request.getPrice()<=0){
            throw new IllegalArgumentException("price must be greater than 0");
        }

        if (request.getStockQuantity()==null || request.getStockQuantity()<0){
            throw new IllegalArgumentException("StockQuantity cannot be negative ");
        }

        Optional<Medicine> existing = medicineRepository.findByMedicineNameAndHospital(
                request.getMedicineName(), hospital);
        if (existing.isPresent()){
            throw new IllegalArgumentException("Medicine already exists in this hospital");
        }

        //--Create Medicine--
        Medicine medicine = Medicine.builder()
                .medicineName(request.getMedicineName())
                .manufacturer(request.getManufacturer())
                .description(request.getDescription())
                .dosage(request.getDosage())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .isActive("ACTIVE")
                .hospital(hospital)
                .build();

        //--Save medicine
        medicineRepository.save(medicine);
        return "Medicine Added Successfully";
    }



    @Override
    public void addRooms(Long hospitalId,
                         List<HospitalRegistrationRequest.Room> rooms) {

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found"));

        if (hospital.getRooms().size() + rooms.size() > hospital.getTotalBeds()) {
            throw new IllegalArgumentException("Exceeds total bed capacity");
        }

        for (HospitalRegistrationRequest.Room roomReq : rooms) {
            Hospital.Room room = new Hospital.Room();
            room.setRoomNumber(roomReq.getRoomNumber());
            room.setRoomType(roomReq.getRoomType());
            room.setAvailable(roomReq.isAvailable());

            hospital.getRooms().add(room);
        }

        hospitalRepository.save(hospital);
    }

    @Override
    public List<RoomResponse> getAvailableRooms(Long hospitalId) {

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found"));

        List<RoomResponse> response = new ArrayList<>();

        for (Hospital.Room room : hospital.getRooms()) {
            if (room.isAvailable()) {
                RoomResponse roomResponse = RoomResponse.builder()
                        .roomNumber(room.getRoomNumber())
                        .roomType(room.getRoomType())
                        .available(room.isAvailable())
                        .build();

                response.add(roomResponse);
            }
        }

        return response;
    }
}