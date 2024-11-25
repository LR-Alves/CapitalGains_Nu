package com.nu.capitalgains.api.app.service;

import com.nu.capitalgains.api.app.dto.OperationRequest;
import com.nu.capitalgains.api.app.dto.TaxResponse;
import com.nu.capitalgains.api.app.ports.input.TaxUseCase;
import com.nu.capitalgains.api.domain.model.Operation;
import com.nu.capitalgains.api.domain.service.TaxCalculatorService;

import java.util.ArrayList;
import java.util.List;


public class TaxCalculatorUseCase implements TaxUseCase {

    private final TaxCalculatorService taxCalculatorService;

    public TaxCalculatorUseCase(TaxCalculatorService taxCalculatorService) {
        this.taxCalculatorService = taxCalculatorService;
    }

    @Override
    public List<List<TaxResponse>> calculateTaxes(List<List<OperationRequest>> operations) {
        validateRequests(operations); // Valida as listas de operações

        List<List<TaxResponse>> allResponses = new ArrayList<>();

        for (List<OperationRequest> operationList : operations) {
            // Converte a lista de OperationRequest para uma lista de Operation
            List<Operation> domainOperations = operationList.stream()
                    .map(request -> new Operation(request.operation(), request.unitCost(), request.quantity()))
                    .toList();

            // Calcula os impostos utilizando o TaxCalculatorService
            List<Double> taxes = taxCalculatorService.calculateTaxes(domainOperations);

            // Converte a lista de impostos para TaxResponse
            List<TaxResponse> responses = taxes.stream()
                    .map(TaxResponse::new)
                    .toList();

            allResponses.add(responses);
        }

        return allResponses;
    }

    private void validateRequests(List<List<OperationRequest>> operations) {
        // Validação genérica de entrada
        if (operations == null || operations.isEmpty()) {
            throw new IllegalArgumentException("Lista de operações não pode ser nula ou vazia.");
        }

        for (List<OperationRequest> operationList : operations) {
            if (operationList == null || operationList.isEmpty()) {
                throw new IllegalArgumentException("Cada lista de operações deve conter pelo menos uma operação.");
            }

            for (OperationRequest request : operationList) {
                if (request.operation() == null || request.operation().isBlank()) {
                    throw new IllegalArgumentException("O tipo de operação não pode ser nulo ou vazio.");
                }
                if (request.unitCost() <= 0) {
                    throw new IllegalArgumentException("O custo unitário deve ser maior que zero.");
                }
                if (request.quantity() <= 0) {
                    throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
                }
            }
        }
    }
}



