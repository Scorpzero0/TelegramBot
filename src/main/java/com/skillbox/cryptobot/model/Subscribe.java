package com.skillbox.cryptobot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table(name = "subscribes")
public class Subscribe {

    @Column(name = "uuid")
    private UUID uuid;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "subscribe_cost")
    private BigDecimal subscribeCost = null;
}
