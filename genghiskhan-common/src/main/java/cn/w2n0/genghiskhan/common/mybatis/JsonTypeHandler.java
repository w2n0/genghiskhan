package cn.w2n0.genghiskhan.common.mybatis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Jackson 实现 JSON 字段类型处理器
 *
 * @author lindong
 */
@Slf4j
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonTypeHandler<T extends Object> extends BaseTypeHandler<T> {

    private Class<T> type;


    public JsonTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    private T parse(String json) {
        if (json == null || json.length() == 0) {
            return null;
        }
        return JSON.parseObject(json, type);
    }

    private String toJsonString(T obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int columnIndex, T parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(columnIndex, toJsonString(parameter));
    }
}