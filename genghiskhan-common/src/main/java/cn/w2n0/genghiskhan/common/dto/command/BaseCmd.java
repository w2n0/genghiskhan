package cn.w2n0.genghiskhan.common.dto.command;


import cn.w2n0.genghiskhan.entity.BaseCommand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 整个应用通用的Command
 * @author:无量
 */
@Data
public class BaseCmd extends BaseCommand {
    /**
     * 租户ID
     */
    @ApiModelProperty(hidden = true)
    private String tenantId;

    /**
     * 用户ID
     */
    @ApiModelProperty(hidden = true)
    private  String userId;


}
