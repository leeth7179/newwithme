package com.javalab.student.entity;

import com.javalab.student.constant.Status;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto increment
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "user_id", nullable = false)  // 외래키 설정
    private Member member;

    @Column(name = "subject", length = 50, nullable = false)
    private String subject;

    @Column(name = "hospital", length = 255)
    private String hospital;

    @Column(name = "doctor_number" , length = 50, nullable = false, unique = true)
    private String doctorNumber;


}
