package io.nuls.api.server.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ReportDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void balance() {
        String deleteTempSQL = "delete from balance_top_temp";
        String executeSQL = "INSERT INTO balance_top_temp( address, balance, tx_count) select o.address, sum(o.value), (select count(*) from tx_account_relation tx where tx.address = o.address) from utxo_output o where o.status = 0 group by o.address";
        String deteleSQL = "delete from balance_top";
        String insertSQL = "insert into balance_top(address, balance, tx_count, create_time) select address, balance, tx_count, ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) from balance_top_temp";
        jdbcTemplate.batchUpdate(deleteTempSQL, executeSQL);
        jdbcTemplate.batchUpdate(deteleSQL, insertSQL, deleteTempSQL);
    }


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void mined() {
        String deleteTempSQL = "delete from mined_top_temp";
        String executeSQL = "INSERT INTO mined_top( consensus_address, mined_count, reward, last_height) select v.consensus_address, count(*), sum(v.blockReward), max(v.height) from ( select b.consensus_address, b.height, (select sum(uo.value) from transaction t,utxo_output uo where t.hash=uo.tx_hash and t.type=1 and t.block_height=b.height) as blockReward from block_header b group by b.consensus_address,b.height ) v group by v.consensus_address ";
        String deteleSQL = "delete from mined_top";
        String insertSQL = "insert into mined_top(consensus_address, mined_count, reward, last_height, create_time) select consensus_address, mined_count, reward, last_height, ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) from mined_top_temp";
        jdbcTemplate.batchUpdate(deleteTempSQL, executeSQL);
        jdbcTemplate.batchUpdate(deteleSQL, insertSQL, deleteTempSQL);
    }


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void txHistory() {
        String deleteSQL = "delete from tx_history where tx_date = CONCAT(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY + 0)";
        String executeSQL = "INSERT INTO tx_history(tx_date, tx_count, create_time) select CONCAT(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY + 0), ifnull(sum(b.tx_count),0), ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) from block_header b where b.create_time >= UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY) * 1000 and b.create_time < UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE)) * 1000;";
        jdbcTemplate.batchUpdate(deleteSQL, executeSQL);
    }
}
