package com.jpfeffer.teamcity.highlighter.service.adapter;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This class prepares MySQL DB to be used for storing/loading highlighter related data.</p>
 *
 * @author jpfeffer
 * @since 2/3/2016
 */
public class MySQLDBAdapter extends BuildServerAdapter
{
    private static final Logger LOG = Logger.getLogger(MySQLDBAdapter.class.getName());

    private String driver;
    private String url;
    private String user;
    private String password;

    @PostConstruct
    private void init()
    {
        try
        {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e)
        {
            LOG.severe(e.toString());
        }
    }

    @Override
    public void serverStartup()
    {
        LOG.info("Initializing MySQL DB for highlighter");
        try (Connection connection = DriverManager.getConnection(url, user, password))
        {
            final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
            resourceDatabasePopulator.setContinueOnError(true);
            resourceDatabasePopulator.addScript(new FileSystemResource("../webapps/ROOT/plugins/highlighter-plugin/sql/mysql_init_db.sql"));
            resourceDatabasePopulator.addScript(new FileSystemResource("../webapps/ROOT/plugins/highlighter-plugin/sql/mysql_init_table.sql"));
            resourceDatabasePopulator.populate(connection);

            LOG.info("Initialization complete!");
        }
        catch (Exception e)
        {
            LOG.log(Level.SEVERE, "MySQL DB initialization failed for highlighter", e);
        }
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
