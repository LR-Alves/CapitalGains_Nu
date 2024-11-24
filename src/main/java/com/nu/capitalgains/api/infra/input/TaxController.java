package com.nu.capitalgains.api.infra.input;

import com.nu.capitalgains.api.app.dto.OperationRequest;
import com.nu.capitalgains.api.app.dto.TaxResponse;
import com.nu.capitalgains.api.app.ports.input.TaxUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/taxes")
public class TaxController {

    @Autowired
    private TaxUseCase taxUseCase;

    @PostMapping
    public ResponseEntity<List<TaxResponse>> calculateTaxes(@RequestBody List<OperationRequest> operations) {
        return ResponseEntity.ok(taxUseCase.calculateTaxes(operations));
    }
}