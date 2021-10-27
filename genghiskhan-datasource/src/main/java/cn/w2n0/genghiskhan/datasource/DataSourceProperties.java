package cn.w2n0.genghiskhan.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.util.List;
/**
 * 数据源属性
 * @author 无量
 * date 2021/10/20 14:24
 */
@ConfigurationProperties(prefix = "genghiskhan.datasource")
@Data
public class DataSourceProperties {
    @NotNull
    private List<DataSourceItemProperties> items;


}
