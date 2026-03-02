package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.request.HospitalRegistrationRequest;

import nimblix.in.HealthCareHub.request.MedicineAddRequest;
import nimblix.in.HealthCareHub.response.RoomResponse;

import java.util.List;

public interface HospitalService {

    String registerHospital(HospitalRegistrationRequest request);

    String addMedicine(MedicineAddRequest request);

    void addRooms(Long hospitalId, List<HospitalRegistrationRequest.Room> rooms);

    List<RoomResponse> getAvailableRooms(Long hospitalId);


}
