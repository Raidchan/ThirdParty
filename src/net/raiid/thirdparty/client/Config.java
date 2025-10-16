package net.raiid.thirdparty.client;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class Config {

    private static final String FILE_NAME = "config.yml";
    private Map<String, Object> root;

    public static void saveDefaultConfig(Path dir) {
        try {
            Files.createDirectories(dir);
            Path file = dir.resolve(FILE_NAME);
            if (!Files.exists(file)) {
                try (InputStream in = Config.class.getResourceAsStream("/config.yml")) {
                    if (in == null) throw new FileNotFoundException("config.yml not found");
                    Files.copy(in, file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write default config", e);
        }
    }

    public Config(Path dir) {
        Path file = dir.resolve(FILE_NAME);
        try (Reader r = Files.newBufferedReader(file)) {
            this.root = new Yaml().load(r);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.yml", e);
        }
    }

    public String getString(String key) {
        Object o = this.root.get(key);
        return (String)o;
    }

    public int getInt(String key) {
        Object o = this.root.get(key);
        if (o instanceof Number) return ((Number) o).intValue();
        return Integer.parseInt((String) o);
    }

}
