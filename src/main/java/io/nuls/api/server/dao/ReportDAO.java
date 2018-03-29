package io.nuls.api.server.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ReportDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void balance() {
        String deleteTempSQL = "DELETE FROM balance_top_temp";
        String executeSQL = "INSERT INTO balance_top_temp( ADDRESS, BALANCE, TX_COUNT) SELECT O.ADDRESS, SUM(O.VALUE), (SELECT COUNT(*) FROM tx_account_relation TX WHERE TX.ADDRESS = O.ADDRESS) FROM utxo_output O WHERE O.STATUS = 0 GROUP BY O.ADDRESS";
        String deteleSQL = "DELETE FROM balance_top";
        String insertSQL = "INSERT INTO balance_top(ADDRESS, BALANCE, TX_COUNT, CREATE_TIME) SELECT ADDRESS, BALANCE, TX_COUNT, ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) FROM balance_top_temp ORDER BY BALANCE DESC ";
        jdbcTemplate.batchUpdate(deleteTempSQL, executeSQL);
        jdbcTemplate.batchUpdate(deteleSQL, insertSQL, deleteTempSQL);
    }


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void mined() {
        String deleteTempSQL = "DELETE FROM mined_top_temp";
        String executeSQL = "INSERT INTO mined_top_temp( CONSENSUS_ADDRESS, MINED_COUNT, REWARD, LAST_HEIGHT) SELECT V.CONSENSUS_ADDRESS, COUNT(*), SUM(V.BLOCKREWARD), MAX(V.HEIGHT) FROM ( SELECT B.CONSENSUS_ADDRESS, B.HEIGHT, (SELECT IFNULL(SUM(UO.VALUE), 0) FROM transaction T,utxo_output UO WHERE T.HASH=UO.TX_HASH AND T.TYPE=1 AND T.BLOCK_HEIGHT=B.HEIGHT) AS BLOCKREWARD FROM block_header B GROUP BY B.CONSENSUS_ADDRESS,B.HEIGHT ) V GROUP BY V.CONSENSUS_ADDRESS ";
        String deteleSQL = "DELETE FROM mined_top";
        String insertSQL = "INSERT INTO mined_top(CONSENSUS_ADDRESS, MINED_COUNT, REWARD, LAST_HEIGHT, CREATE_TIME) SELECT CONSENSUS_ADDRESS, MINED_COUNT, REWARD, LAST_HEIGHT, ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) FROM mined_top_temp ORDER BY MINED_COUNT DESC ";
        jdbcTemplate.batchUpdate(deleteTempSQL, executeSQL);
        jdbcTemplate.batchUpdate(deteleSQL, insertSQL, deleteTempSQL);
    }


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void txHistory() {
        String deleteSQL = "DELETE FROM tx_history WHERE TX_DATE = CONCAT(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY + 0)";
        String executeSQL = "INSERT INTO tx_history(TX_DATE, TX_COUNT, CREATE_TIME) SELECT CONCAT(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY + 0), IFNULL(SUM(B.TX_COUNT),0), ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) FROM block_header B WHERE B.CREATE_TIME >= UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY) * 1000 AND B.CREATE_TIME < UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE)) * 1000;";
        jdbcTemplate.batchUpdate(deleteSQL, executeSQL);
    }
}
