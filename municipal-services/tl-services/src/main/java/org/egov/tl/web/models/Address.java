package org.egov.tl.web.models;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**	
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@ApiModel(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address   {

        @Size(max=64)
        @SafeHtml
        @JsonProperty("id")
        private String id;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("tenantId")
        private String tenantId = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("doorNo")
        private String doorNo = null;

        @JsonProperty("latitude")
        private Double latitude = null;

        @JsonProperty("longitude")
        private Double longitude = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("addressId")
        private String addressId = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("addressNumber")
        private String addressNumber = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("type")
        private String type = null;

        @SafeHtml
        @JsonProperty("addressLine1")
        private String addressLine1 = null;

        @Size(max=256)
        @SafeHtml
        @JsonProperty("addressLine2")
        private String addressLine2 = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("landmark")
        private String landmark = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("city")
        private String city = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("pincode")
        private String pincode = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("detail")
        private String detail = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("buildingName")
        private String buildingName = null;

        @Size(max=64)
        @SafeHtml
        @JsonProperty("street")
        private String street = null;

        @Valid
        @JsonProperty("locality")
        private Boundary locality = null;

       
     

        boolean isInvalid() {
            return isPinCodeInvalid()
                    || isCityInvalid()
                    || isAddressInvalid();
        }

        boolean isNotEmpty() {
            return StringUtils.isNotEmpty(pincode)
                    || StringUtils.isNotEmpty(city)
                    || StringUtils.isNotEmpty(addressId);
        }

        boolean isPinCodeInvalid() {
            return pincode != null && pincode.length() > 10;
        }

        boolean isCityInvalid() {
            return city != null && city.length() > 300;
        }

        boolean isAddressInvalid() {
            return addressId != null && addressId.length() > 300;
        }


}

