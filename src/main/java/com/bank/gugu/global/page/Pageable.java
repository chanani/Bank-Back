package com.bank.gugu.global.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Pageable {

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Schema(description = "한 페이지에 담길 기본 데이터 수", hidden = true)
    private final int DEFAULT_PAGE_SIZE = 10;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Schema(description="기본 블럭 개수 (blockSize: 3일 경우 예: [<][1][2][3][>])", hidden = true)
    private final int DEFAULT_BLOCK_SIZE = 5;

    @Schema(description = "현재 페이지", example = "1")
    private int page;

    @Schema(description = "총 아이템 수")
    private int totalCount;

    @Schema(description = "총 페이지 수")
    private int totalPageCount;

    @Schema(description = "현재 블럭의 시작 페이지")
    private int startPage;

    @Schema(description = "현재 블럭의 마지막 페이지")
    private int endPage;

    @Schema(description = "첫 페이지", example = "1")
    private int firstPage;

    @Schema(description = "현재 페이지의 이전 페이지 (현재 페이지가 첫 페이지인 경우 1)")
    private int prevPage;

    @Schema(description = "현재 페이지의 다음 페이지 (현재 페이지가 마지막 페이지인 경우 마지막페이지)")
    private int nextPage;

    @Schema(description = "마지막 페이지")
    private int lastPage;

    @Schema(description = "총 블럭 수")
    private int totalBlockCount;

    @Schema(description = "현재 블럭")
    private int block;

    @Schema(description = "이전 블럭")
    private int prevBlock;

    @Schema(description = "이전 블럭 여부")
    private boolean hasPrevBlock = false;

    @Schema(description = "다음 블럭")
    private int nextBlock;

    @Schema(description = "다음 블럭 여부")
    private boolean hasNextBlock = false;

    // 실제 DB 조회시 사용되는 변수
    // 해당 블럭의 시작 페이지 인덱스 offset
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Schema(hidden = true)
    private int startIndex;
    // 해당 블럭의 마지막 페이지 인덱스 limit
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Schema(hidden = true)
    private int endIndex;

    @Builder
    public Pageable(int page, int totalCount) {
        settings(page, totalCount, DEFAULT_PAGE_SIZE, DEFAULT_BLOCK_SIZE);
    }

    @Builder
    public Pageable(int page, int totalCount, int pageSize, int blockSize) {
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (blockSize <= 0) {
            blockSize = DEFAULT_BLOCK_SIZE;
        }

        settings(page, totalCount, pageSize, blockSize);
    }

    public Pageable(int page, int totalCount, int pageSize) {
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        settings(page, totalCount, pageSize, 5);
    }


    /**
     * 페이지의 시작 번호는 항상 `1` 부터 시작됩니다. 또한
     * DB 페이징 조회시 `startIndex` `endIndex` 를 활용합니다
     * */
    private void settings(int page, int totalCount, int pageSize, int blockSize) {
        this.page = page <= 0 ? 1 : page;
        this.totalCount = totalCount;

        this.totalPageCount = (int) Math.ceil(totalCount * 1.0 / pageSize);
        this.totalBlockCount = (int) Math.ceil(totalPageCount * 1.0 / blockSize);

        this.block = (int) Math.ceil((this.page * 1.0) / blockSize);

        this.startPage = (block - 1) * blockSize + 1;
        this.endPage = (startPage - 1) + blockSize;

        if (endPage >= totalPageCount) {
            this.endPage = totalPageCount;
        }

        if (totalPageCount == 0) {
            this.endPage = 1;
        }

        this.firstPage = 1;
        this.lastPage = totalPageCount;

        this.prevPage = this.page - 1;
        if (prevPage < 1) this.prevPage = 1;
        this.nextPage = this.page + 1;
        if (totalPageCount < nextPage) nextPage = totalPageCount;

        this.prevBlock = (block - 2) * blockSize + 1;
        if (prevBlock < 1) this.prevBlock = 1;
        this.hasPrevBlock = block > 1;

        this.nextBlock = block * blockSize + 1;
        if (nextBlock > totalPageCount) nextBlock = totalPageCount;
        this.hasNextBlock = totalBlockCount > block;

        this.startIndex = (this.page - 1) * pageSize;
        this.endIndex = pageSize;
    }

    @JsonIgnore
    public int getLimit() {
        return this.endIndex;
    }

    @JsonIgnore
    public int getOffset() {
        return this.startIndex;
    }
}
