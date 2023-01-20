package org.egov.tl.web.models;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTransaction {
	
	private ResponseInfo responseInfo;
	private List<Transaction> transaction;

}
