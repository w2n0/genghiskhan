package cn.w2n0.genghiskhan.entity;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 分页对象
 * @author 无量
 */
@Data
public class DataPager<T> implements Serializable {

    /**
     * 页码
     */
    private long pageNum;
    /**
     * 每页记录数
     */
    private long size;
    /**
     * 总页数
     */
    private long pages;
    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> rows;

    public static <T> DataPager<T> result(List<?> result)
    {
        DataPager dataPager=new DataPager();
        if(result instanceof Page)
        {
            dataPager.setPageNum(new PageInfo(result).getPageNum());
            dataPager.setPages(new PageInfo(result).getPages());
            dataPager.setSize(new PageInfo(result).getPageSize());
            dataPager.setTotal(new PageInfo(result).getTotal());
        }
        dataPager.setRows(result);
        return dataPager;
    }

}
