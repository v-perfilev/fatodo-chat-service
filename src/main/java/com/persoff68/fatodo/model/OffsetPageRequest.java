package com.persoff68.fatodo.model;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class OffsetPageRequest implements Pageable {

    private long offset;
    private int limit;
    private Sort sort = Sort.unsorted();

    protected OffsetPageRequest(long offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException();
        }
        if (limit < 1) {
            throw new IllegalArgumentException();
        }
        this.offset = offset;
        this.limit = limit;
    }

    public static OffsetPageRequest of(long offset, int limit) {
        return new OffsetPageRequest(offset, limit);
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetPageRequest(getOffset() + getPageSize(), getPageSize());

    }

    public OffsetPageRequest previous() {
        return hasPrevious()
                ? new OffsetPageRequest(getOffset() - getPageSize(), getPageSize())
                : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious()
                ? previous()
                : first();
    }

    @Override
    public Pageable first() {
        return new OffsetPageRequest(0, getPageSize());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

}
