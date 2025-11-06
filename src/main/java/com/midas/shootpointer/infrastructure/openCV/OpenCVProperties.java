package com.midas.shootpointer.infrastructure.openCV;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "opencv")
public class OpenCVProperties {
    private String url;
    private Api api;
    private Expire expire;
    private Redis redis;

    @Getter
    @Setter
    public static class Api{
        private Post post;
        private Get get;

        @Getter
        @Setter
        public static class Post{
            private String sendImage;
        }

        @Getter
        @Setter
        public static class Get{
            private String fetchVideo;
        }
    }

    @Getter
    @Setter
    public static class Redis{
        private String host;
        private int port;
    }

    @Getter
    @Setter
    public static class Expire{
        private long expirationTime;
    }
}
