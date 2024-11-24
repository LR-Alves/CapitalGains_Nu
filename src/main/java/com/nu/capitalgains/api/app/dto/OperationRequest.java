package com.nu.capitalgains.api.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OperationRequest(String operation,
                               @JsonProperty("unit-cost")
                               double unitCost,
                               int quantity) {

}

