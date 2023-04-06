package org.egov.user.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.egov.user.abm.developer.contract.SsoCitizen;
import org.egov.user.domain.exception.*;
import org.egov.user.domain.model.LoggedInUserUpdatePasswordRequest;
import org.egov.user.domain.model.NonLoggedInUserUpdatePasswordRequest;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.UserSearchCriteria;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.service.utils.EncryptionDecryptionUtil;
import org.egov.user.domain.service.utils.NotificationUtil;
import org.egov.user.persistence.dto.FailedLoginAttempt;
import org.egov.user.persistence.repository.FileStoreRepository;
import org.egov.user.persistence.repository.OtpRepository;
import org.egov.user.persistence.repository.UserRepository;
import org.egov.user.web.contract.Otp;
import org.egov.user.web.contract.OtpValidateRequest;
import org.egov.user.web.contract.TokenResponse;
import org.egov.user.web.contract.UserSearchRequest;
import org.egov.user.web.contract.UserSearchResponse;
import org.egov.user.web.contract.UserSearchResponseContent;
import org.egov.user.web.controller.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.egov.tracer.http.HttpUtils.isInterServiceCall;
import static org.egov.user.config.UserServiceConstants.USER_CLIENT_ID;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
public class UserService {

	@Value("${tcp.url}")
	public String tcpurl;
	@Value("${tcp.is.existSSO.Token}")
	public String tcpExistSSoNumber;
	@Value("${tcp.return.url}")
	public String tcpReturnUrl;
	@Value("${contextPath.citizen.stakeholder}")
	public String citizenStakeholder;
	@Value("${contextPath.citizen.newlicense}")
	public String citizenNewLicense;

	@Value("${egov.user.search.default.size}")
	private Integer defaultSearchSize;

	@Value("${tcp.sso.authorized.url}")
	private String ssoAuthorizedUrl;

	private UserRepository userRepository;
	private OtpRepository otpRepository;
	private PasswordEncoder passwordEncoder;
	private int defaultPasswordExpiryInDays;
	private boolean isCitizenLoginOtpBased;
	private boolean isEmployeeLoginOtpBased;
	private FileStoreRepository fileRepository;
	private EncryptionDecryptionUtil encryptionDecryptionUtil;
	private TokenStore tokenStore;
	UserController userController;

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${account.unlock.cool.down.period.minutes}")
	private Long accountUnlockCoolDownPeriod;

	@Value("${max.invalid.login.attempts.period.minutes}")
	private Long maxInvalidLoginAttemptsPeriod;

	@Value("${create.user.validate.name}")
	private boolean createUserValidateName;

	@Value("${max.invalid.login.attempts}")
	private Long maxInvalidLoginAttempts;

	@Value("${egov.user.pwd.pattern}")
	private String pwdRegex;

	@Value("${egov.user.pwd.pattern.min.length}")
	private Integer pwdMinLength;

	@Value("${egov.user.pwd.pattern.max.length}")
	private Integer pwdMaxLength;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private NotificationUtil notificationUtil;
	private UserSearchCriteria userSearchCriteria;

	public UserService(UserRepository userRepository, OtpRepository otpRepository, FileStoreRepository fileRepository,
			PasswordEncoder passwordEncoder, EncryptionDecryptionUtil encryptionDecryptionUtil, TokenStore tokenStore,
			@Value("${default.password.expiry.in.days}") int defaultPasswordExpiryInDays,
			@Value("${citizen.login.password.otp.enabled}") boolean isCitizenLoginOtpBased,
			@Value("${employee.login.password.otp.enabled}") boolean isEmployeeLoginOtpBased,
			@Value("${egov.user.pwd.pattern}") String pwdRegex,
			@Value("${egov.user.pwd.pattern.max.length}") Integer pwdMaxLength,
			@Value("${egov.user.pwd.pattern.min.length}") Integer pwdMinLength) {
		this.userRepository = userRepository;
		this.otpRepository = otpRepository;
		this.passwordEncoder = passwordEncoder;
		this.defaultPasswordExpiryInDays = defaultPasswordExpiryInDays;
		this.isCitizenLoginOtpBased = isCitizenLoginOtpBased;
		this.isEmployeeLoginOtpBased = isEmployeeLoginOtpBased;
		this.fileRepository = fileRepository;
		this.encryptionDecryptionUtil = encryptionDecryptionUtil;
		this.tokenStore = tokenStore;
		this.pwdRegex = pwdRegex;
		this.pwdMaxLength = pwdMaxLength;
		this.pwdMinLength = pwdMinLength;

	}

