package org.egov.tl.validator;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.util.TLConstants;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.egov.tl.util.TLConstants.businessService_BPA;
import static org.egov.tl.util.TLConstants.businessService_TL;


@Component
@Slf4j
public class MDMSValidator {


    private ServiceRequestRepository requestRepository;

    private TradeUtil util;

    private ServiceRequestRepository serviceRequestRepository;


    @Autowired
    public MDMSValidator(ServiceRequestRepository requestRepository, TradeUtil util,
                         ServiceRequestRepository serviceRequestRepository) {
        this.requestRepository = requestRepository;
        this.util = util;
        this.serviceRequestRepository = serviceRequestRepository;
    }





    /**
     * method to validate the mdms data in the request
     *
     * @param licenseRequest
     */
    public void validateMdmsData(TradeLicenseRequest licenseRequest,Object mdmsData) {

        Map<String, String> errorMap = new HashMap<>();

        Map<String, List<String>> masterData = getAttributeValues(mdmsData);
        
        String[] masterArray = { TLConstants.ACCESSORIES_CATEGORY, TLConstants.TRADE_TYPE,
                                 TLConstants.OWNERSHIP_CATEGORY, TLConstants.STRUCTURE_TYPE};

        validateIfMasterPresent(masterArray, masterData);

        Map<String,String> tradeTypeUomMap = getTradeTypeUomMap(mdmsData);
        Map<String,String> accessoryeUomMap = getAccessoryUomMap(mdmsData);

        licenseRequest.getLicenses().forEach(license -> {

            String businessService = license.getBusinessService();
            if (businessService == null)
                businessService = businessService_TL;
            switch(businessService)
            {
                case businessService_TL:
                   
               
                    break;

                case businessService_BPA:
                  
                    break;
            }

        });

        if (!CollectionUtils.isEmpty(errorMap))
            throw new CustomException(errorMap);
    }



    /**
     * Validates if MasterData is properly fetched for the given MasterData names
     * @param masterNames
     * @param codes
     */
    private void validateIfMasterPresent(String[] masterNames,Map<String,List<String>> codes){
        Map<String,String> errorMap = new HashMap<>();
        for(String masterName:masterNames){
            if(CollectionUtils.isEmpty(codes.get(masterName))){
                errorMap.put("MDMS DATA ERROR ","Unable to fetch "+masterName+" codes from MDMS");
            }
        }
        if (!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }




    /**
     * Fetches all the values of particular attribute as map of field name to list
     *
     * takes all the masters from each module and adds them in to a single map
     *
     * note : if two masters from different modules have the same name then it
     *
     *  will lead to overriding of the earlier one by the latest one added to the map
     *
     * @return Map of MasterData name to the list of code in the MasterData
     *
     */
    private Map<String, List<String>> getAttributeValues(Object mdmsData) {

        List<String> modulepaths = Arrays.asList(TLConstants.TL_JSONPATH_CODE,
                TLConstants.COMMON_MASTER_JSONPATH_CODE);

        final Map<String, List<String>> mdmsResMap = new HashMap<>();
        modulepaths.forEach( modulepath -> {
            try {
                mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
            } catch (Exception e) {
                log.error("Error while fetvhing MDMS data", e);
                throw new CustomException(TLConstants.INVALID_TENANT_ID_MDMS_KEY, TLConstants.INVALID_TENANT_ID_MDMS_MSG);
            }
        });

        System.err.println(" the mdms response is : " + mdmsResMap);
        return mdmsResMap;
    }


    /**
     * Fetches map of UOM to UOMValues
     * @param mdmsData The MDMS data
     * @return
     */
    private Map<String, List<String>> getUomMap(Object mdmsData) {

        List<String> modulepaths = Arrays.asList(TLConstants.TL_JSONPATH_CODE);
        final Map<String, List<String>> mdmsResMap = new HashMap<>();

        modulepaths.forEach( modulepath -> {
            try {
                mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
            } catch (Exception e) {
                log.error("Error while fetvhing MDMS data", e);
                throw new CustomException(TLConstants.INVALID_TENANT_ID_MDMS_KEY, TLConstants.INVALID_TENANT_ID_MDMS_MSG);
            }
        });

        System.err.println(" the mdms response is : " + mdmsResMap);
        return mdmsResMap;
    }


    private Map getTradeTypeUomMap(Object mdmsData){

        List<String> tradeTypes = JsonPath.read(mdmsData,TLConstants.TRADETYPE_JSONPATH_CODE);
        List<String> tradeTypeUOM = JsonPath.read(mdmsData,TLConstants.TRADETYPE_JSONPATH_UOM);

        Map<String,String> tradeTypeToUOM = new HashMap<>();

        for (int i = 0;i < tradeTypes.size();i++){
            tradeTypeToUOM.put(tradeTypes.get(i),tradeTypeUOM.get(i));
        }

        return tradeTypeToUOM;
    }


    private Map getAccessoryUomMap(Object mdmsData){

        List<String> accessories = JsonPath.read(mdmsData,TLConstants.ACCESSORY_JSONPATH_CODE);
        List<String> accessoryUOM = JsonPath.read(mdmsData,TLConstants.ACCESSORY_JSONPATH_UOM);

        Map<String,String> accessoryToUOM = new HashMap<>();

        for (int i = 0;i < accessories.size();i++){
            accessoryToUOM.put(accessories.get(i),accessoryUOM.get(i));
        }

        return accessoryToUOM;
    }

















}
