package org.paasta.container.platform.api.roles;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class RolesServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String ROLE_NAME = "test-role-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";

    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final boolean isAdmin = true;
    private static final boolean isNotAdmin = false;

    private static HashMap gResultMap = null;
    private static HashMap gResultAdminMap = null;
    private static HashMap gResultAdminFailMap = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static Roles gResultModel = null;
    private static Roles gFinalResultModel = null;

    private static RolesAdmin gResultAdminModel = null;
    private static RolesAdmin gFinalResultAdminModel = null;
    private static RolesAdmin gFinalResultAdminFailModel = null;

    private static RolesList gResultListModel = null;
    private static RolesList gFinalResultListModel = null;
    private static RolesList gFinalResultListFailModel = null;

    private static RolesListAdmin gResultListAdminModel = null;
    private static RolesListAdmin gFinalResultListAdminModel = null;
    private static RolesListAdmin gFinalResultListAdminFailModel = null;

    private static RolesListAllNamespaces gResultListAllNamespacesModel = null;
    private static RolesListAllNamespaces gFinalResultListAllNamespacesModel = null;
    private static RolesListAllNamespaces gFinalResultListAllNamespacesFailModel = null;


    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    RolesService rolesService;

    @Before
    public void setUp() throws Exception {
        gResultMap = new HashMap();

        gResultStatusModel = new ResultStatus();
        gResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultStatusModel = new ResultStatus();
        gFinalResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_ROLES);

        // 리스트가져옴
        gResultListModel = new RolesList();
        
        gFinalResultListModel = new RolesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new RolesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new Roles();
        gFinalResultModel = new Roles();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultFailModel = new ResultStatus();
        gResultFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gResultFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultAdminMap = new HashMap();
        gResultListAdminModel = new RolesListAdmin();
        gFinalResultListAdminModel = new RolesListAdmin();

        gFinalResultListAdminModel = new RolesListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new RolesListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        // 하나만 가져옴
        gResultAdminModel = new RolesAdmin();
        gFinalResultAdminModel = new RolesAdmin();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultAdminFailModel = new RolesAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        // 리스트가져옴
        gResultListAllNamespacesModel = new RolesListAllNamespaces();

        gFinalResultListAllNamespacesModel = new RolesListAllNamespaces();
        gFinalResultListAllNamespacesModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListAllNamespacesFailModel = new RolesListAllNamespaces();
        gFinalResultListAllNamespacesFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
    }

    @Test
    public void getRolesList() {
        //when
        when(propertyService.getCpMasterApiListRolesListUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, RolesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, RolesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        RolesList resultList = rolesService.getRolesList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getRoles() {
        //when
        when(propertyService.getCpMasterApiListRolesGetUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Roles.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        Roles result = rolesService.getRoles(NAMESPACE, ROLE_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getRolesYaml() {
        //when
        when(propertyService.getCpMasterApiListRolesGetUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) rolesService.getRolesYaml(NAMESPACE, ROLE_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createRoles() {
        //when
        when(propertyService.getCpMasterApiListRolesCreateUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_ROLES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) rolesService.createRoles(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteRoles() {
        //when
        when(propertyService.getCpMasterApiListRolesDeleteUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultMap, Roles.class)).thenReturn(gResultModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_ROLES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = rolesService.deleteRoles(NAMESPACE, ROLE_NAME, gResultMap);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateRoles() {
        String nextUrl = Constants.URI_ROLES.replace("{roleName:.+}", ROLE_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListRolesUpdateUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) rolesService.updateRoles(NAMESPACE, ROLE_NAME, YAML_STRING);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void getRolesListAdmin() {
        //when
        when(propertyService.getCpMasterApiListRolesListUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);

        when(commonService.setResultObject(gResultAdminMap, RolesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, RolesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        RolesListAdmin resultList = (RolesListAdmin) rolesService.getRolesListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getRolesAdmin() {
        //when
        when(propertyService.getCpMasterApiListRolesGetUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, RolesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        //call method
        RolesAdmin result = (RolesAdmin) rolesService.getRolesAdmin(NAMESPACE, ROLE_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getRolesListAllNamespacesAdmin() {
        //when
        when(propertyService.getCpMasterApiListRolesListAllNamespacesUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/roles");

        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/roles?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, RolesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, RolesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        RolesListAdmin resultList = (RolesListAdmin) rolesService.getRolesListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }
}