	/**
	 * get user By UserName And TenantId
	 *
	 * @param userName
	 * @param tenantId
	 * @return
	 */
	public User getUniqueUser(String userName, String tenantId, UserType userType) {

		UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder().userName(userName)
				.tenantId(getStateLevelTenantForCitizen(tenantId, userType)).type(userType).build();

		if (isEmpty(userName) || isEmpty(tenantId) || isNull(userType)) {
			log.error("Invalid lookup, mandatory fields are absent");
			throw new UserNotFoundException(userSearchCriteria);
		}

		/* encrypt here */

		userSearchCriteria = encryptionDecryptionUtil.encryptObject(userSearchCriteria, "UserSearchCriteria",
				UserSearchCriteria.class);
		List<User> users = userRepository.findAll(userSearchCriteria);

		if (users.isEmpty())
			throw new UserNotFoundException(userSearchCriteria);
		if (users.size() > 1)
			throw new DuplicateUserNameException(userSearchCriteria);

		return users.get(0);
	}

	public User getUserByUuid(String uuid) {

		UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder().uuid(Collections.singletonList(uuid))
				.build();

		if (isEmpty(uuid)) {
			log.error("UUID is mandatory");
			throw new UserNotFoundException(userSearchCriteria);
		}

		List<User> users = userRepository.findAll(userSearchCriteria);

		if (users.isEmpty())
			throw new UserNotFoundException(userSearchCriteria);
		return users.get(0);
	}

	/**
	 * get the users based on on userSearch criteria
	 *
	 * @param searchCriteria
	 * @return
	 */

	public List<org.egov.user.domain.model.User> searchUsers(UserSearchCriteria searchCriteria,
			boolean isInterServiceCall, RequestInfo requestInfo) {

		searchCriteria.validate(isInterServiceCall);

		searchCriteria
				.setTenantId(getStateLevelTenantForCitizen(searchCriteria.getTenantId(), searchCriteria.getType()));
		/* encrypt here / encrypted searchcriteria will be used for search */

		String altmobnumber = null;

		if (searchCriteria.getMobileNumber() != null) {
			altmobnumber = searchCriteria.getMobileNumber();
		}
		if (searchCriteria.getParentid() != null && searchCriteria.getParentid() > 0) {
			searchCriteria.setParentid(searchCriteria.getParentid());
		}
		searchCriteria = encryptionDecryptionUtil.encryptObject(searchCriteria, "UserSearchCriteria",
				UserSearchCriteria.class);

		if (altmobnumber != null) {
			searchCriteria.setAlternatemobilenumber(altmobnumber);
		}

		List<org.egov.user.domain.model.User> list = userRepository.findAll(searchCriteria);

		/* decrypt here / final reponse decrypted */

		list = encryptionDecryptionUtil.decryptObject(list, "UserList", User.class, requestInfo);

		setFileStoreUrlsByFileStoreIds(list);
		return list;
	}

	/**
	 * api will create the user based on some validations
	 *
	 * @param user
	 * @return
	 */
	public User createUser(User user, RequestInfo requestInfo) {
		user.setUuid(UUID.randomUUID().toString());
		user.setActive(true);
		user.validateNewUser(createUserValidateName);
		Map<String, Object> authorizedValid = new HashMap<>();
		authorizedValid.put("EmailID", user.getEmailId());
		authorizedValid.put("Mobile", user.getMobileNumber());
		authorizedValid.put("DeveloperName", user.getName());
		conditionallyValidateOtp(user);
		/* encrypt here */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
		validateUserUniqueness(user);
		if (isEmpty(user.getPassword())) {
			user.setPassword(UUID.randomUUID().toString());
		} else {
			validatePassword(user.getPassword());
		}

		user.setPassword(encryptPwd(user.getPassword()));
		user.setDefaultPasswordExpiry(defaultPasswordExpiryInDays);
		user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
		User persistedNewUser = persistNewUser(user);
		log.info("persistedNewUser" + persistedNewUser);
		ResponseEntity<Map> responseAuthorizedSSo = validSSOAuthorized(authorizedValid);
		log.info("responseAuthorizedSSo:" + responseAuthorizedSSo.getBody().get("status"));
		return encryptionDecryptionUtil.decryptObject(persistedNewUser, "User", User.class, requestInfo);

		/* decrypt here because encrypted data coming from DB */

	}

