package cn.w2n0.genghiskhan.common.repo;

import lombok.Data;

import java.util.Date;

/**
 * 基础查询
 * @author:无量
 */
@Data
public class BaseQuery {
    /**
     * 查询页码
     */
    private Integer pageNum;
    /**
     * 查询数量
     */
    private Integer pageSize;
    /**
     * id
     */
    private String id;

    /**
     * 删除标记
     */
    protected Boolean deleted;
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
    /**
     * 时间戳
     */
    private Date ts;
}
