package org.egov.lndcalculator.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.lndcalculator.repository.rowmapper.BillingSlabRowMapper;
import org.egov.lndcalculator.repository.rowmapper.CalculationRowMapper;
import org.egov.lndcalculator.web.models.BillingSlab;
import org.egov.lndcalculator.web.models.BillingSlabIds;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Repository
public class CalculationRepository {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CalculationRowMapper calculationRowMapper;
	private ObjectMapper mapper;

	private RestTemplate restTemplate;

	@Autowired
	public CalculationRepository(ObjectMapper mapper, RestTemplate restTemplate) {
		this.mapper = mapper;
		this.restTemplate = restTemplate;
	}
    /**
     * Executes the argument query on db
     * @param query The query to be executed
     * @param preparedStmtList The parameter values for the query
     * @return BillingSlabIds
     */
    public BillingSlabIds getDataFromDB(String query, List<Object> preparedStmtList){
        BillingSlabIds billingSlabIds = null;
        try {
            billingSlabIds = jdbcTemplate.query(query, preparedStmtList.toArray(), calculationRowMapper);
        }catch(Exception e) {
            log.error("Exception while fetching from DB: " + e);
            return billingSlabIds;
        }

        return billingSlabIds;
    }

    public Object fetchResult(StringBuilder uri, Object request) {
		Object response = null;
		log.debug("URI: " + uri.toString());
		try {
			log.debug("Request: " + mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from searcher: ", e);
		}

		return response;
	}
}
