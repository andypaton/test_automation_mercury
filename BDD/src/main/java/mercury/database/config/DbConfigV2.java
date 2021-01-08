package mercury.database.config;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "mercury.database.dao" } , excludeFilters = {@Filter(value = Controller.class)})
@PropertySource(value = {"classpath:LOCAL_environment.properties", "file:${target.env}"},
ignoreResourceNotFound = true)
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        basePackages = { "mercury.database.dao" }
        )
public class DbConfigV2 {

    private static final Logger logger = LogManager.getLogger();

    @Value("${spring.datasource.driver}")
    private String driver;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.servicechannel.url:#{null}}")
    private String urlServiceChannel;

    @Value("${spring.datasource.servicechannel.user:#{null}}")
    private String usernameServiceChannel;

    @Value("${spring.datasource.servicechannel.password:#{null}}")
    private String passwordServiceChannel;

    @Value("${spring.datasource.testCity.url:#{null}}")
    private String urlTestCity;

    @Value("${spring.datasource.testCity.user:#{null}}")
    private String usernameTestCity;

    @Value("${spring.datasource.testCity.password:#{null}}")
    private String passwordTestCity;

    @Value("${spring.datasource.test.url:#{null}}")
    private String urlTestDb;

    @Value("${spring.datasource.test.username:#{null}}")
    private String testUsername;

    @Value("${spring.datasource.test.password:#{null}}")
    private String testPassword;

    @Value("${databaseName.portal}")
    private String databaseName_portal;

    @Value("${databaseName.helpdesk}")
    private String databaseName_helpdesk;

    @Value("${databaseName.test}")
    private String databaseName_test;

    @Value("${databaseName.servicechannel:#{null}}")
    private String databaseName_serviceChannel;

    @Value("${databaseName.servicechannel.recon:#{null}}")
    private String databaseName_serviceChannelRecon;

    @Value("${databaseName.testCity:#{null}}")
    private String databaseName_testCity;

    @Value("${schemaName.helpdesk}")
    private String schemaName_helpdesk;

    @Value("${schemaName.portal}")
    private String schemaName_portal;

    @Value("${schemaName.test}")
    private String schemaName_test;

    @Value("${schemaName.servicechannel:#{null}}")
    private String schemaName_serviceChannel;

    @Value("${spring.datasource.username:#{null}}")
    private String username;

    @Value("${spring.datasource.password:#{null}}")
    private String password;

    @Value("${spring.datasource.queryTimeout}")
    private String queryTimeout;

    @Value("${spring.datasource.initialSize}")
    private Integer initialSize;

    @Value("${spring.datasource.maxActive}")
    private Integer maxActive;

    @Value("${spring.datasource.communicator.url}")
    private String communicatorURL;

    @Value("${spring.datasource.communicator.username}")
    private String communicatorUsername;

    @Value("${spring.datasource.communicator.password}")
    private String communicatorPassword;

    @Value("${databaseName.communicator}")
    private String databaseName_communicator;


    @Autowired
    private Environment env;

    private String setWindowsAuthenticationIfRequired(String url) {
        // if now username is not provided then login with windows authentication .... will not work on Jenkins Azure VM slave!
        if ((username == null || username.isEmpty()) && !url.contains("integratedSecurity")) {
            return url.charAt(url.length()-1) == ';' ? url + "integratedSecurity=true;" : url + ";integratedSecurity=true";
        }
        return url;
    }

    private DataSource getDataSource(String url) {

        // manually download and add sqljdbc_auth.dll to c:\Windows\System32 to allow windows authentication login to SQL Server

        url = setWindowsAuthenticationIfRequired(url);
        logger.debug("creating local datasource. driver [" + driver + "], url [" + url + "], username [" + username + "], password [" + password + "]");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setRemoveAbandoned(true);
        dataSource.addConnectionProperty("queryTimeout", queryTimeout);
        return dataSource;
    }

    private DataSource getDataSource(String url, String uname, String pwd) {
        url = setWindowsAuthenticationIfRequired(url);
        logger.debug("creating local datasource. driver [" + driver + "], url [" + url + "], username [" + uname + "], password [" + pwd + "]");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(uname);
        dataSource.setPassword(pwd);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setRemoveAbandoned(true);
        dataSource.addConnectionProperty("queryTimeout", queryTimeout);
        return dataSource;
    }