	private void validateUserUniqueness(User user) {
		if (userRepository.isUserPresent(user.getUsername(),
				getStateLevelTenantForCitizen(user.getTenantId(), user.getType()), user.getType()))
			throw new DuplicateUserNameException(UserSearchCriteria.builder().userName(user.getUsername())
					.type(user.getType()).tenantId(user.getTenantId()).build());
	}

	private String getStateLevelTenantForCitizen(String tenantId, UserType userType) {
		if (!isNull(userType) && userType.equals(UserType.CITIZEN) && !isEmpty(tenantId) && tenantId.contains("."))
			return tenantId.split("\\.")[0];
		else
			return tenantId;
	}

	/**
	 * api will create the citizen with otp
	 *
	 * @param user
	 * @return
	 */
	public User createCitizen(User user, RequestInfo requestInfo) {
		validateAndEnrichCitizen(user);
		return createUser(user, requestInfo);
	}

	private void validateAndEnrichCitizen(User user) {
		log.info("Validating User........");
		if (isCitizenLoginOtpBased && !StringUtils.isNumeric(user.getUsername()))
			throw new UserNameNotValidException();
		else if (isCitizenLoginOtpBased) {
			user.setMobileNumber(user.getUsername());
		}
		if (!isCitizenLoginOtpBased)
			validatePassword(user.getPassword());
		user.setRoleToCitizen();
		user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
	}

	/**
	 * api will create the citizen with otp
	 *
	 * @param user
	 * @return
	 */
	public Object registerWithLogin(User user, RequestInfo requestInfo) {
		user.setActive(true);
		createCitizen(user, requestInfo);
		return getAccess(user, user.getOtpReference());
	}

