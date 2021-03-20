package com.ms.oracle.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Entity
@Table(name = "HELLO")
public class Hello {

    @Id
    private String greeting;

}
