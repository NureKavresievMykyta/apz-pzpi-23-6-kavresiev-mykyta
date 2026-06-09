package ua.nure.kavresiev.optiview.service;

import ua.nure.kavresiev.optiview.dto.VisitDTO;
import ua.nure.kavresiev.optiview.entity.Visit;
import ua.nure.kavresiev.optiview.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MedicalRecordService {

    private final VisitRepository visitRepository;

    public MedicalRecordService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public Visit createVisit(VisitDTO visitDTO) {
        if (visitDTO.getDiagnosis() == null || visitDTO.getDiagnosis().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty");
        }

        Visit visit = new Visit();
        mapDtoToEntity(visitDTO, visit);

        visit.setStartTime(LocalDateTime.now());
        visit.setEndTime(LocalDateTime.now().plusMinutes(20));
        visit.setVisitStatus("COMPLETED");
        visit.setVisitType("CONSULTATION");

        return visitRepository.save(visit);
    }

    public Visit updateVisit(Long id, VisitDTO visitDTO) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit not found with id: " + id));

        mapDtoToEntity(visitDTO, visit);
        return visitRepository.save(visit);
    }

    public void deleteVisit(Long id) {
        if (!visitRepository.existsById(id)) {
            throw new RuntimeException("Visit not found with id: " + id);
        }
        visitRepository.deleteById(id);
    }

    private void mapDtoToEntity(VisitDTO dto, Visit entity) {
        entity.setPatientId(dto.getPatientId());
        entity.setDoctorId(dto.getDoctorId());
        entity.setDiagnosis(dto.getDiagnosis());
        entity.setTreatment(dto.getTreatment());
        entity.setVisualAcuity(dto.getVisualAcuity());
    }
}