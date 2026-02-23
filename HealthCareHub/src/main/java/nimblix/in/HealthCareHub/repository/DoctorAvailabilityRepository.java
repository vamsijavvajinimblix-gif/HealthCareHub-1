package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorId(Long doctorId);

    // For ADD
    boolean existsByDoctorIdAndAvailableDateAndStartTime(
            Long doctorId,
            String availableDate,
            String startTime
    );
     @Query("""
SELECT COUNT(d) > 0 FROM DoctorAvailability d
                    WHERE d.doctorId = :doctorId
                    AND d.availableDate = :availableDate
                    AND (
                            (:startTime < d.endTime AND :endTime > d.startTime)
                    ) """)
             boolean existsOverlappingSlot(
             @Param("doctorId") Long doctorId,
             @Param("availableDate") String availableDate,
             @Param("startTime") String startTime,
             @Param("endTime") String endTime
     );
    // For UPDATE
    boolean existsByDoctorIdAndAvailableDateAndStartTimeAndIdNot(
            Long doctorId,
            String availableDate,
            String startTime,
            Long id
    );
    @Query("""
SELECT COUNT(d) > 0 FROM DoctorAvailability d
WHERE d.doctorId = :doctorId
AND d.availableDate = :availableDate
AND d.id <> :slotId
AND (
        (:startTime < d.endTime AND :endTime > d.startTime)
)
""")
    boolean existsOverlappingSlotForUpdate(
            @Param("doctorId") Long doctorId,
            @Param("availableDate") String availableDate,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("slotId") Long slotId
    );
}