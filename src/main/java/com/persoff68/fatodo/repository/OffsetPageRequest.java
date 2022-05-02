package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Data
public class OffsetPageRequest implements Pageable, Serializable {
    protected static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private int offset;
    private int page;
    private int size;
    private Sort sort = Sort.unsorted();

    protected OffsetPageRequest(int offset, int page, int size) {
        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        if (page < 0) {
            throw new IllegalArgumentException();
        }

        if (size < 1) {
            throw new IllegalArgumentException();
        }

        this.offset = offset;
        this.page = page;
        this.size = size;
    }

    public static OffsetPageRequest of(int offset, int page, int size) {
        return new OffsetPageRequest(offset, page, size);
    }

    public static OffsetPageRequest of(int offset, int size) {
        return new OffsetPageRequest(offset, 0, size);
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return offset + (long) page * (long) size;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return of(offset, page + 1, size);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? of(offset, page - 1, size) : first();
    }

    @Override
    public Pageable first() {
        return of(offset, 0, size);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return of(offset, pageNumber, size);
    }

    public boolean hasPrevious() {
        return page > 0;
    }

}
