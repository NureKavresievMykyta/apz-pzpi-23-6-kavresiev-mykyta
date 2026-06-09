package ua.nure.kavresiev.optiview.controller;

import ua.nure.kavresiev.optiview.entity.Measurement;
import ua.nure.kavresiev.optiview.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    @Autowired
    private MeasurementRepository repository;

    @GetMapping
    public List<Measurement> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Measurement create(@RequestBody Measurement measurement) {
        return repository.save(measurement);
    }

    @PutMapping("/{id}")
    public Measurement updateMeasurement(@PathVariable Long id, @RequestBody Measurement details) {
        Measurement m = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement not found with id: " + id));

        m.setSphOd(details.getSphOd());
        m.setCylOd(details.getCylOd());
        m.setAxisOd(details.getAxisOd());
        m.setSphOs(details.getSphOs());
        m.setCylOs(details.getCylOs());
        m.setAxisOs(details.getAxisOs());
        m.setIopOd(details.getIopOd());
        m.setIopOs(details.getIopOs());
        m.setDeviceSerial(details.getDeviceSerial());

        return repository.save(m);
    }

    @DeleteMapping("/{id}")
    public void deleteMeasurement(@PathVariable Long id) {
        repository.deleteById(id);
    }
}