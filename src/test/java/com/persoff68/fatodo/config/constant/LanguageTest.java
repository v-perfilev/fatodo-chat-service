package com.persoff68.fatodo.config.constant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageTest {

    @Test
    void testEnum() {
        assertThat(Language.DEFAULT.getValue()).isEqualTo("en");
        assertThat(Language.EN.getValue()).isEqualTo("en");
        assertThat(Language.RU.getValue()).isEqualTo("ru");
    }

    @Test
    void testContains() {
        boolean isTrue = Language.contains("en");
        assertThat(isTrue).isTrue();
        boolean isFalse = Language.contains("mars");
        assertThat(isFalse).isFalse();
    }

}
