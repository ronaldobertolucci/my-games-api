package io.github.ronaldobertolucci.mygames.model.mygame;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyGameFilter {
    private String username;
    private String title;
    private Long sourceId;
    private Long platformId;
}