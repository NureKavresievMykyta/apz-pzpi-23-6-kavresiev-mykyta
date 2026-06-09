package ua.nure.kavresiev.optiview.controller;

import ua.nure.kavresiev.optiview.entity.MedicalRecord;
import ua.nure.kavresiev.optiview.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordRepository repository;

    @GetMapping
    public List<MedicalRecord> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public MedicalRecord create(@RequestBody MedicalRecord record) {
        return repository.save(record);
    }

    @PutMapping("/{id}")
    public MedicalRecord updateRecord(@PathVariable Long id, @RequestBody MedicalRecord details) {
        MedicalRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        record.setComplaints(details.getComplaints());
        record.setAnamnesis(details.getAnamnesis());
        record.setVisitId(details.getVisitId());

        return repository.save(record);
    }

    @DeleteMapping("/{id}")
    public void deleteRecord(@PathVariable Long id) {
        repository.deleteById(id);
    }
}