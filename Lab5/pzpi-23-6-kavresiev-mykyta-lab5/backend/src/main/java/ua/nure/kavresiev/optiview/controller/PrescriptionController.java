package ua.nure.kavresiev.optiview.controller;

import ua.nure.kavresiev.optiview.entity.Prescription;
import ua.nure.kavresiev.optiview.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository repository;

    @GetMapping
    public List<Prescription> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Prescription create(@RequestBody Prescription prescription) {
        return repository.save(prescription);
    }

    @PutMapping("/{id}")
    public Prescription updatePrescription(@PathVariable Long id, @RequestBody Prescription details) {
        Prescription p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));

        p.setPrescriptionType(details.getPrescriptionType());
        p.setSphOd(details.getSphOd());
        p.setCylOd(details.getCylOd());
        p.setAxisOd(details.getAxisOd());
        p.setSphOs(details.getSphOs());
        p.setCylOs(details.getCylOs());
        p.setAxisOs(details.getAxisOs());
        p.setPd(details.getPd());

        return repository.save(p);
    }

    @DeleteMapping("/{id}")
    public void deletePrescription(@PathVariable Long id) {
        repository.deleteById(id);
    }
}