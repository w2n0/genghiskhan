package cn.w2n0.genghiskhan.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 基础dto
 * @author:无量
 */
@Data
public class BaseDto {
    /**
     * id
     */
    @NotBlank(message = "id不能为空！",groups = Update.class)
    @ApiModelProperty(value = "唯一ID")
    private String id;

    /**
     * 租户ID
     */
    @ApiModelProperty(hidden = true)
    private String tenantId;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateUser;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

}