	private Object getAccess(User user, String password) {
		log.info("Fetch access token for register with login flow");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.set("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDo=");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("username", user.getUsername());
			if (!isEmpty(password))
				map.add("password", password);
			else
				map.add("password", user.getPassword());
			map.add("grant_type", "password");
			map.add("scope", "read");
			map.add("tenantId", user.getTenantId());
			map.add("isInternal", "true");
			map.add("userType", UserType.CITIZEN.name());

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			return restTemplate.postForEntity(userHost + "/user/oauth/token", request, Map.class).getBody();

		} catch (Exception e) {
			log.error("Error occurred while logging-in via register flow", e);
			throw new CustomException("LOGIN_ERROR",
					"Error occurred while logging in via register flow: " + e.getMessage());
		}
	}

	/**
	 * dependent on otpValidationMandatory filed,it will validate the otp.
	 *
	 * @param user
	 */
	private void conditionallyValidateOtp(User user) {
		if (user.isOtpValidationMandatory()) {
			if (!validateOtp(user))
				throw new OtpValidationPendingException();
		}
	}

	/**
	 * This api will validate the otp
	 *
	 * @param user
	 * @return
	 */
	public Boolean validateOtp(User user) {
		Otp otp = Otp.builder().otp(user.getOtpReference()).identity(user.getMobileNumber())
				.tenantId(user.getTenantId()).userType(user.getType()).build();
		RequestInfo requestInfo = RequestInfo.builder().action("validate").ts(new Date().getTime()).build();
		OtpValidateRequest otpValidationRequest = OtpValidateRequest.builder().requestInfo(requestInfo).otp(otp)
				.build();
		return otpRepository.validateOtp(otpValidationRequest);

	}

	/**
	 * api will update user details without otp
	 *
	 * @param user
	 * @return
	 */
	// TODO Fix date formats
	public User updateWithoutOtpValidation(User user, RequestInfo requestInfo) {
		final User existingUser = getUserByUuid(user.getUuid());
		user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
		validateUserRoles(user);
		user.validateUserModification();
		validatePassword(user.getPassword());
		user.setPassword(encryptPwd(user.getPassword()));
		/* encrypt */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
		userRepository.update(user, existingUser, requestInfo.getUserInfo().getId(),
				requestInfo.getUserInfo().getUuid());

		// If user is being unlocked via update, reset failed login attempts
		if (user.getAccountLocked() != null && !user.getAccountLocked() && existingUser.getAccountLocked())
			resetFailedLoginAttempts(user);

		User encryptedUpdatedUserfromDB = getUserByUuid(user.getUuid());
		User decryptedupdatedUserfromDB = encryptionDecryptionUtil.decryptObject(encryptedUpdatedUserfromDB, "User",
				User.class, requestInfo);
		return decryptedupdatedUserfromDB;
	}

	public void removeTokensByUser(User user) {
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(USER_CLIENT_ID,
				user.getUsername());

		for (OAuth2AccessToken token : tokens) {
			if (token.getAdditionalInformation() != null
					&& token.getAdditionalInformation().containsKey("UserRequest")) {
				if (token.getAdditionalInformation()
						.get("UserRequest") instanceof org.egov.user.web.contract.auth.User) {
					org.egov.user.web.contract.auth.User userInfo = (org.egov.user.web.contract.auth.User) token
							.getAdditionalInformation().get("UserRequest");
					if (user.getUsername().equalsIgnoreCase(userInfo.getUserName())
							&& user.getTenantId().equalsIgnoreCase(userInfo.getTenantId())
							&& user.getType().equals(UserType.fromValue(userInfo.getType())))
						tokenStore.removeAccessToken(token);
				}
			}
		}

	}

	/**
	 * this api will validate whether user roles exist in Database or not
	 *
	 * @param user
	 */
	private void validateUserRoles(User user) {
		if (user.getRoles() == null || user.getRoles() != null && user.getRoles().isEmpty()) {
			throw new AtleastOneRoleCodeException();
		}
	}

	/**
	 * this api will update user profile data except these fields userName ,
	 * mobileNumber type , password ,pwsExpiryData, roles
	 *
	 * @param user
	 * @return
	 */
	public User partialUpdate(User user, RequestInfo requestInfo) {
		/* encrypt here */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);

		User existingUser = getUserByUuid(user.getUuid());
		validateProfileUpdateIsDoneByTheSameLoggedInUser(user);
		user.nullifySensitiveFields();
		validatePassword(user.getPassword());
		userRepository.update(user, existingUser, requestInfo.getUserInfo().getId(),
				requestInfo.getUserInfo().getUuid());
		User updatedUser = getUserByUuid(user.getUuid());

		/* decrypt here */
		existingUser = encryptionDecryptionUtil.decryptObject(existingUser, "User", User.class, requestInfo);
		updatedUser = encryptionDecryptionUtil.decryptObject(updatedUser, "User", User.class, requestInfo);

		setFileStoreUrlsByFileStoreIds(Collections.singletonList(updatedUser));
		if (!(updatedUser.getEmailId().equalsIgnoreCase(existingUser.getEmailId()))) {
			notificationUtil.sendEmail(requestInfo, existingUser.getEmailId(), updatedUser.getEmailId(),
					updatedUser.getMobileNumber());
		}
		return updatedUser;
	}

	/**
	 * This api will update the password for logged-in user
	 *
	 * @param updatePasswordRequest
	 */
	public void updatePasswordForLoggedInUser(LoggedInUserUpdatePasswordRequest updatePasswordRequest) {
		updatePasswordRequest.validate();
		final User user = getUniqueUser(updatePasswordRequest.getUserName(), updatePasswordRequest.getTenantId(),
				updatePasswordRequest.getType());

		if (user.getType().toString().equals(UserType.CITIZEN.toString()) && isCitizenLoginOtpBased)
			throw new InvalidUpdatePasswordRequestException();
		if (user.getType().toString().equals(UserType.EMPLOYEE.toString()) && isEmployeeLoginOtpBased)
			throw new InvalidUpdatePasswordRequestException();

		validateExistingPassword(user, updatePasswordRequest.getExistingPassword());
		validatePassword(updatePasswordRequest.getNewPassword());
		user.updatePassword(encryptPwd(updatePasswordRequest.getNewPassword()));
		userRepository.update(user, user, user.getId(), user.getUuid());
	}

	/**
	 * This Api will update the password for non logged-in user
	 *
	 * @param request
	 */
	public void updatePasswordForNonLoggedInUser(NonLoggedInUserUpdatePasswordRequest request,
			RequestInfo requestInfo) {
		request.validate();
		// validateOtp(request.getOtpValidationRequest());
		User user = getUniqueUser(request.getUserName(), request.getTenantId(), request.getType());
		if (user.getType().toString().equals(UserType.CITIZEN.toString()) && isCitizenLoginOtpBased) {
			log.info("CITIZEN forgot password flow is disabled");
			throw new InvalidUpdatePasswordRequestException();
		}
		if (user.getType().toString().equals(UserType.EMPLOYEE.toString()) && isEmployeeLoginOtpBased) {
			log.info("EMPLOYEE forgot password flow is disabled");
			throw new InvalidUpdatePasswordRequestException();
		}
		/* decrypt here */
		/*
		 * the reason for decryption here is the otp service requires decrypted username
		 */
		user = encryptionDecryptionUtil.decryptObject(user, "User", User.class, requestInfo);
		user.setOtpReference(request.getOtpReference());
		validateOtp(user);
		validatePassword(request.getNewPassword());
		user.updatePassword(encryptPwd(request.getNewPassword()));
		/* encrypt here */
		/* encrypted value is stored in DB */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
		userRepository.update(user, user, requestInfo.getUserInfo().getId(), requestInfo.getUserInfo().getUuid());
	}

	/**
	 * Deactivate failed login attempts for provided user
	 *
	 * @param user whose failed login attempts are to be reset
	 */
	public void resetFailedLoginAttempts(User user) {
		if (user.getUuid() != null)
			userRepository.resetFailedLoginAttemptsForUser(user.getUuid());
	}

	/**
	 * Checks if user is eligible for unlock returns true, - If configured cool down
	 * period has passed since last lock else false
	 *
	 * @param user to be checked for eligibility for unlock
	 * @return if unlock able
	 */
	public boolean isAccountUnlockAble(User user) {
		if (user.getAccountLocked()) {
			boolean unlockAble = System.currentTimeMillis() - user.getAccountLockedDate() > TimeUnit.MINUTES
					.toMillis(accountUnlockCoolDownPeriod);

			log.info("Account eligible for unlock - " + unlockAble);
			log.info("Current time {}, last lock time {} , cool down period {} ", System.currentTimeMillis(),
					user.getAccountLockedDate(), TimeUnit.MINUTES.toMillis(accountUnlockCoolDownPeriod));
			return unlockAble;
		} else
			return true;
	}

	/**
	 * Perform actions where a user login fails - Fetch existing failed login
	 * attempts within configured time
	 * period{@link UserService#maxInvalidLoginAttemptsPeriod} - If failed login
	 * attempts exceeds configured {@link UserService#maxInvalidLoginAttempts} -
	 * then lock account - Add failed login attempt entry to repository
	 *
	 * @param user      user whose failed login attempt to be handled
	 * @param ipAddress IP address of remote
	 */
	public void handleFailedLogin(User user, String ipAddress, RequestInfo requestInfo) {
		if (!Objects.isNull(user.getUuid())) {
			List<FailedLoginAttempt> failedLoginAttempts = userRepository.fetchFailedAttemptsByUserAndTime(
					user.getUuid(),
					System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(maxInvalidLoginAttemptsPeriod));

			if (failedLoginAttempts.size() + 1 >= maxInvalidLoginAttempts) {
				User userToBeUpdated = user.toBuilder().accountLocked(true).password(null)
						.accountLockedDate(System.currentTimeMillis()).build();

				user = updateWithoutOtpValidation(userToBeUpdated, requestInfo);
				removeTokensByUser(user);
				log.info(
						"Locked account with uuid {} for {} minutes as exceeded max allowed attempts of {} within {} "
								+ "minutes",
						user.getUuid(), accountUnlockCoolDownPeriod, maxInvalidLoginAttempts,
						maxInvalidLoginAttemptsPeriod);
				throw new OAuth2Exception("Account locked");
			}

			userRepository.insertFailedLoginAttempt(
					new FailedLoginAttempt(user.getUuid(), ipAddress, System.currentTimeMillis(), true));
		}
	}

	/**
	 * This api will validate existing password and current password matching or not
	 *
	 * @param user
	 * @param existingRawPassword
	 */
	private void validateExistingPassword(User user, String existingRawPassword) {
		if (!passwordEncoder.matches(existingRawPassword, user.getPassword())) {
			throw new PasswordMismatchException("Invalid username or password");
		}
	}

