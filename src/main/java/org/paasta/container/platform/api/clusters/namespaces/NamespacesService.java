package org.paasta.container.platform.api.clusters.namespaces;

import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesList;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesService;
import org.paasta.container.platform.api.clusters.namespaces.support.NamespacesListSupport;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasList;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.signUp.SignUpAdminService;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersListAdmin;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.paasta.container.platform.api.common.Constants.*;

/**
 * Namespaces Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.14
 */
@Service
public class NamespacesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamespacesService.class);

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final ResourceYamlService resourceYamlService;
    private final UsersService usersService;
    private final AccessTokenService accessTokenService;
    private final ResourceQuotasService resourceQuotasService;
    private final LimitRangesService limitRangesService;
    private final SignUpAdminService signUpAdminService;

    /**
     * Instantiates a new Namespace service
     *
     * @param restTemplateService   the rest template service
     * @param commonService         the common service
     * @param propertyService       the property service
     * @param resourceYamlService   the resource yaml service
     * @param usersService          the users service
     * @param accessTokenService    the access token service
     * @param resourceQuotasService the resource quotas service
     * @param limitRangesService    the limit ranges service
     */
    @Autowired
    public NamespacesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService,
                             ResourceYamlService resourceYamlService, UsersService usersService, AccessTokenService accessTokenService,
                             ResourceQuotasService resourceQuotasService, LimitRangesService limitRangesService,SignUpAdminService signUpAdminService ) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.resourceYamlService = resourceYamlService;
        this.usersService = usersService;
        this.accessTokenService = accessTokenService;
        this.resourceQuotasService = resourceQuotasService;
        this.limitRangesService = limitRangesService;
        this.signUpAdminService = signUpAdminService;
    }

    /**
     * Namespaces 상세 조회(Get Namespaces detail)
     *
     * @param namespace the namespaces
     * @return the namespaces detail
     */
    Namespaces getNamespaces(String namespace) {
        HashMap resultMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, Map.class);

        return (Namespaces) commonService.setResultModel(commonService.setResultObject(resultMap, Namespaces.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * NameSpaces 상세 조회(Get NameSpaces Admin detail)
     *
     * @param namespace the namespaces
     * @return the namespaces admin
     */
    public Object getNamespacesAdmin(String namespace) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, Map.class);

        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e) {
            return obj;
        }
        NamespacesAdmin namespacesAdmin = commonService.setResultObject(responseMap, NamespacesAdmin.class);
        namespacesAdmin = commonService.annotationsProcessing(namespacesAdmin, NamespacesAdmin.class);
        return commonService.setResultModel(namespacesAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * NameSpaces Admin 목록 조회(Get NameSpaces Admin list)
     *
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the namespaces admin list
     */
    public Object getNamespacesListAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesListUrl() + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_CLUSTER)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        NamespacesListAdmin namespacesListAdmin = commonService.setResultObject(responseMap, NamespacesListAdmin.class);
        namespacesListAdmin = commonService.resourceListProcessing(namespacesListAdmin, offset, limit, orderBy, order, searchName, NamespacesListAdmin.class);

        return commonService.setResultModel(namespacesListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * NameSpaces Admin YAML 조회(Get NameSpaces yaml)
     *
     * @param namespace    the namespace
     * @param resultMap    the result map
     * @return the namespaces yaml
     */
    public Object getNamespacesAdminYaml(String namespace, HashMap resultMap) {

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);


        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }

        //noinspection unchecked
        resultMap.put("sourceTypeYaml", response);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * NameSpaces 삭제(Delete NameSpaces)
     *
     * @param cluster the cluster
     * @param namespace the namespace
     * @return return is succeeded
     */
    public ResultStatus deleteNamespaces(String cluster, String namespace) {
        ResultStatus resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesDeleteUrl()
                        .replace("{name}", namespace), HttpMethod.DELETE, null, ResultStatus.class);

        List<String> userNamesList = usersService.getUsersNameListByNamespace(cluster, namespace).get(USERS);
        for (String userId : userNamesList) {
            usersService.deleteUsers(usersService.getUsers(cluster, namespace, userId));
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, Constants.URI_CLUSTER_NAMESPACES);
    }


    /**
     * Namespaces 생성(Create Namespaces)
     *
     * @param cluster the cluster
     * @param initTemplate the initTemplate
     * @return return is succeeded
     */
    public ResultStatus createInitNamespaces(String cluster, NamespacesInitTemplate initTemplate) {
        String namespace = initTemplate.getName();
        String nsAdminUserId = initTemplate.getNsAdminUserId();

        resourceYamlService.createNamespace(namespace);

        resourceYamlService.createInitRole(namespace);
        resourceYamlService.createNsAdminRole(namespace);

        ResultStatus saResult = resourceYamlService.createServiceAccount(nsAdminUserId, namespace);

        if (Constants.RESULT_STATUS_FAIL.equals(saResult.getResultCode())) {
            return saResult;
        }

        ResultStatus rbResult = resourceYamlService.createRoleBinding(nsAdminUserId, namespace, propertyService.getAdminRole());

        if (Constants.RESULT_STATUS_FAIL.equals(rbResult.getResultCode())) {
            LOGGER.info("CLUSTER ROLE BINDING EXECUTE IS FAILED. K8S SERVICE ACCOUNT WILL BE REMOVED...");
            restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", namespace).replace("{name}", nsAdminUserId), HttpMethod.DELETE, null, Object.class, true);
            return rbResult;
        }


        for (String rq : initTemplate.getResourceQuotasList()) {
            if (propertyService.getResourceQuotasList().contains(rq)) {
                resourceYamlService.createDefaultResourceQuota(namespace, rq);
            }
        }

        for (String lr : initTemplate.getLimitRangesList()) {
            if (propertyService.getLimitRangesList().contains(lr)) {
                resourceYamlService.createDefaultLimitRanges(namespace, lr);
            }
        }

        String saSecretName = restTemplateService.getSecretName(namespace, nsAdminUserId);

        Users newNsUser = usersService.getUsers(cluster, propertyService.getDefaultNamespace(), nsAdminUserId);
        newNsUser.setId(0);
        newNsUser.setCpNamespace(namespace);
        newNsUser.setRoleSetCode(propertyService.getAdminRole());
        newNsUser.setSaSecret(saSecretName);
        newNsUser.setSaToken(accessTokenService.getSecrets(namespace, saSecretName).getUserAccessToken());
        newNsUser.setUserType(AUTH_NAMESPACE_ADMIN);
        newNsUser.setIsActive(CHECK_Y);

        ResultStatus rsDb = usersService.createUsers(usersService.commonSaveClusterInfo(propertyService.getCpClusterName(), newNsUser));

        if (Constants.RESULT_STATUS_FAIL.equals(rsDb.getResultCode())) {
            LOGGER.info("DATABASE EXECUTE IS FAILED. K8S SERVICE ACCOUNT, CLUSTER ROLE BINDING WILL BE REMOVED...");
            restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListNamespacesDeleteUrl().replace("{namespace}", namespace), HttpMethod.DELETE, null, Object.class, true);
            restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListClusterRoleBindingsDeleteUrl().replace("{namespace}", namespace).replace("{name}", nsAdminUserId + Constants.NULL_REPLACE_TEXT + propertyService.getAdminRole() + "-binding"), HttpMethod.DELETE, null, Object.class, true);
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rsDb, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, "YOUR_NAMESPACES_LIST_PAGE");
    }


    /**
     * Namespaces 수정(Update Namespaces)
     *
     * @param cluster the cluster
     * @param namespace    the namespace
     * @param initTemplate the init template
     * @return return is succeeded
     */
    public ResultStatus modifyInitNamespaces(String cluster, String namespace, NamespacesInitTemplate initTemplate) {
        if (!namespace.equals(initTemplate.getName())) {
            return Constants.NOT_MATCH_NAMESPACES;
        }

        String nsAdminUserId = initTemplate.getNsAdminUserId();

        if(nsAdminUserId.trim().isEmpty() || nsAdminUserId == null ) {
            return Constants.REQUIRES_NAMESPACE_ADMINISTRATOR_ASSIGNMENT;
        }

         UsersListAdmin clusterAdminInfo = signUpAdminService.getClusterAdminRegister();

         for(UsersListAdmin.UserDetail clusterAdmin : clusterAdminInfo.getItems()) {
               if(clusterAdmin.getUserId().equals(nsAdminUserId)) {
                   if(clusterAdmin.getCpNamespace().equals(namespace)) {
                       return Constants.UNAPPROACHABLE_USERS;
                   }
               }
        }

        // Modify ResourceQuotas , LimitRanges
        modifyResourceQuotas(namespace, initTemplate.getResourceQuotasList());
        modifyLimitRanges(namespace, initTemplate.getLimitRangesList());


        Users newNsUser = null;

        try {
            newNsUser = usersService.getUsers(cluster, propertyService.getDefaultNamespace(), nsAdminUserId);
        }
        catch(NullPointerException e){
            LOGGER.info("THERE ARE NO USERS IN THE TEMP NAMESPACE.....");
            return Constants.UNAPPROACHABLE_USERS;

        }


        Users nsAdminUser = null;

        try {
            nsAdminUser = usersService.getUsersByNamespaceAndNsAdmin(cluster, namespace);
        }
        catch(NullPointerException e){
            LOGGER.info("NAMESPACE ADMINISTRATOR DOES NOT EXIST...");
        }


        if (nsAdminUser != null && !nsAdminUser.getUserId().equals(initTemplate.getNsAdminUserId())) {
            LOGGER.info("THE CURRENT NAMESPACE ADMINISTRATOR EXISTS AND CHANGES TO A NEW NAMESPACE ADMINISTRATOR....");
            //delete current namespace admin
            nsAdminUser.setUserType(AUTH_USER);
            usersService.updateUsers(nsAdminUser);
        }

        if (nsAdminUser == null || !nsAdminUser.getUserId().equals(initTemplate.getNsAdminUserId())) {

            LOGGER.info("WHEN THE CURRENT NAMESPACE ADMINISTRATOR DOES NOT EXIST OR CHANGES TO A NEW NAMESPACE ADMINISTRATOR.....");

            // Delete the existing user account
            Users newNamespaceAdmin = null;
            try {
                // Verify that the new namespace admin is the current namespace member
                newNamespaceAdmin = usersService.getUsers(cluster, namespace, nsAdminUserId);
            } catch (NullPointerException e) {
                LOGGER.info("THE NEW NAMESPACE ADMINISTRATOR IS NOT A CURRENT NAMESPACE MEMBER.....");
            }

            if (newNamespaceAdmin != null) {
                // If the new namespace admin is current a namespace member, it deletes the user.
                usersService.deleteUsers(newNamespaceAdmin);

            }

            // create admin and init role
            resourceYamlService.createNsAdminRole(namespace);
            resourceYamlService.createInitRole(namespace);


            ResultStatus saResult = resourceYamlService.createServiceAccount(nsAdminUserId, namespace);

            if (Constants.RESULT_STATUS_FAIL.equals(saResult.getResultCode())) {
                return saResult;
            }

            ResultStatus rbResult = resourceYamlService.createRoleBinding(nsAdminUserId, namespace, propertyService.getAdminRole());

            if (Constants.RESULT_STATUS_FAIL.equals(rbResult.getResultCode())) {
                LOGGER.info("CLUSTER ROLE BINDING EXECUTE IS FAILED. K8S SERVICE ACCOUNT WILL BE REMOVED...");
                restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", namespace).replace("{name}", nsAdminUserId), HttpMethod.DELETE, null, Object.class, true);
                return rbResult;
            }
            String saSecretName = restTemplateService.getSecretName(namespace, nsAdminUserId);


            newNsUser.setId(0);
            newNsUser.setCpNamespace(namespace);
            newNsUser.setRoleSetCode(propertyService.getAdminRole());
            newNsUser.setSaSecret(saSecretName);
            newNsUser.setSaToken(accessTokenService.getSecrets(namespace, saSecretName).getUserAccessToken());
            newNsUser.setUserType(AUTH_NAMESPACE_ADMIN);
            newNsUser.setIsActive(CHECK_Y);

            usersService.createUsers(usersService.commonSaveClusterInfo(propertyService.getCpClusterName(), newNsUser));

        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(Constants.SUCCESS_RESULT_STATUS, Constants.RESULT_STATUS_SUCCESS, "YOUR_NAMESPACES_DETAIL_PAGE");
    }


    /**
     * ResourceQuotas 변경(Modify ResourceQuotas)
     *
     * @param namespace            the namespace
     * @param requestUpdatedRqList the request update resourceQuotas list
     */
    private void modifyResourceQuotas(String namespace, List<String> requestUpdatedRqList) {
        ResourceQuotasList resourceQuotasList = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasListUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, ResourceQuotasList.class);

        List<String> k8sResourceQuotasList = resourceQuotasList.getItems().stream().map(a -> a.getMetadata().getName()).collect(Collectors.toList());

        ArrayList<String> toBeDelete = commonService.compareArrayList(k8sResourceQuotasList, requestUpdatedRqList);
        ArrayList<String> toBeAdd = commonService.compareArrayList(requestUpdatedRqList, k8sResourceQuotasList);

        // delete
        for (String deleteRqName : toBeDelete) {
            resourceQuotasService.deleteResourceQuotas(namespace, deleteRqName);
        }

        // add
        for (String rqName : toBeAdd) {
            resourceYamlService.createDefaultResourceQuota(namespace, rqName);
        }
    }


    /**
     * LimitRanges 변경(Modify LimitRanges)
     *
     * @param namespace            the namespace
     * @param requestUpdatedLrList the request update limitRanges list
     */
    private void modifyLimitRanges(String namespace, List<String> requestUpdatedLrList) {
        LimitRangesList limitRangesList = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesListUrl().replace("{namespace}", namespace),
                HttpMethod.GET, null, LimitRangesList.class);

        List<String> k8sLimitRangesList = limitRangesList.getItems().stream().map(lr -> lr.getMetadata().getName()).collect(Collectors.toList());

        ArrayList<String> toBeDelete = commonService.compareArrayList(k8sLimitRangesList, requestUpdatedLrList);
        ArrayList<String> toBeAdd = commonService.compareArrayList(requestUpdatedLrList, k8sLimitRangesList);

        for (String lrName : toBeAdd) {
            resourceYamlService.createDefaultLimitRanges(namespace, lrName);
        }

        for (String deleteLrName : toBeDelete) {
            limitRangesService.deleteLimitRanges(namespace, deleteLrName);
        }
    }


    /**
     * Namespace SelectBox 를 위한 전체 목록 조회(Get all list for NameSpace SelectBox)
     *
     * @return the namespaces admin
     */
    public Object getNamespacesListForSelectbox() {
        NamespacesListAdmin namespacesListAdmin = (NamespacesListAdmin) getNamespacesListAdmin(0, 0, "name", "asc", "");
        List<NamespacesListAdminItem> namespaceItem = namespacesListAdmin.getItems();

        List<String> returnNamespaceList = new ArrayList<>();
        NamespacesListSupport namespacesListSupport = new NamespacesListSupport();

        // all namespaces
        returnNamespaceList.add(ALL_NAMESPACES);

        for (NamespacesListAdminItem n : namespaceItem) {
            returnNamespaceList.add(n.getName());
        }

        namespacesListSupport.setItems(returnNamespaceList);


        return commonService.setResultModel(namespacesListSupport, Constants.RESULT_STATUS_SUCCESS);
    }

}
