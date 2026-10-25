package com.example.onlineshop.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BannerUpdateDTO {
    private String title;
    private String mediaUrl;
    private String linkUrl;
    private Integer displayOrder;
    private Boolean active;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}
