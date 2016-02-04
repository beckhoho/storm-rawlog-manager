/**
 * $RCSfile: PreparedStatementAdapter.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:01 $
 *
 * Copyright 2001 CoolServlets, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.nl.util.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public class PreparedStatementAdapter extends StatementAdapter
        implements PreparedStatement {

    protected PreparedStatement pstmt;

    public PreparedStatementAdapter(PreparedStatement pstmt) {
        super(pstmt);
        this.pstmt = pstmt;
    }

    public void addBatch() throws SQLException {
        pstmt.addBatch();
    }

    public void clearParameters() throws SQLException {
        pstmt.clearParameters();
    }

    public boolean execute() throws SQLException {
        return pstmt.execute();
    }

    public ResultSet executeQuery() throws SQLException {
        return pstmt.executeQuery();
    }

    public int executeUpdate() throws SQLException {
        return pstmt.executeUpdate();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return pstmt.getMetaData();
    }


    public ParameterMetaData getParameterMetaData() throws SQLException {
        return pstmt.getParameterMetaData();
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {

    }

    public void setNString(int parameterIndex, String value) throws SQLException {

    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {

    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

    }


    public void setArray(int i, Array x) throws SQLException {
        pstmt.setArray(i, x);
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        pstmt.setURL(parameterIndex, x);
    }


    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        pstmt.setAsciiStream(parameterIndex, x, length);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
        pstmt.setBigDecimal(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        pstmt.setBinaryStream(parameterIndex, x, length);
    }

    public void setBlob(int i, Blob x) throws SQLException {
        pstmt.setBlob(i, x);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        pstmt.setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        pstmt.setByte(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        pstmt.setBytes(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader reader,
                                   int length) throws SQLException {
        pstmt.setCharacterStream(parameterIndex, reader, length);
    }

    public void setClob(int i, Clob x) throws SQLException {
        pstmt.setClob(i, x);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        pstmt.setDate(parameterIndex, x);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
        pstmt.setDate(parameterIndex, x, cal);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        pstmt.setDouble(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        pstmt.setFloat(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        pstmt.setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        pstmt.setLong(parameterIndex, x);
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        pstmt.setNull(parameterIndex, sqlType);
    }

    public void setNull(int paramIndex, int sqlType, String typeName)
            throws SQLException {
        pstmt.setNull(paramIndex, sqlType, typeName);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        pstmt.setObject(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        pstmt.setObject(parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType,
                          int scale) throws SQLException {
        pstmt.setObject(parameterIndex, x, targetSqlType, scale);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {

    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

    }

    public void setRef(int i, Ref x) throws SQLException {
        pstmt.setRef(i, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        pstmt.setShort(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        pstmt.setString(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        pstmt.setTime(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        pstmt.setTime(parameterIndex, x, cal);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        pstmt.setTimestamp(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        pstmt.setTimestamp(parameterIndex, x, cal);
    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
