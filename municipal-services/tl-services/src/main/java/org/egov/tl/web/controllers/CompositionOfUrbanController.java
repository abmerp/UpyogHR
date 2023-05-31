package org.egov.tl.web.controllers;

import org.egov.tl.service.CompositionOfUrbanService;
import org.egov.tl.web.models.CompositionOfUrbanRequest;
import org.egov.tl.web.models.CompositionOfUrbanResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/composition/")
public class CompositionOfUrbanController {
	
	@Autowired
	CompositionOfUrbanService compositionOfUrbanService;
	
	
	@PostMapping("_create")
	public ResponseEntity<CompositionOfUrbanResponse> saveCompositionOfUrban(@RequestBody CompositionOfUrbanRequest CompositionOfUrban){
		CompositionOfUrbanResponse compositionOfUrbanResponse=compositionOfUrbanService.saveCompositionOfUrban(CompositionOfUrban,false);
       return new ResponseEntity<>(compositionOfUrbanResponse, HttpStatus.OK);
	}
	
	@PostMapping("_update")
	public ResponseEntity<CompositionOfUrbanResponse> updateCompletionCertificate(@RequestBody CompositionOfUrbanRequest CompositionOfUrban
			,@RequestParam(value = "isScrutiny", required = false) String isScrutiny){
		CompositionOfUrbanResponse compositionOfUrbanResponse=compositionOfUrbanService.saveCompositionOfUrban(CompositionOfUrban,isScrutiny!=null&&!isScrutiny.equals("0")?true:false);   
       return new ResponseEntity<>(compositionOfUrbanResponse, HttpStatus.OK);
	}
	
	@PostMapping("_get")
	public ResponseEntity<CompositionOfUrbanResponse> getBeneficial(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber
			) {
		CompositionOfUrbanResponse compositionOfUrbanResponse=compositionOfUrbanService.getCompositionOfUrban(requestInfoWrapper.getRequestInfo(), applicationNumber);   
       return new ResponseEntity<>(compositionOfUrbanResponse, HttpStatus.OK);
	}
	
}
