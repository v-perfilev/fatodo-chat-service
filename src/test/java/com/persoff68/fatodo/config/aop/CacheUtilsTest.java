package com.persoff68.fatodo.config.aop;

import com.persoff68.fatodo.config.aop.cache.util.CacheUtils;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CacheUtilsTest {

    @Test
    void testGetValue_success() {
        String[] names = {"one", "two"};
        Object[] args = {"test", "test2"};
        Object result = CacheUtils.getValue(names, args, "#two");
        assertThat(result).isEqualTo("test2");
    }

    @Test
    void testGetValue_argsNotValid() {
        String[] names = {"one", "two"};
        Object[] args = {"test"};
        assertThatThrownBy(() -> CacheUtils.getValue(names, args, "#two"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void testGetValue_keyNotFound() {
        String[] names = {"one", "two"};
        Object[] args = {"test", "test2"};
        assertThatThrownBy(() -> CacheUtils.getValue(names, args, "ewq-qweq-qweq"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void testGetCollectionValue_success() {
        String[] names = {"one", "two"};
        Object[] args = {"test", testObject()};
        Object result = CacheUtils.getCollectionValue(names, args, "#two.list.string");
        assertThat(result).isEqualTo(resultObject());
    }

    @Test
    void testGetCollectionValue_argsNotValid() {
        String[] names = {"one", "two"};
        Object[] args = {"test"};
        assertThatThrownBy(() -> CacheUtils.getCollectionValue(names, args, "#two.list.string"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void testGetCollectionValue_keyNotFound() {
        String[] names = {"one", "two"};
        Object[] args = {"test", testObject()};
        assertThatThrownBy(() -> CacheUtils.getCollectionValue(names, args, "#three.list.string"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void testGetCollectionValue_keyNotValid() {
        String[] names = {"one", "two"};
        Object[] args = {"test", testObject()};
        assertThatThrownBy(() -> CacheUtils.getCollectionValue(names, args, "qweq-qweqwe-qweq"))
                .isInstanceOf(Exception.class);
    }

    private Object testObject() {
        TestObject testObject2 = new TestObject();
        testObject2.setString("test_2");
        testObject2.setList(List.of("test_3", "test_3"));
        TestObject testObject1 = new TestObject();
        testObject1.setString("test_1");
        testObject1.setList(List.of(testObject2, testObject2));
        return testObject1;
    }

    private List<Object> resultObject() {
        return List.of("test_2", "test_2");
    }

    @Data
    private static class TestObject {
        private String string;
        private List<Object> list;
    }
}
