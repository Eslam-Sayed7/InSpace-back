import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceCheckConfig {

    @Bean
    CommandLineRunner printDataSource(DataSource dataSource) {
        return args -> {
            System.out.println(">>> DataSource = " + dataSource);
            System.out.println(">>> URL = " + dataSource.getConnection().getMetaData().getURL());
            System.out.println(">>> Driver = " + dataSource.getConnection().getMetaData().getDriverName());
            System.out.println(">>> User = " + dataSource.getConnection().getMetaData().getUserName());
        };
    }
}
