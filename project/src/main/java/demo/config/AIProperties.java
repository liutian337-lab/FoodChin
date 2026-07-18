package demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public class AIProperties {
    private String url;
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
