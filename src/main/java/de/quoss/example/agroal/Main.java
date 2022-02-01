package de.quoss.example.agroal;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.AgroalDataSourceConfiguration;
import io.agroal.api.configuration.supplier.AgroalConnectionFactoryConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalConnectionPoolConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public void run() throws SQLException {

        // configure connection factory
        AgroalConnectionFactoryConfigurationSupplier connectionFactoryConfigurationSupplier = new AgroalConnectionFactoryConfigurationSupplier();
        connectionFactoryConfigurationSupplier.jdbcUrl("jdbc:h2:mem:test");

        // configure connection pool
        AgroalConnectionPoolConfigurationSupplier connectionPoolConfigurationSupplier = new AgroalConnectionPoolConfigurationSupplier();
        connectionPoolConfigurationSupplier.connectionFactoryConfiguration(connectionFactoryConfigurationSupplier);
        connectionPoolConfigurationSupplier.maxSize(10);

        // configure data source
        AgroalDataSourceConfigurationSupplier dataSourceConfigurationSupplier = new AgroalDataSourceConfigurationSupplier();
        dataSourceConfigurationSupplier.connectionPoolConfiguration(connectionPoolConfigurationSupplier);

        // get data source from config
        AgroalDataSource dataSource = AgroalDataSource.from(dataSourceConfigurationSupplier.get());

        try (Connection c = dataSource.getConnection();
                Statement s = c.createStatement()) {
            s.execute("select table_name from information_schema.tables");
            ResultSet r = s.getResultSet();
            while (r.next()) {
                System.err.format("Table name: %s%n", r.getObject(1));
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
