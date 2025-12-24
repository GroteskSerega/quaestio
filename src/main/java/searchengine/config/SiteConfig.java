package searchengine.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SiteConfig {
    private String url;
    private String name;

    @Override
    public String toString() {
        return "Site{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
