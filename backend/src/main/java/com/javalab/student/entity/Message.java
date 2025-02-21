package com.javalab.student.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javalab.student.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 메시지 엔티티
 */
@Entity
@Getter @Setter
@Table(name = "message")
@Builder
@NoArgsConstructor  // @Builder 사용 시 @AllArgsConstructor 필수
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id")
    private Long id;

    // 발신자 (ManyToOne 관계)
    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩 설정
    @JoinColumn(name = "sender_id", nullable = false)  // FK 설정
    private Member sender;

    // 수신자 (ManyToOne 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @NotBlank(message = "메시지 내용은 비워둘 수 없습니다.")
    @Size(min = 1, max = 255, message = "메시지 내용은 1자 이상 255자 이하여야 합니다.")
    @Column(nullable = false)
    private String content;

    // 메시지를 읽었는지 여부
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    // 메시지를 발신자가 삭제했는지 여부
    @Column(name = "deleted_by_sender", nullable = false)
    private boolean deletedBySender = false;

    // 메시지를 수신자가 삭제했는지 여부
    @Column(name = "deleted_by_receiver", nullable = false)
    private boolean deletedByReceiver = false;

    // 메시지가 편집되었는지 여부
    @Column(name = "edited", nullable = false)
    private boolean edited = false;

    // 편집된 메시지 내용
    @Column(name = "edited_content")
    private String editedContent;

//    public Message() {}
//
//    // 생성자 추가
//    public Message(Member sender, Member receiver, String content, boolean read) {
//        this.sender = sender;
//        this.receiver = receiver;
//        this.content = content;
//        this.read = read;
//    }
}
