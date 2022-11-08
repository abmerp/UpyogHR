package org.egov.land.calcutaor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.validation.Valid;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {

	@Autowired
	CalculatorImpl calcuImpl;

	@GetMapping("/_calculate")
	public FeeTypeCalculationDtoInfo get(@Valid @RequestBody CalculatorRequest calculatorRequest)
			throws FileNotFoundException, IOException, ParseException {

		FeeTypeCalculationDtoInfo info = new FeeTypeCalculationDtoInfo();
		FeesTypeCalculationDto calculator = calcuImpl.feesTypeCalculation(calculatorRequest);
		info.setFeeTypeCalculationDto(calculator);
		return info;
	}
}