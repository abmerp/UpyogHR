package org.egov.bpa.web.model;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubOccupancy {

	private List<Floor> floors;
	private String riskType;
	private BasementPosition basementPosition;

}
