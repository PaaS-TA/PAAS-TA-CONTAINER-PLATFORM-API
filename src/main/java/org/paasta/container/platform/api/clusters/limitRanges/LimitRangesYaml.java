package org.paasta.container.platform.api.clusters.limitRanges;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonMetaData;

/**
 * LimitRanges Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.22
 */
@Data
public class LimitRangesYaml {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String sourceTypeYaml;
}
