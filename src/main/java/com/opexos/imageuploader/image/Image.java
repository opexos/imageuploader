package com.opexos.imageuploader.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * Image entity. Data layer.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Original image must be defined")
    private byte[] original;

    @NotNull(message = "Preview image must be defined")
    private byte[] preview;

    @NotNull(message = "Upload date must be defined")
    private OffsetDateTime uploadDate;

}
