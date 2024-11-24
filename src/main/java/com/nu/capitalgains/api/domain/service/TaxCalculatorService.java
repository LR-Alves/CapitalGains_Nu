package com.nu.capitalgains.api.domain.service;

import com.nu.capitalgains.api.domain.model.Operation;

public class TaxCalculatorService {

    public double calculateTax(Operation capitalGainsEntity, double averageCost) {
        if (capitalGainsEntity.operation().equalsIgnoreCase("sell")) {
            double profit = (capitalGainsEntity.unitCost() - averageCost) * capitalGainsEntity.quantity();
            return profit > 0 ? profit * 0.20 : 0.0;
        }
        return 0.0;
    }

}
