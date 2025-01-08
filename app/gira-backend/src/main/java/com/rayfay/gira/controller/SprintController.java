package com.rayfay.gira.controller;

import com.rayfay.gira.dto.SprintRequest;
import com.rayfay.gira.service.SprintService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SprintController {
    private final SprintService sprintService;

    @PostMapping("/sprints")
    public ResponseEntity<?> createSprint(@RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.createSprint(request));
    }

    @GetMapping("/projects/{projectId}/sprints")
    public ResponseEntity<?> getSprintList(@PathVariable Long projectId) {
        return ResponseEntity.ok(sprintService.getSprintsByProject(projectId));
    }

    @PutMapping("/sprints/{id}")
    public ResponseEntity<?> updateSprint(@PathVariable Long id, @RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.updateSprint(id, request));
    }

    @PostMapping("/sprints/{id}/start")
    public ResponseEntity<?> startSprint(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.startSprint(id));
    }

    @PostMapping("/sprints/{id}/complete")
    public ResponseEntity<?> completeSprint(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.completeSprint(id));
    }

    @DeleteMapping("/sprints/{id}")
    public ResponseEntity<?> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.ok().build();
    }
}