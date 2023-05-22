package cn.w2n0.genghiskhan.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 基础实体
 * @author:无量
 */
@Data
public class BaseEntity {
    /**
     * 唯一ID
     */
    private String id;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 租户ID
     */
    private String tenantId;


}
