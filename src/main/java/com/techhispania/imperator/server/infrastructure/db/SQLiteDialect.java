package com.techhispania.imperator.server.infrastructure.db;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

public class SQLiteDialect extends Dialect {

    public SQLiteDialect(DialectResolutionInfo info) {
        super(info);
    }

    public SQLiteDialect(DatabaseVersion version) {
        super(version);
    }
    
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new IdentityColumnSupportImpl();
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }
}
