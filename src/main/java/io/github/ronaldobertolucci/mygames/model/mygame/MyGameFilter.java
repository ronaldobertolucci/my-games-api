package io.github.ronaldobertolucci.mygames.model.mygame;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyGameFilter {
    private String username;
    private String title;
    private Long sourceId;
    private Long platformId;
    private List<Status> statuses;
}