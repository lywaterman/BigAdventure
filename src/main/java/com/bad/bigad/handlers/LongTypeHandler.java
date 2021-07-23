package com.bad.bigad.handlers;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.bad.bigad.entity.game.Grid;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LongTypeHandler extends BaseTypeHandler<Long> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setLong(i, parameter);
    }

    @Override
    public Long getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        long result = rs.getLong(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Long getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        long result = rs.getLong(columnIndex);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Long getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        long result = cs.getLong(columnIndex);
        return result == 0 && cs.wasNull() ? null : result;
    }
}
