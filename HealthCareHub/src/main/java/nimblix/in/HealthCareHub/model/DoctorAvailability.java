package nimblix.in.HealthCareHub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import nimblix.in.HealthCareHub.utility.HealthCareUtil;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "doctor_id",nullable = false)
    private Long doctorId;
    @Column(nullable = false)
    private String availableDate;
    @Column(nullable = false)
    private String startTime;
    @Column(nullable = false)
    private String endTime;
    @Column(nullable = false)
    private boolean isAvailable;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "updated_time")
    private String updatedTime;


    @PrePersist
    protected void onCreate(){
        createdTime= HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();
        updatedTime= HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();

    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedTime= HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();


    }


}