    /**
     * static PropertySourcesPlaceholderConfigurer is required for the @Value
     * annotations to work. Must be static
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Primary
    @Bean
    public DataSource dataSource(String url) {
        url = setWindowsAuthenticationIfRequired(url);
        logger.debug("creating datasource bean. driver [" + driver + "], url [" + url + "], username [" + username + "], password [" + password + "]");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setRemoveAbandoned(true);
        dataSource.addConnectionProperty("queryTimeout", queryTimeout);
        return dataSource;

    }

    @Bean(name = "jdbc_serviceChannel")
    public NamedParameterJdbcTemplate jdbc_serviceChannel() {
        logger.debug("jdbc_serviceChannel ...");
        return new NamedParameterJdbcTemplate(getDataSource(urlServiceChannel + "databaseName=" + databaseName_serviceChannel, usernameServiceChannel, passwordServiceChannel));
    }

    @Bean(name = "jdbc_serviceChannelRecon")
    public NamedParameterJdbcTemplate jdbc_serviceChannelRecon() {
        logger.debug("jdbc_serviceChannelRecon ...");
        return new NamedParameterJdbcTemplate(getDataSource(urlServiceChannel + "databaseName=" + databaseName_serviceChannelRecon, usernameServiceChannel, passwordServiceChannel));
    }

    @Bean(name = "jdbc_testCity")
    public NamedParameterJdbcTemplate jdbc_testCity() {
        logger.debug("jdbc_testCity ...");
        return new NamedParameterJdbcTemplate(getDataSource(urlTestCity + "databaseName=" + databaseName_testCity, usernameTestCity, passwordTestCity));
    }

    @Bean(name = "jdbc_helpdesk")
    public NamedParameterJdbcTemplate jdbc_helpdesk() {
        logger.debug("jdbc_helpdesk ...");
        return new NamedParameterJdbcTemplate(getDataSource(url + "databaseName=" + databaseName_helpdesk));
    }

    @Bean(name = "jdbc_portal")
    public NamedParameterJdbcTemplate jdbc_portal() {
        logger.debug("jdbc_portal ...");
        return new NamedParameterJdbcTemplate(getDataSource(url + "databaseName=" + databaseName_portal));
    }

    @Bean(name = "jdbc_test")
    public NamedParameterJdbcTemplate jdbc_test() {
        logger.debug("jdbc_test ...");
        String testUrl = urlTestDb == null ? url : urlTestDb;
        String uname = testUsername == null ? username : testUsername;
        String passwd = testPassword == null ? password : testPassword;
        return new NamedParameterJdbcTemplate(getDataSource(testUrl + "databaseName=" + databaseName_test, uname, passwd));
    }

    @Bean(name = "jdbc_communicator")
    public NamedParameterJdbcTemplate jdbc_communicator() {
        logger.debug("jdbc_communicator ...");
        return new NamedParameterJdbcTemplate(getDataSource(communicatorURL + "databaseName=" + databaseName_communicator, communicatorUsername, communicatorPassword));
    }

    @Bean(name = "jdbc_serviceChannel_name")
    public String jdbc_serviceChannel_name() {
        return databaseName_serviceChannel;
    }

    @Bean(name = "jdbc_serviceChannelRecon_name")
    public String jdbc_serviceChannelRecon_name() {
        return databaseName_serviceChannelRecon;
    }

    @Bean(name = "jdbc_testCity_name")
    public String jdbc_testCity_name() {
        return databaseName_testCity;
    }

    @Bean(name = "jdbc_helpdesk_name")
    public String jdbc_helpdesk_name() {
        return databaseName_helpdesk;
    }

    @Bean(name = "jdbc_portal_name")
    public String jdbc_portal_name() {
        return databaseName_portal;
    }

    @Bean(name = "jdbc_test_name")
    public String jdbc_test_name() {
        return databaseName_test;
    }

    @Bean(name = "jdbc_serviceChannel_schema_name")
    public String jdbc_serviceChannel_schema_name() {
        return schemaName_serviceChannel;
    }

    @Bean(name = "jdbc_helpdesk_schema_name")
    public String jdbc_helpdesk_schema_name() {
        return schemaName_helpdesk;
    }

    @Bean(name = "jdbc_portal_schema_name")
    public String jdbc_portal_schema_name() {
        return schemaName_portal;
    }

    @Bean(name = "jdbc_test_schema_name")
    public String jdbc_test_schema_name() {
        return schemaName_test;
    }

    @Bean(name = "jdbc_communicator_name")
    public String jdbc_communicator_name() {
        return databaseName_communicator;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.SQL_SERVER);
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(Boolean.TRUE);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPackagesToScan("mercury.database.models");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setDataSource(dataSource((url + "databaseName=" + databaseName_helpdesk)));

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        jpaProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        jpaProperties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        jpaProperties.put("hibernate.use_sql_comments", env.getProperty("hibernate.use_sql_comments"));
        jpaProperties.put("hibernate.connection.isolation", env.getProperty("hibernate.connection.isolation"));
        jpaProperties.put("hibernate.connection.autoReconnect", env.getProperty("hibernate.connection.autoReconnect"));
        jpaProperties.put("hibernate.connection.autoReconnectForPools", env.getProperty("hibernate.connection.autoReconnectForPools"));

        factory.setJpaProperties(jpaProperties);
        return factory;
    }

    @Primary
    @Bean(name = "entityManager")
    public EntityManager entityManager() {
        return entityManagerFactoryBean().getObject().createEntityManager();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactoryBean().getObject());
    }

    @Bean(name = "helpdeskDataSource")
    public DataSource helpdeskDataSource() {
        return getDataSource(url + "databaseName=" + databaseName_helpdesk);
    }
}
