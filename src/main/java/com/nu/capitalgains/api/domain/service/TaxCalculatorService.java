package com.nu.capitalgains.api.domain.service;

import com.nu.capitalgains.api.domain.model.Operation;

import java.util.ArrayList;
import java.util.List;

public class TaxCalculatorService {

    private double weightedAverageCost = 0.0; // Média ponderada inicial
    private int totalShares = 0;             // Total de ações disponíveis
    private double accumulatedLoss = 0.0;    // Prejuízo acumulado para deduzir de futuros lucros

    public List<Double> calculateTaxes(List<Operation> operations) {
        List<Double> taxes = new ArrayList<>();

        for (Operation operation : operations) {
            String type = operation.operation();
            double unitCost = operation.unitCost();
            int quantity = operation.quantity();

            if (type.equalsIgnoreCase("buy")) {
                // Atualizar o preço médio ponderado ao comprar
                weightedAverageCost = ((totalShares * weightedAverageCost) + (quantity * unitCost)) / (totalShares + quantity);
                totalShares += quantity;
                taxes.add(0.0); // Compra não paga imposto
            } else if (type.equalsIgnoreCase("sell")) {
                double totalValue = unitCost * quantity; // Valor total da venda

                // Regra de isenção para valores abaixo de R$ 20.000,00
                if (totalValue <= 20000.00) {
                    totalShares -= quantity; // Reduz ações vendidas
                    taxes.add(0.0);
                    continue;
                }

                // Calcula o lucro ou prejuízo
                double profit = (unitCost - weightedAverageCost) * quantity;

                if (profit > 0) {
                    // Deduz prejuízo acumulado, se houver
                    if (accumulatedLoss > 0) {
                        profit = Math.max(0, profit - accumulatedLoss);
                        accumulatedLoss -= Math.min(profit, accumulatedLoss);
                    }

                    totalShares -= quantity; // Reduz ações vendidas
                    taxes.add(profit * 0.20); // 20% sobre o lucro
                } else {
                    // Registra prejuízo acumulado
                    accumulatedLoss += Math.abs(profit);
                    totalShares -= quantity; // Reduz ações vendidas
                    taxes.add(0.0); // Prejuízo não paga imposto
                }
            } else {
                throw new IllegalArgumentException("Operação desconhecida: " + type);
            }
        }

        return taxes;
    }
}

