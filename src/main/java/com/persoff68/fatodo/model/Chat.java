package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ftd_chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Chat extends AbstractAuditingModel {

    private String title;

    private boolean isDirect = false;

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "chat")
    private List<MemberEvent> memberEvents = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "chat")
    private List<Message> messages = new ArrayList<>();


    public Chat(boolean isDirect) {
        this.isDirect = isDirect;
    }

}
