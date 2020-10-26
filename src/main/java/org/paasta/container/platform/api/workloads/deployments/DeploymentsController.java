package org.paasta.container.platform.api.workloads.deployments;

import io.swagger.annotations.*;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Deployments Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.08
 */
@Api(value = "DeploymentsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/deployments")
public class DeploymentsController {

    private final DeploymentsService deploymentsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentsController.class);

    /**
     * Instantiates a new Deployments controller
     *
     * @param deploymentsService the deployments service
     */
    @Autowired
    public DeploymentsController(DeploymentsService deploymentsService) {
        this.deploymentsService = deploymentsService;
    }

    /**
     * Deployments 목록 조회(Get Deployments list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the deployments list
     */
    @ApiOperation(value = "Deployments 목록 조회", nickname = "getDeploymentList")
    @ApiImplicitParams({
    })
    @GetMapping
    public Object getDeploymentsList(@PathVariable(value = "cluster") String cluster,
                                     @PathVariable(value = "namespace") String namespace,
                                     @RequestParam(required = false, defaultValue = "0") int offset,
                                     @RequestParam(required = false, defaultValue = "0") int limit,
                                     @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                     @RequestParam(required = false, defaultValue = "desc") String order,
                                     @RequestParam(required = false, defaultValue = "") String searchName,
                                     @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return deploymentsService.getDeploymentsListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }

        return deploymentsService.getDeploymentsList(namespace, offset, limit, orderBy, order, searchName);
    }

    /**
     * Deployments 상세 조회(Get Deployments detail)
     * (User Portal)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the deployments detail
     */
    @ApiOperation(value = "Deployments 상세 조회", nickname = "getDeployment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "deploymentName", value = "deployment 명", required = true, dataType = "string", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "SUCCESS")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getDeployments(@PathVariable(value = "namespace") String namespace
            , @PathVariable(value = "resourceName") String resourceName
            , @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        // For Admin
        if (isAdmin) {
            return deploymentsService.getDeploymentsAdmin(namespace, resourceName);
        }

        return deploymentsService.getDeployments(namespace, resourceName);
    }


    /**
     * Deployments 상세 조회(Get Deployments detail)
     * (Admin Portal)
     *
     * @param namespace the namespace
     * @param resourceName   the resource name
     * @return the Object
     */
//    @GetMapping(value = "/{resourceName:.+}/admin")
//    public Object getDeploymentsAdmin(@PathVariable(value = "namespace") String namespace, @PathVariable(value = "resourceName") String resourceName) {
//        return deploymentsService.getDeploymentsAdmin(namespace, resourceName);
//    }

    /**
     * Deployments YAML 조회(Get Deployments yaml)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the deployments yaml
     */
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Deployments getDeploymentsYaml(@PathVariable(value = "namespace") String namespace, @PathVariable(value = "resourceName") String resourceName) {
        return deploymentsService.getDeploymentsYaml(namespace, resourceName, new HashMap<>());
    }

    /**
     * Deployments 생성(Create Deployments)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param yaml      the yaml
     * @return return is succeeded
     */
    @PostMapping
    public Object createDeployments(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @RequestBody String yaml) throws Exception {
        if (yaml.contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml);
            return object;
        }

        return deploymentsService.createDeployments(namespace, yaml);

    }

    /**
     * Deployments 삭제(Delete Deployments)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return return is succeeded
     */
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteDeployments(@PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName) {
        return deploymentsService.deleteDeployments(namespace, resourceName);
    }

    /**
     * Deployments 수정(Update Deployments)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return return is succeeded
     */
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateDeployments(@PathVariable(value = "cluster") String cluster,
                                          @PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName,
                                          @RequestBody String yaml) {
        return deploymentsService.updateDeployments(namespace, resourceName, yaml);
    }


}
