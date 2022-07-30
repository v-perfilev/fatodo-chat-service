package com.persoff68.fatodo.model;

import lombok.Data;

import java.util.List;
import java.util.function.Function;

@Data
public class PageableList<T> {
    private List<T> data;
    private long count;

    public <Z> PageableList<Z> convert(Function<T, Z> converterFunc) {
        List<Z> convertedList = this.data.stream()
                .map(converterFunc)
                .toList();
        return PageableList.of(convertedList, count);
    }

    public static <T> PageableList<T> of(List<T> data, long count) {
        PageableList<T> pageableList = new PageableList<>();
        pageableList.setData(data);
        pageableList.setCount(count);
        return pageableList;
    }

}
