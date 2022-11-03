package org.egov.land.calcutaor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.land.abm.service.ThirPartyAPiCall;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Component
public class UpdateJson {
	

	public FeesType readPurposeJson(String feeType, String potenialZone, String purposename, String colonyType)
			throws FileNotFoundException, IOException, ParseException {

	
		
		JSONParser parser = new JSONParser();
	
		Object obj = parser.parse(new FileReader("D:\\dummy\\hr\\common-masters\\Purpose.json"));
		//Object obj = parser.parse(new FileReader(path));
		
	//Object obj = parser.parse(new FileReader("/opt/hr/common-masters/Purpose.json"));
		
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray purposeMainFile = (JSONArray) jsonObject.get("Purpose");

		FeesType feesType = new FeesType();
		List<Float> farValueList = new ArrayList<>();

		float farValue = 0;

		for (Object myObject : purposeMainFile) {
			JSONObject json1 = (JSONObject) myObject;

			if (json1.get("name").equals(purposename)) {

				if (json1.get("scrutinyFeeCharges") != null) {

					JSONObject json2 = (JSONObject) json1.get("scrutinyFeeCharges");

					JSONObject json3 = (JSONObject) json2.get(colonyType);

					farValueList.add(farValue);
					feesType.setScrutinyFeeCharges(Float.parseFloat(json3.get("far").toString()));
				}

				if (json1.get("licenseFeeCharges") != null) {
					JSONObject json2 = (JSONObject) json1.get("licenseFeeCharges");
					JSONObject json3 = (JSONObject) json2.get(colonyType);

					farValueList.add(farValue);
					feesType.setLicenseFeeCharges(Float.parseFloat(json3.get("far").toString()));
				}

				if (json1.get("conversionCharges") != null) {
					JSONObject json2 = (JSONObject) json1.get("conversionCharges");
					JSONObject json3 = (JSONObject) json2.get(colonyType);

					farValueList.add(farValue);
					feesType.setConversionCharges(Float.parseFloat(json3.get("far").toString()));
				}

				if (json1.get("externalDevelopmentCharges") != null) {
					JSONObject json2 = (JSONObject) json1.get("externalDevelopmentCharges");
					JSONObject json3 = (JSONObject) json2.get(colonyType);

					farValueList.add(farValue);
					feesType.setExternalDevelopmentCharges(Float.parseFloat(json3.get("far").toString()));
				}

				if (json1.get("stateInfrastructureDevelopmentCharges") != null) {
					JSONObject json2 = (JSONObject) json1.get("stateInfrastructureDevelopmentCharges");
					JSONObject json3 = (JSONObject) json2.get(colonyType);

					farValueList.add(farValue);
					feesType.setStateInfrastructureDevelopmentCharges(Float.parseFloat(json3.get("far").toString()));
				}

			}
		}
		feesType.setFarValue(farValueList);
		return feesType;
	}

}
