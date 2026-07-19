package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private Map<Integer, Developer> developers;
    private final Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer createDeveloper(@RequestBody Developer request) {
        double netSalary;
        Developer developer;

        switch (request.getExperience()) {
            case JUNIOR:
                netSalary = calculateNetSalary(
                        request.getSalary(),
                        taxable.getSimpleTaxRate()
                );

                developer = new JuniorDeveloper(
                        request.getId(),
                        request.getName(),
                        netSalary
                );
                break;

            case MID:
                netSalary = calculateNetSalary(
                        request.getSalary(),
                        taxable.getMiddleTaxRate()
                );

                developer = new MidDeveloper(
                        request.getId(),
                        request.getName(),
                        netSalary
                );
                break;

            case SENIOR:
                netSalary = calculateNetSalary(
                        request.getSalary(),
                        taxable.getUpperTaxRate()
                );

                developer = new SeniorDeveloper(
                        request.getId(),
                        request.getName(),
                        netSalary
                );
                break;

            default:
                throw new IllegalArgumentException(
                        "Gecersiz experience degeri"
                );
        }

        developers.put(developer.getId(), developer);
        return developer;
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(
            @PathVariable int id,
            @RequestBody Developer developer
    ) {
        if (!developers.containsKey(id)) {
            return null;
        }

        developer.setId(id);
        developers.put(id, developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable int id) {
        return developers.remove(id);
    }

    private double calculateNetSalary(double salary, double taxRate) {
        return salary - (salary * taxRate / 100);
    }
}