package com.marakicode.securepay.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users", schema = "securepay")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "pin")
    private String pin;

    @OneToMany(mappedBy = "sender")
    private List<TransactionParticipant> senderParticipants = new ArrayList<>();

    public void addSenderParticipant(TransactionParticipant participant) {
        senderParticipants.add(participant);
        participant.setSender(this);
    }

    public void removeSenderParticipant(TransactionParticipant participant) {
        senderParticipants.remove(participant);
        participant.setSender(null);
    }

    @OneToMany(mappedBy = "receiver")
    private List<TransactionParticipant> receiverParticipants = new ArrayList<>();

    public void addReceiverParticipant(TransactionParticipant participant) {
        receiverParticipants.add(participant);
        participant.setReceiver(this);
    }

    public void removeReceiverParticipant(TransactionParticipant participant) {
        receiverParticipants.remove(participant);
        participant.setReceiver(null);
    }

}