package com.github.damiano1996.intellijplugin.incoder.llm.server.container;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContainerState {
    @NonNull private Status status;
    private String exitCode;
    private String errorMessage;
    private Long createdAt;
    private Long startedAt;
    private Long finishedAt;

    public enum Status {
        CREATED,
        RUNNING,
        PAUSED,
        STOPPED,
        RESTARTING,
        DEAD
    }
}
