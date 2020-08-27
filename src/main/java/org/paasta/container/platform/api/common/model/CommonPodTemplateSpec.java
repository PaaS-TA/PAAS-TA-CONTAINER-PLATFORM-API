package org.paasta.container.platform.api.common.model;

import lombok.Data;

/**
 * CommonPodTemplateSpec Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
public class CommonPodTemplateSpec {
    private CommonMetaData metadata;
    private CommonPodSpec spec;
}
