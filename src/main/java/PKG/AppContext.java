package PKG;

import PKG.addons.Actions;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan(basePackages = {"PKG"})
//@PropertySource(value = {"file:cfg/pkg.properties"})
@PropertySource(value = {"classpath:pkg.properties"})
//@EnableAsync
//@EnableScheduling
//@EnableTransactionManagement
//@EnableLoadTimeWeaving
//@EnableCaching
//@EnableWebMvc
//@Import(Ch4Configuration.class)
//@ImportResource("classpath:/beans-tx.xml")
//@DependsOn(value = {"actions"})
public class AppContext {

    @Value("${db.driver}")
    private String dbDriver;
    //@Value("${db.url}")
    private String dbUrl;
    //@Value("${db.username}")
    private String dbUsername;
    //@Value("${db.password}")
    private String dbPassword;

    @Value("classpath:com/foo/sql/db-schema.sql")
    private Resource schemaScript;

    @Value("classpath:com/foo/sql/db-test-data.sql")
    private Resource dataScript;
    
    public static Connection con = null ; 

    //@Inject
    //private Actions actions;
    //@Value("${hash}")
    //private String HashTip; 
    @PostConstruct
    public void checkValue() {
        if (!Actions.userCheck()) {
            System.exit(0);
        }
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /*@Bean(destroyMethod = "close")
    public DataSource dataSource() {
        BasicDataSource dsNodes = new BasicDataSource();
        dsNodes.setDriverClassName(dbDriver);
        dbUrl = "jdbc:derby:./db/" + Actions.login + ";create=true;dataEncryption=true;bootPassword=hohners1974" + Actions.passw;
        dbPassword = Actions.passw;
        dbUsername = Actions.login;
        dsNodes.setUrl(dbUrl);
        dsNodes.setPassword(dbPassword);
        dsNodes.setUsername(dbUsername);
        return dsNodes;
    }*/
    
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariDataSource dsNodes = new HikariDataSource();
        dsNodes.setDriverClassName(dbDriver);
        //dbUrl = "jdbc:derby:./db/" + Actions.login + ";create=true;dataEncryption=true;bootPassword=hohners1974" + Actions.passw;
        dbUrl = "jdbc:h2:./db/" + Actions.login + "/" + Actions.login + ";CIPHER=AES";
        dbPassword = "h2" + Actions.login + " " + Actions.passw;
        dbUsername = Actions.login;
        dsNodes.setJdbcUrl(dbUrl);
        dsNodes.setPassword(dbPassword);
        dsNodes.setUsername(dbUsername);
        return dsNodes;
    } 
    
    /*@Bean
    public JdbcTemplate jdbcTemplate () {
        return new JdbcTemplate(this.dataSource());
    }*/
    
    /*@Bean
    //@DependsOn("ds")
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        //String ddl1 = "CREATE SCHEMA HARP07";
        String ddl1 = "DROP TABLE hosts";
        String ddl2 = "CREATE TABLE hosts(id int not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), title varchar(255), login varchar(255), passw varchar(255), descr varchar(255), url varchar(255))";
        DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource);
        dsi.setDatabasePopulator(new DatabasePopulator() {
            @Override
            public void populate(Connection conn) throws SQLException,
                    ScriptException {
                Statement st = conn.createStatement();
                st.execute(ddl1);
                st.execute(ddl2);
                st.close();
            }
        });
        dsi.setDatabasePopulator(new DatabasePopulator() {
            @Override
            public void populate(Connection conn) throws SQLException,
                    ScriptException {
                Statement st = conn.createStatement();
                st.execute(ddl2);
                st.close();
            }
        }); 
        dsi.setEnabled(true);
        dsi.afterPropertiesSet();
        return dsi;
    }*/

    /*@Bean
    @DependsOn("ds")
    public DataSourceInitializer dataSourceInitializer() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        //ResourceDatabasePopulator databaseCleaner = new ResourceDatabasePopulator();
        File file = new File("db/" + Actions.login);
        //if (!file.exists()) {
        //databasePopulator.addScript(new ClassPathResource("/PKG/sql/create_schema.sql"));
        databasePopulator.addScript(new ClassPathResource("/PKG/sql/create_table.sql"));
        //databasePopulator.execute(ds());
        //databaseCleaner.addScript(new ClassPathResource("/PKG/sql/drop_table.sql"));
        //}
        //databaseCleaner.setIgnoreFailedDrops(true);
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(ds());
        initializer.setDatabasePopulator(databasePopulator);
        //initializer.setDatabaseCleaner(databaseCleaner);
        initializer.setEnabled(true);
        return initializer;
    }*/

 /*@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer pspc
                = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[]{new ClassPathResource("hofat.properties")};
        pspc.setLocations(resources);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        return pspc;
    }  */
    
}
