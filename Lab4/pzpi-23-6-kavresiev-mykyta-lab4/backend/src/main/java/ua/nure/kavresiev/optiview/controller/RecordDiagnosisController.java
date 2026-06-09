package ua.nure.kavresiev.optiview.controller;

import ua.nure.kavresiev.optiview.entity.RecordDiagnosis;
import ua.nure.kavresiev.optiview.repository.RecordDiagnosisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/record-diagnoses")
public class RecordDiagnosisController {

    @Autowired
    private RecordDiagnosisRepository repository;

    @GetMapping
    public List<RecordDiagnosis> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public RecordDiagnosis create(@RequestBody RecordDiagnosis recordDiagnosis) {
        return repository.save(recordDiagnosis);
    }

    @PutMapping("/{id}")
    public RecordDiagnosis updateRecordDiagnosis(@PathVariable Long id, @RequestBody RecordDiagnosis details) {
        RecordDiagnosis rd = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RecordDiagnosis not found with id: " + id));

        rd.setDiagnosisType(details.getDiagnosisType());
        rd.setDiagnosisId(details.getDiagnosisId());
        rd.setMedicalRecordId(details.getMedicalRecordId());

        return repository.save(rd);
    }

    @DeleteMapping("/{id}")
    public void deleteRecordDiagnosis(@PathVariable Long id) {
        repository.deleteById(id);
    }
}