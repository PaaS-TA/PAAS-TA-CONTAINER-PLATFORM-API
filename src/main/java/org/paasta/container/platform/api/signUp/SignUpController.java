package org.paasta.container.platform.api.signUp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.MessageConstant;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.config.NoAuth;
import org.paasta.container.platform.api.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.paasta.container.platform.api.common.CommonUtils.regexMatch;
import static org.paasta.container.platform.api.common.CommonUtils.stringNullCheck;

/**
 * Sign Up Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Api(value = "SignUpController v1")
@RestController
public class SignUpController {

    private final SignUpUserService signUpUserService;
    private final SignUpAdminService signUpAdminService;

    /**
     * Instantiates a new SignUp controller
     *
     * @param signUpUserService the signUpUserService service
     * @param signUpAdminService the signUpAdminService service
     */
    @Autowired
    public SignUpController(SignUpUserService signUpUserService, SignUpAdminService signUpAdminService) {
        this.signUpUserService = signUpUserService;
        this.signUpAdminService = signUpAdminService;
    }


    /**
     * 회원가입(Sign Up)
     *
     * @param requestUsers the requestUsers
     * @return the resultStatus
     */
    @ApiOperation(value = "회원가입(Sign Up)", nickname = "signUpUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestUsers", value = "요청한 유저", required = true, dataType = "Object", paramType = "body")
    })
    @NoAuth
    @PostMapping(value = Constants.URI_SIGN_UP)
    public ResultStatus signUpUsers(@RequestBody Object requestUsers) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.convertValue(requestUsers, Map.class);

        Users users = objectMapper.convertValue(map, Users.class);

        // input parameter regex
        if(!Constants.RESULT_STATUS_SUCCESS.equals(regexMatch(users))) {
            return ResultStatus.builder().resultCode(Constants.RESULT_STATUS_FAIL)
                    .resultMessage(MessageConstant.RE_CONFIRM_INPUT_VALUE)
                    .httpStatusCode(400)
                    .detailMessage(regexMatch(users)).build();
        }


        // id duplication check
        if(duplicatedUserIdCheck(users)) {
            return ResultStatus.builder().resultCode(Constants.RESULT_STATUS_FAIL)
                    .resultMessage(MessageConstant.DUPLICATE_USER_ID)
                    .httpStatusCode(409)
                    .detailMessage(MessageConstant.DUPLICATE_USER_ID).build();
        }

        // for Admin
        if(users.getClusterToken() != null) {
            Object obj = stringNullCheck(requestUsers);
            if(obj instanceof ResultStatus) {
                return (ResultStatus) obj;
            }

            return signUpAdminService.signUpAdminUsers(users);
        }

        return signUpUserService.signUpUsers(users);
    }


    /**
     * Users 이름 목록 조회(Get Users names list)
     *
     * @return the map
     */
    @ApiOperation(value = "Users 이름 목록 조회(Get Users names list)", nickname = "getUsersNameList")
    @GetMapping(value = "/users/names")
    public Map<String, List<String>> getUsersNameList() {
        return signUpUserService.getUsersNameList();
    }


    /**
     * User ID 중복 체크(Duplication check User ID)
     *
     * @param users the users
     * @return the boolean
     */
    public Boolean duplicatedUserIdCheck(Users users) {
        Boolean isDuplicated = false;
        List<String> list = getUsersNameList().get(Constants.USERS);
        for (String name:list) {
            if (name.equals(users.getUserId())) {
                isDuplicated = true;
            }
        }
        return isDuplicated;
    }
}
