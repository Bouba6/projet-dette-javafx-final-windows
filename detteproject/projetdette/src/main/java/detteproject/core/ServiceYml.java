package detteproject.core;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class ServiceYml {

    public ServiceYml() {

    }

    public Map<String, Object> readYml(String path) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            Map<String, Object> data = yaml.load(inputStream);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or throw new RuntimeException(e);
        }
    }

}
