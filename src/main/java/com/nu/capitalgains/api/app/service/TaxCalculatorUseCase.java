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
    public List<TaxResponse> calculateTaxes(List<OperationRequest> requests) {
        try {
            validateRequests(requests); // Validação das entradas
            return processOperations(requests); // Processamento principal
        } catch (IllegalArgumentException e) {
            throw e; // Repassa exceções conhecidas
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular impostos: " + e.getMessage(), e); // Exceções inesperadas
        }
    }

    private void validateRequests(List<OperationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("A lista de operações não pode ser nula ou vazia.");
        }
        for (OperationRequest request : requests) {
            if (request.operation() == null || request.unitCost() <= 0 || request.quantity() <= 0) {
                throw new IllegalArgumentException("Operação inválida: " + request);
            }
        }
    }

    private List<TaxResponse> processOperations(List<OperationRequest> requests) {
        double totalQuantity = 0;
        double totalCost = 0;
        List<TaxResponse> responses = new ArrayList<>();

        for (OperationRequest request : requests) {
            if ("buy".equalsIgnoreCase(request.operation())) {
                totalCost += request.unitCost() * request.quantity();
                totalQuantity += request.quantity();
                responses.add(new TaxResponse(0.0)); // Compras não geram imposto
            } else if ("sell".equalsIgnoreCase(request.operation())) {
                responses.add(processSellOperation(request, totalQuantity, totalCost));
                totalQuantity -= request.quantity();
            } else {
                throw new IllegalArgumentException("Tipo de operação inválida: " + request.operation());
            }
        }
        return responses;
    }

    private TaxResponse processSellOperation(OperationRequest request, double totalQuantity, double totalCost) {
        if (totalQuantity <= 0) {
            throw new IllegalArgumentException("Venda inválida: quantidade insuficiente no inventário.");
        }
        double averageCost = totalCost / totalQuantity;
        double tax = taxCalculatorService.calculateTax(
                new Operation(request.operation(), request.unitCost(), request.quantity()), averageCost);
        return new TaxResponse(tax);
    }
}
