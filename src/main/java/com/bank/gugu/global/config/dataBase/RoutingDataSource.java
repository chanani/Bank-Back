package com.bank.gugu.global.config.dataBase;

import com.bank.gugu.global.config.constant.DataSourceType;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 트랜잭션이 읽기 전용인지 확인하여 MASTER 또는 SLAVE DataSource를 반환
 *
 * @return DataSourceType.SLAVE (읽기 전용) 또는 DataSourceType.MASTER (쓰기 가능)
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? DataSourceType.SLAVE : DataSourceType.MASTER;
    }
}
