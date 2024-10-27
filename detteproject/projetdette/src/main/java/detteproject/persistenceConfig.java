package detteproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class persistenceConfig {
    private Map<String, Object> config;

    public persistenceConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            // Charger le fichier YAML
            config = mapper.readValue(new File("projetdette/src/main/resources/META-INF/config.yaml"), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getActivePersistenceUnit() {
        Map<String, String> persistence = (Map<String, String>) config.get("persistence");
        return persistence.get("activePersistenceUnit");
    }

}
