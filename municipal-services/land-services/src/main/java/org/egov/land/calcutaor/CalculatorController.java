package org.egov.land.calcutaor;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.parser.ParseException;


import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {



	@GetMapping("/_calculate")
	public FeeTypeCalculationDtoInfo get(@RequestParam("arce") float arce, @RequestParam("feeType") String feeType,
			@RequestParam("potenialZone") String potenialZone, @RequestParam("purposename") String purposename,
			@RequestParam("colonyType") String colonyType) throws FileNotFoundException, IOException, ParseException {
		
		FeeTypeCalculationDtoInfo info = new FeeTypeCalculationDtoInfo();
		FeesTypeCalculationDto calculator = Calculator.feesTypeCalculation(arce, feeType, potenialZone, purposename, colonyType);
		info.setFeeTypeCalculationDto(calculator);
		return info;
	}
}