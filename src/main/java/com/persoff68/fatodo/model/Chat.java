package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "ftd_chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Chat extends AbstractAuditingModel {

    private String title;

    private boolean isDirect;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Member> members;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Message> messages;

    public Chat(boolean isDirect) {
        super();
        this.isDirect = isDirect;
    }

}
