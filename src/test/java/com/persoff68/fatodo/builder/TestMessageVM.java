package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.web.rest.vm.MessageVM;
import lombok.Builder;

import java.util.UUID;

public class TestMessageVM extends MessageVM {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestMessageVM(String text, UUID forwardedMessageId) {
        super();
        super.setText(text);
        super.setForwardedMessageId(forwardedMessageId);
    }

    public static TestMessageVM.TestMessageVMBuilder defaultBuilder() {
        return TestMessageVM.builder().text(DEFAULT_VALUE);
    }

    public MessageVM toParent() {
        MessageVM vm = new MessageVM();
        vm.setText(getText());
        vm.setForwardedMessageId(getForwardedMessageId());
        return vm;
    }

}
