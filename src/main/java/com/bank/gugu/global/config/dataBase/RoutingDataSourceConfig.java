package com.bank.gugu.global.config.dataBase;

import com.bank.gugu.global.config.constant.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableJpaRepositories(
        basePackages = {
                "com.bank.gugu.domain.assets.repository",
                "com.bank.gugu.domain.assetsDetail.repository",
                "com.bank.gugu.domain.category.repository",
                "com.bank.gugu.domain.icon.repository",
                "com.bank.gugu.domain.record.repository",
                "com.bank.gugu.domain.recordsFavorite.repository",
                "com.bank.gugu.domain.recordsImage.repository",
                "com.bank.gugu.domain.user.repository"
        },
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@Configuration
public class RoutingDataSourceConfig {

    private static final String ROUTING_DATA_SOURCE = "routingDataSource";
    private static final String MASTER_DATA_SOURCE = "masterDataSource";
    private static final String SLAVE_DATA_SOURCE = "slaveDataSource";
    private static final String DATA_SOURCE = "dataSource";
    private static final String ENTITY_PACKAGE = "com.bank.gugu.entity";
    private static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
    private static final String TRANSACTION_MANAGER = "transactionManager";

    /**
     * MASTER와 SLAVE 데이터 소스를 설정하고, 읽기/쓰기 옵션에 따라 적절한 데이터 소스를 동적으로 선택하는 {@link RoutingDataSource}를 생성
     *
     * @param masterDataSource MASTER 데이터 소스
     * @param slaveDataSource  SLAVE 데이터 소스
     * @return {@link DataSource} 타입의 RoutingDataSource Bean
     */
    @Bean(ROUTING_DATA_SOURCE)
    public DataSource routingDataSource(
            @Qualifier(MASTER_DATA_SOURCE) final DataSource masterDataSource,
            @Qualifier(SLAVE_DATA_SOURCE) final DataSource slaveDataSource
    ) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.MASTER, masterDataSource);
        dataSourceMap.put(DataSourceType.SLAVE, slaveDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    /**
     * {@link LazyConnectionDataSourceProxy}를 활용하여 데이터 소스를 지연 연결 방식으로 사용하도록 설정
     *
     * @param routingDataSource RoutingDataSource Bean
     * @return {@link DataSource} 타입의 LazyConnectionDataSourceProxy Bean
     */
    @Bean(DATA_SOURCE)
    public DataSource dataSource(
            @Qualifier(ROUTING_DATA_SOURCE) DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    /**
     * JPA EntityManagerFactory를 구성
     *
     * @param dataSource 데이터 소스 @return
     * @return {@link LocalContainerEntityManagerFactoryBean} 타입의 EntityManagerFactory Bean
     */
    @Bean(ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier(DATA_SOURCE) DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan(ENTITY_PACKAGE);
        entityManagerFactory.setJpaVendorAdapter(this.jpaVendorAdapter());
        entityManagerFactory.setPersistenceUnitName("entityManager");
        return entityManagerFactory;
    }

    /**
     * Hibernate를 사용하는 JPA Vendor Adapter를 생성
     *
     * @return {@link JpaVendorAdapter} 타입의 HibernateJpaVendorAdapter Bean
     */
    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        return hibernateJpaVendorAdapter;
    }

    /**
     * JPA 트랜잭션 관리자를 설정
     *
     * @param entityManagerFactory EntityManagerFactory Bean
     * @return {@link PlatformTransactionManager} 타입의 JpaTransactionManager Bean
     */
    @Bean(TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return jpaTransactionManager;
    }

}
