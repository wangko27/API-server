package io.nuls.api.server.dto;

import io.nuls.api.entity.BlockHeader;

/**
 * Description:
 * Author: zsj
 * Date:  2018/6/7 0007
 */
public class BlockHeaderDto extends BlockHeader {
    private Long confirmCount = 0L;
    private BlockHeader blockHeader;

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public Long getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(Long confirmCount) {
        this.confirmCount = confirmCount;
    }

    public BlockHeaderDto(){

    }
    public BlockHeaderDto(BlockHeader blockHeader){
        this.blockHeader = blockHeader;
    }
}
