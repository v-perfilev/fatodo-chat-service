package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.vm.MessageVM;
import lombok.Builder;

import java.util.UUID;

public class TestMessageVM extends MessageVM {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestMessageVM(String text, UUID referenceId) {
        super();
        super.setText(text);
        super.setReferenceId(referenceId);
    }

    public static TestMessageVM.TestMessageVMBuilder defaultBuilder() {
        return TestMessageVM.builder().text(DEFAULT_VALUE);
    }

    public MessageVM toParent() {
        MessageVM vm = new MessageVM();
        vm.setText(getText());
        vm.setReferenceId(getReferenceId());
        return vm;
    }

}
