package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String filePath;

    private long fileSize;

    private String mediaType;

    @Lob
    @Type(type = "binary")
    private byte[] data;

}
