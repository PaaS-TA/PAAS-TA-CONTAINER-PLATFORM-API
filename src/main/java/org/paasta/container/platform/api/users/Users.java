package org.paasta.container.platform.api.users;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.util.StringUtils;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;

/**
 * User Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/

@Data
public class Users {
    public String resultCode;
    public String resultMessage;
    public Integer httpStatusCode;
    public String detailMessage;

    public long id;
    public String userId;
    public String password;
    public String passwordConfirm;
    public String email;
    public String clusterName;
    public String clusterApiUrl;
    public String clusterToken;
    public String cpNamespace;
    public String cpAccountTokenName;
    public String serviceAccountName;
    public String saSecret;
    public String saToken;
    public String isActive;
    public String roleSetCode;
    public String description;
    public String userType;
    public String created;
    public String lastModified;

    private String browser;
    private String clientIp;

    // user 생성 시 multi namespaces, roles
    private List<NamespaceRole> selectValues;

    @Data
    public static class NamespaceRole {
        private String namespace;
        private String role;
    }

    public String getUserId() {
        return CommonUtils.procReplaceNullValue(userId);
    }

    public String getPassword() {
        return CommonUtils.procReplaceNullValue(password);
    }

    public String getPasswordConfirm() {
        return CommonUtils.procReplaceNullValue(passwordConfirm);
    }

    public String getEmail() {
        return CommonUtils.procReplaceNullValue(email);
    }

    public String getClusterName() {
        return CommonUtils.procReplaceNullValue(clusterName);
    }

    public String getClusterApiUrl() {
        return CommonUtils.procReplaceNullValue(clusterApiUrl);
    }

    public String getClusterToken() {
        return CommonUtils.procReplaceNullValue(clusterToken);
    }

    public String getCpNamespace() {
        return CommonUtils.procReplaceNullValue(cpNamespace);
    }

    public String getServiceAccountName() {
        return CommonUtils.procReplaceNullValue(serviceAccountName);
    }

    public String getSaSecret() {
        return CommonUtils.procReplaceNullValue(saSecret);
    }

    public String getSaToken() {
        return CommonUtils.procReplaceNullValue(saToken);
    }

    public String getRoleSetCode() {
        return CommonUtils.procReplaceNullValue(roleSetCode);
    }

    public String getCpAccountTokenName() {
        return CommonUtils.procReplaceNullValue(cpAccountTokenName);
    }

    public String getDescription() {
        return CommonUtils.procReplaceNullValue(description);
    }

    public String getBrowser() {
        return CommonUtils.procReplaceNullValue(browser);
    }

    public String getClientIp() {
        return CommonUtils.procReplaceNullValue(clientIp);
    }

    public List<NamespaceRole> getSelectValues() {
        return (StringUtils.isEmpty(selectValues)) ? new ArrayList<NamespaceRole>() {{
            add(new Users.NamespaceRole() {{
                setNamespace(Constants.NULL_REPLACE_TEXT);
                setRole(Constants.NULL_REPLACE_TEXT);
            }});
        }} : selectValues;
    }
}
