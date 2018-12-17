package com.opexos.imageuploader.image;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Image entity. Data layer.
 */
@Entity
@Table(name = "image")
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @NotNull(message = "Original image must be defined")
    @Getter @Setter private byte[] original;

    @NotNull(message = "Preview image must be defined")
    @Getter @Setter private byte[] preview;

    @NotNull(message = "Upload date must be defined")
    @Getter @Setter private LocalDateTime uploadDate;

}
