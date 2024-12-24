package com.rayfay.gira.controller;

import com.rayfay.gira.dto.LabelDto;
import com.rayfay.gira.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@Tag(name = "Label", description = "Label management APIs")
public class LabelController {

    private final LabelService labelService;

    @PostMapping
    @Operation(summary = "Create a new label")
    public ResponseEntity<LabelDto> createLabel(@Valid @RequestBody LabelDto labelDto) {
        return ResponseEntity.ok(labelService.createLabel(labelDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a label by ID")
    public ResponseEntity<LabelDto> getLabel(@PathVariable Long id) {
        return ResponseEntity.ok(labelService.getLabelById(id));
    }

    @GetMapping
    @Operation(summary = "Get all labels")
    public ResponseEntity<List<LabelDto>> getAllLabels() {
        return ResponseEntity.ok(labelService.getAllLabels());
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get labels by project ID")
    public ResponseEntity<List<LabelDto>> getLabelsByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(labelService.getLabelsByProjectId(projectId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a label")
    public ResponseEntity<LabelDto> updateLabel(@PathVariable Long id, @Valid @RequestBody LabelDto labelDto) {
        return ResponseEntity.ok(labelService.updateLabel(id, labelDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a label")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return ResponseEntity.ok().build();
    }
}