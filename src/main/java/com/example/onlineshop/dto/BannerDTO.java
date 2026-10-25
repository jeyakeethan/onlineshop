package com.example.onlineshop.dto;

import com.example.onlineshop.model.Banner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BannerDTO {

    private Long bannerId;
    private String title;
    private String mediaUrl;
    private String linkUrl;
    private int displayOrder;
    private boolean active;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private boolean archived;

    public BannerDTO(Banner banner) {
        this.bannerId = banner.getBannerId();
        this.title = banner.getTitle();
        this.mediaUrl = banner.getMediaUrl();
        this.linkUrl = banner.getLinkUrl();
        this.displayOrder = banner.getDisplayOrder();
        this.active = banner.isActive();
        this.startsAt = banner.getStartsAt();
        this.endsAt = banner.getEndsAt();
        this.archived = banner.isArchived();
    }
}