package com.github.damiano1996.intellijplugin.incoder.llm.server.container;

import java.util.List;
import java.util.Map;
import lombok.*;
import org.jetbrains.annotations.Nullable;

@With
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Container {
    @NonNull private Image image;
    @Nullable private String id;
    @Nullable private String name;
    @NonNull private Integer hostPort;
    @NonNull private Integer port;
    @NonNull private List<Bind> binds;
    @NonNull private List<Device> devices;
    @NonNull private String runtime;
    @NonNull private Map<String, String> envVariables;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Image {
        @NonNull private String name;
        @NonNull private String version;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bind {
        @NonNull private String path;
        @NonNull private String volume;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Device {
        @NonNull private String cGroupPermissions;
        @NonNull private String pathInContainer;
        @NonNull private String pathOnHost;
    }
}
