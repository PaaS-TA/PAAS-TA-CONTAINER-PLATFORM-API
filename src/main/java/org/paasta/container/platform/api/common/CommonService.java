package org.paasta.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Common Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Service
public class CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
    private final Gson gson;

    /**
     * Instantiates a new Common service.
     *
     * @param gson the gson
     */
    @Autowired
    public CommonService(Gson gson) {this.gson = gson;}


    /**
     * Sets result model.
     *
     * @param reqObject  the req object
     * @param resultCode the result code
     * @return the result model
     */
    public Object setResultModel(Object reqObject, String resultCode) {
        try {
            Class<?> aClass = reqObject.getClass();
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(reqObject, Map.class);

            Method methodSetResultCode = aClass.getMethod("setResultCode", String.class);

            if (Constants.RESULT_STATUS_FAIL.equals(map.get("resultCode"))) {
                methodSetResultCode.invoke(reqObject, map.get("resultCode"));
            } else {
                methodSetResultCode.invoke(reqObject, resultCode);
            }

        } catch (NoSuchMethodException e) {
            LOGGER.error("NoSuchMethodException :: {}", e);
        } catch (IllegalAccessException e1) {
            LOGGER.error("IllegalAccessException :: {}", e1);
        } catch (InvocationTargetException e2) {
            LOGGER.error("InvocationTargetException :: {}", e2);
        }

        return reqObject;
    }

    /**
     * 생성/수정/삭제 후 페이지 이동을 위한 Sets result model
     *
     * @param reqObject
     * @param resultCode
     * @param nextActionUrl
     * @return
     */
    public Object setResultModelWithNextUrl(Object reqObject, String resultCode, String nextActionUrl) {
        try {
            Class<?> aClass = reqObject.getClass();
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(reqObject, Map.class);

            Method methodSetResultCode = aClass.getMethod("setResultCode", String.class);
            Method methodSetNextActionUrl = aClass.getMethod("setNextActionUrl", String.class);

            if (Constants.RESULT_STATUS_FAIL.equals(map.get("resultCode"))) {
                methodSetResultCode.invoke(reqObject, map.get("resultCode"));
            } else {
                methodSetResultCode.invoke(reqObject, resultCode);
            }

            if(nextActionUrl != null) {
                methodSetNextActionUrl.invoke(reqObject, nextActionUrl);
            }

        } catch (NoSuchMethodException e) {
            LOGGER.error("NoSuchMethodException :: {}", e);
        } catch (IllegalAccessException e1) {
            LOGGER.error("IllegalAccessException :: {}", e1);
        } catch (InvocationTargetException e2) {
            LOGGER.error("InvocationTargetException :: {}", e2);
        }

        return reqObject;
    }

    /**
     * Sets result object.
     *
     * @param <T>           the type parameter
     * @param requestObject the request object
     * @param requestClass  the request class
     * @return the result object
     */
    public <T> T setResultObject(Object requestObject, Class<T> requestClass) {
        return this.fromJson(this.toJson(requestObject), requestClass);
    }


    /**
     * To json string.
     *
     * @param requestObject the request object
     * @return the string
     */
    private String toJson(Object requestObject) {
        return gson.toJson(requestObject);
    }


    /**
     * From json t.
     *
     * @param <T>           the type parameter
     * @param requestString the request string
     * @param requestClass  the request class
     * @return the t
     */
    private <T> T fromJson(String requestString, Class<T> requestClass) {
        return gson.fromJson(requestString, requestClass);
    }
}
