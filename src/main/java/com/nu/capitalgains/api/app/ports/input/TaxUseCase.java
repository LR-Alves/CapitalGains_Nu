package com.nu.capitalgains.api.app.ports.input;

import com.nu.capitalgains.api.app.dto.OperationRequest;
import com.nu.capitalgains.api.app.dto.TaxResponse;

import java.util.List;

public interface TaxUseCase {


    List<List<TaxResponse>> calculateTaxes(List<List<OperationRequest>> operations);


}
