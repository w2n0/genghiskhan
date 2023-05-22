package cn.w2n0.genghiskhan.common.dto.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 基础分页查询
 * @author:无量
 */
@Data
public class BasePagingQry extends BaseCmd {
    /**
     * 查询页码
     */
    @Min(value = 1,message = "需传入页码且至少为1")
    @NotNull( message = "需传入页码且至少为1")
    @ApiModelProperty(value = "页码",required = true,position = 2,example="1")
    private Integer pageNum;
    /**
     * 查询数量
     */
    @Min(value = 1,message = "需传入每页显示条数且至少为1")
    @NotNull( message = "需传入页码且至少为1")
    @ApiModelProperty(value = "每页显示条数",required = true,position = 3,example="10")
    private Integer pageSize;
}
