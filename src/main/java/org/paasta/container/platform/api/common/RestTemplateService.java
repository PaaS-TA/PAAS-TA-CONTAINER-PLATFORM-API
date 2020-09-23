package org.paasta.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.paasta.container.platform.api.adminToken.AdminToken;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.exception.CpCommonAPIException;
import org.paasta.container.platform.api.exception.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.paasta.container.platform.api.common.Constants.TARGET_COMMON_API;

/**
 * RestTemplate Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Service
public class RestTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private final String commonApiBase64Authorization;
    private final RestTemplate restTemplate;
    private final PropertyService propertyService;
    private String base64Authorization;
    private String baseUrl;

    /**
     * Instantiates a new Rest template service.
     * @param restTemplate                   the rest template
     * @param commonApiAuthorizationId       the common api authorization id
     * @param commonApiAuthorizationPassword the common api authorization password
     * @param propertyService                the property service
     */
    @Autowired
    public RestTemplateService(RestTemplate restTemplate,
                               @Value("${commonApi.authorization.id}") String commonApiAuthorizationId,
                               @Value("${commonApi.authorization.password}") String commonApiAuthorizationPassword,
                               PropertyService propertyService) {
        this.restTemplate = restTemplate;
        this.propertyService = propertyService;

        this.commonApiBase64Authorization = "Basic "
                + Base64Utils.encodeToString(
                (commonApiAuthorizationId + ":" + commonApiAuthorizationPassword).getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Send t.
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE);
    }


    /**
     * Send t.
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType) {
        return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, acceptType, MediaType.APPLICATION_JSON_VALUE);
    }


    /**
     * Send t.
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @param contentType  the content type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType) {

        setApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);
        reqHeaders.add("ACCEPT", acceptType);

        HttpEntity<Object> reqEntity;
        if (bodyObject == null) {
            reqEntity = new HttpEntity<>(reqHeaders);
        } else {
            reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        }

        LOGGER.info("<T> T SEND :: REQUEST: {} BASE-URL: {}, CONTENT-TYPE: {}", httpMethod, reqUrl, reqHeaders.get(CONTENT_TYPE));

        ResponseEntity<T> resEntity = null;

        try {
            resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", reqUrl, exception.getRawStatusCode(), exception.getMessage());

            ErrorMessage errorMessage = new ErrorMessage(Constants.RESULT_STATUS_FAIL, exception.getStatusText(), exception.getRawStatusCode(), exception.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            Map result = objectMapper.convertValue(errorMessage, Map.class);

            return (T) result;
        }

        if (resEntity.getBody() != null) {
            LOGGER.info("RESPONSE-TYPE: {}", resEntity.getBody().getClass());

            Integer[] RESULT_STATUS_SUCCESS_CODE = {200, 201, 202};

            List<Integer> intList = new ArrayList<>(RESULT_STATUS_SUCCESS_CODE.length);
            for (int i : RESULT_STATUS_SUCCESS_CODE)
            {
                intList.add(i);
            }

            if (httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.POST || httpMethod == HttpMethod.DELETE) {
                if (Arrays.asList(RESULT_STATUS_SUCCESS_CODE).contains(resEntity.getStatusCode().value()) ) {
                    ResultStatus resultStatus = new ResultStatus(Constants.RESULT_STATUS_SUCCESS, resEntity.getStatusCode().toString(), CommonStatusCode.OK.getCode(), CommonStatusCode.OK.getMsg());
                    return (T) resultStatus;
                }
            }

        } else {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
    }


    /**
     * Authorization 값을 입력한다.
     *
     * @param reqApi the reqApi
     */
    private void setApiUrlAuthorization(String reqApi) {

        String apiUrl = "";
        String authorization = "";

        // CONTAINER PLATFORM MASTER API
        if (Constants.TARGET_CP_MASTER_API.equals(reqApi)) {
            apiUrl = propertyService.getCpMasterApiUrl();
            authorization = "Bearer " + this.getAdminToken().getTokenValue();
        }

        // COMMON API
        if (TARGET_COMMON_API.equals(reqApi)) {
            apiUrl = propertyService.getCommonApiUrl();
            authorization = commonApiBase64Authorization;
        }

        this.base64Authorization = authorization;
        this.baseUrl = apiUrl;
    }

    /**
     * Admin Token 상세 정보를 조회한다.
     *
     * @return the AdminToken
     */
    public AdminToken getAdminToken() {
        this.setApiUrlAuthorization(TARGET_COMMON_API);
        String reqUrl = Constants.URI_COMMON_API_ADMIN_TOKEN_DETAIL.replace("{tokenName:.+}",Constants.TOKEN_KEY);
        AdminToken adminToken = this.send(TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, AdminToken.class);

        if(Constants.RESULT_STATUS_FAIL.equals(adminToken.getResultCode())) {
            throw new CpCommonAPIException(adminToken.getResultCode(), CommonStatusCode.NOT_FOUND.getMsg(), adminToken.getStatusCode(), adminToken.getResultMessage());
        }

        return adminToken;
    }


    /**
     * 사용자가 보낸 YAML 그대로 REST API Call 하는 메소드
     *
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendYaml(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml");
    }
}
