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
        String deleteTempSQL = "DELETE FROM BALANCE_TOP_TEMP";
        String executeSQL = "INSERT INTO BALANCE_TOP_TEMP( ADDRESS, BALANCE, TX_COUNT) SELECT O.ADDRESS, SUM(O.VALUE), (SELECT COUNT(*) FROM TX_ACCOUNT_RELATION TX WHERE TX.ADDRESS = O.ADDRESS) FROM UTXO_OUTPUT O WHERE O.STATUS = 0 GROUP BY O.ADDRESS";
        String deteleSQL = "DELETE FROM BALANCE_TOP";
        String insertSQL = "INSERT INTO BALANCE_TOP(ADDRESS, BALANCE, TX_COUNT, CREATE_TIME) SELECT ADDRESS, BALANCE, TX_COUNT, ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) FROM BALANCE_TOP_TEMP ORDER BY BALANCE DESC ";
        jdbcTemplate.batchUpdate(deleteTempSQL, executeSQL);
        jdbcTemplate.batchUpdate(deteleSQL, insertSQL, deleteTempSQL);
    }


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void mined() {
        String deleteTempSQL = "DELETE FROM MINED_TOP_TEMP";
        String executeSQL = "INSERT INTO MINED_TOP( CONSENSUS_ADDRESS, MINED_COUNT, REWARD, LAST_HEIGHT) SELECT V.CONSENSUS_ADDRESS, COUNT(*), SUM(V.BLOCKREWARD), MAX(V.HEIGHT) FROM ( SELECT B.CONSENSUS_ADDRESS, B.HEIGHT, (SELECT IFNULL(SUM(UO.VALUE), 0) FROM TRANSACTION T,UTXO_OUTPUT UO WHERE T.HASH=UO.TX_HASH AND T.TYPE=1 AND T.BLOCK_HEIGHT=B.HEIGHT) AS BLOCKREWARD FROM BLOCK_HEADER B GROUP BY B.CONSENSUS_ADDRESS,B.HEIGHT ) V GROUP BY V.CONSENSUS_ADDRESS ";
        String deteleSQL = "DELETE FROM MINED_TOP";
        String insertSQL = "INSERT INTO MINED_TOP(CONSENSUS_ADDRESS, MINED_COUNT, REWARD, LAST_HEIGHT, CREATE_TIME) SELECT CONSENSUS_ADDRESS, MINED_COUNT, REWARD, LAST_HEIGHT, ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) FROM MINED_TOP_TEMP ORDER BY MINED_COUNT DESC ";
        jdbcTemplate.batchUpdate(deleteTempSQL, executeSQL);
        jdbcTemplate.batchUpdate(deteleSQL, insertSQL, deleteTempSQL);
    }


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void txHistory() {
        String deleteSQL = "DELETE FROM TX_HISTORY WHERE TX_DATE = CONCAT(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY + 0)";
        String executeSQL = "INSERT INTO TX_HISTORY(TX_DATE, TX_COUNT, CREATE_TIME) SELECT CONCAT(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY + 0), IFNULL(SUM(B.TX_COUNT),0), ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) FROM BLOCK_HEADER B WHERE B.CREATE_TIME >= UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY) * 1000 AND B.CREATE_TIME < UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE)) * 1000;";
        jdbcTemplate.batchUpdate(deleteSQL, executeSQL);
    }
}
