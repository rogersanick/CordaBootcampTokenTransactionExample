package com.bootcamp.schema;

import net.corda.core.node.AppServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;

import java.sql.SQLException;

@CordaService
public class SchemaInitializer extends SingletonSerializeAsToken {

    private AppServiceHub serviceHub;

    public SchemaInitializer(AppServiceHub appServiceHub) {
        this.serviceHub = appServiceHub;
        try {
            this.init();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() throws SQLException {

        String query = "create table if not exists token_child_states(owner varchar(100), issuer varchar(100), amount integer, child_proof varchar(100))";

        java.sql.Connection session = serviceHub.jdbcSession();
        java.sql.PreparedStatement preparedStatements = session.prepareStatement(query);

        try {
            preparedStatements.execute();
        } catch (Error SQLException) {
            throw SQLException;
        }

    }

}