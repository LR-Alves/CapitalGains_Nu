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
        try {
            validateRequests(operations);  // Validação das entradas
            return processOperations(operations);  // Processamento das operações
        } catch (IllegalArgumentException e) {
            throw e;  // Repassa exceções conhecidas
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular impostos: " + e.getMessage(), e);  // Exceções inesperadas
        }
    }

    // Ajustando para receber List<List<OperationRequest>> (múltiplas listas de operações)
    private void validateRequests(List<List<OperationRequest>> operations) {


        if (operations == null || operations.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma lista de operações fornecida.");
        }
        for (List<OperationRequest> operationList : operations) {
            if (operationList == null || operationList.isEmpty()) {
                throw new IllegalArgumentException("Uma das listas de operações está vazia.");
            }
            for (OperationRequest request : operationList) {
                if (request.operation() == null || request.unitCost() <= 0 || request.quantity() <= 0) {
                    throw new IllegalArgumentException("Operação inválida: " + request);
                }
            }
        }
    }

    // Alterando para processar múltiplas listas de operações
    private List<List<TaxResponse>> processOperations(List<List<OperationRequest>> operations) {
        List<List<TaxResponse>> allResponses = new ArrayList<>();

        for (List<OperationRequest> requests : operations) {
            List<TaxResponse> responses = new ArrayList<>();
            double totalQuantity = 0;
            double totalCost = 0;

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
            allResponses.add(responses);  // Adiciona os resultados para a lista de operações
        }
        return allResponses;
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