//    /**
//     * this api will check user is exist or not, If not exist it will throw
//     * exception.
//     *
//     * @param user
//     */
//    private void validateUserPresent(User user) {
//        if (user == null) {
//            throw new UserNotFoundException(null);
//        }
//    }

	/**
	 * this api will validate, updating the profile for same logged-in user or not
	 *
	 * @param user
	 */
	private void validateProfileUpdateIsDoneByTheSameLoggedInUser(User user) {
		if (user.isLoggedInUserDifferentFromUpdatedUser()) {
			throw new UserProfileUpdateDeniedException();
		}
	}

	String encryptPwd(String pwd) {
		if (!isNull(pwd))
			return passwordEncoder.encode(pwd);
		else
			return null;
	}

	/**
	 * This api will persist the user
	 *
	 * @param user
	 * @return
	 */
	private User persistNewUser(User user) {

		return userRepository.create(user);
	}

	/**
	 * This api will fetch the fileStoreUrl By fileStoreId
	 *
	 * @param userList
	 * @throws Exception
	 */
	private void setFileStoreUrlsByFileStoreIds(List<User> userList) {
		List<String> fileStoreIds = userList.parallelStream().filter(p -> p.getPhoto() != null).map(User::getPhoto)
				.collect(Collectors.toList());
		if (!isEmpty(fileStoreIds)) {
			Map<String, String> fileStoreUrlList = null;
			try {
				fileStoreUrlList = fileRepository.getUrlByFileStoreId(userList.get(0).getTenantId(), fileStoreIds);
			} catch (Exception e) {
				// TODO Auto-generated catch block

				log.error("Error while fetching fileStore url list: " + e.getMessage());
			}

			if (fileStoreUrlList != null && !fileStoreUrlList.isEmpty()) {
				for (User user : userList) {
					user.setPhoto(fileStoreUrlList.get(user.getPhoto()));
				}
			}
		}
	}

	public void validatePassword(String password) {
		Map<String, String> errorMap = new HashMap<>();
		if (!StringUtils.isEmpty(password)) {
			if (password.length() < pwdMinLength || password.length() > pwdMaxLength)
				errorMap.put("INVALID_PWD_LENGTH", "Password must be of minimum: " + pwdMinLength + " and maximum: "
						+ pwdMaxLength + " characters.");
			Pattern p = Pattern.compile(pwdRegex);
			Matcher m = p.matcher(password);
			if (!m.find()) {
				errorMap.put("INVALID_PWD_PATTERN",
						"Password MUST HAVE: Atleast one digit, one upper case, one lower case, one special character (@#$%) and MUST NOT contain any spaces");
			}
		}
		if (!CollectionUtils.isEmpty(errorMap.keySet())) {
			throw new CustomException(errorMap);
		}
	}

	public Map<String, Object> ssoCitizen(SsoCitizen ssoCitizen, RequestInfo requestInfo) {

		requestInfo = new RequestInfo();
		org.egov.common.contract.request.User userInfo = new org.egov.common.contract.request.User();

		userInfo.setTenantId("hr");

		requestInfo.setUserInfo(userInfo);
		Map<String, Object> ssoCitizenMap = new HashMap<String, Object>();
		ssoCitizenMap.put("UserId", ssoCitizen.getUserId());
		ssoCitizenMap.put("TokenId", ssoCitizen.getTokenId());
		ResponseEntity<Map> isExistSSOToken = null;
		String ssoValue = "no";
		// requestInfo.getUserInfo().setTenantId("hr");
		log.info("Data by Request" + ssoCitizenMap);
		try {
			isExistSSOToken = isExistSSOToken(ssoCitizenMap);

			ssoValue = (String) isExistSSOToken.getBody().get("Value");

		} catch (Exception e) {
			e.printStackTrace();
		}
		String sso = "yes";
		String sso1 = "no";
		User user = new User();
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
		if (ssoValue.equalsIgnoreCase(sso)) {
			userSearchCriteria.setUserName(ssoCitizen.getEmailId());
			userSearchCriteria.setTenantId(requestInfo.getUserInfo().getTenantId());
			List<User> searchUsers = searchUsers(userSearchCriteria, true, requestInfo);
			log.info("searchUsers" + searchUsers);

			if (null == searchUsers || searchUsers.isEmpty()) {
				user.setTenantId(requestInfo.getUserInfo().getTenantId());
				user.setUsername(ssoCitizen.getEmailId());
				user.setName(ssoCitizen.getUserId());
				user.setMobileNumber(ssoCitizen.getMobileNumber());
				user.setOtpReference("123456");
				user.setIdentificationMark(ssoCitizen.getUserId());

				// User createUser = createUser(user,requestInfo);
				Object newUser = registerWithLogin(user, requestInfo);
				log.info("newUser" + newUser);
				ssoCitizenMap.put("TokenId", newUser);

				String url = (tcpReturnUrl + citizenStakeholder);
				ssoCitizenMap.put("ReturnUrl", url);
				ssoCitizenMap.put("RedirectUrl", ssoCitizen.getRedirectUrl());
				return (Map<String, Object>) ssoCitizenMap;

			} else {
				user.setUsername(ssoCitizen.getEmailId());
				user.setTenantId(requestInfo.getUserInfo().getTenantId());
				user.setOtpReference("123456");
				Object updateUser = getAccess(user, user.getOtpReference());

				String data = null;
				ObjectMapper mapper = new ObjectMapper();
				try {
					data = mapper.writeValueAsString(updateUser);
				} catch (JsonProcessingException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				}
				TokenResponse tokenResponse = null;
				ObjectReader reader = mapper.readerFor(new TypeReference<TokenResponse>() {
				});
				try {
					tokenResponse = reader.readValue(data);
				} catch (IOException e) {

					e.printStackTrace();
				}

				log.info("tokenResponse" + tokenResponse);
				Long id = tokenResponse.getUserRequest().getId();
				userInfo.setId(id);
				requestInfo.setUserInfo(userInfo);
				user.setUuid(searchUsers.get(0).getUuid());
				user.setRoles(searchUsers.get(0).getRoles());
				user.setTenantId(requestInfo.getUserInfo().getTenantId());
				user.setActive(true);
				User updatedUser = updateWithoutOtpValidation(user, requestInfo);
				log.info("updatedUser" + updatedUser);

				ssoCitizenMap.put("Token", updateUser);
				String url = (tcpReturnUrl + citizenNewLicense);
				ssoCitizenMap.put("ReturnUrl", url);
				ssoCitizenMap.put("RedirectUrl", ssoCitizen.getRedirectUrl());
				return (Map<String, Object>) ssoCitizenMap;

			}
		} else {

			try {
				ssoCitizenMap.put("ReturnUrl", ssoCitizen.getReturnUrl());
				ssoCitizenMap.put("TokenId", ssoCitizen.getTokenId());
				return ssoCitizenMap;
			} catch (Exception e) {
				log.error("Exception while creating user: ", e);

				log.error("ReturnUrl: " + ssoCitizenMap);
				throw new InvalidUserSearchCriteriaException(new UserSearchCriteria());
			}
		}

	}

	public ResponseEntity<Map> isExistSSOToken(Map<String, Object> request) {

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpExistSSoNumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("isexistSSO Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> validSSOAuthorized(Map<String, Object> request) {

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + ssoAuthorizedUrl, request, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("validSSOAuthorized\n" + response.getBody().get("status"));
		}
		
		return response;
	}
}
