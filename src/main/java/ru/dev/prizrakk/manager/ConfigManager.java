

package ru.dev.prizrakk.manager;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;

public class ConfigManager
{
    private Properties properties;
    private File configFile;
    
    public ConfigManager() {
        this.properties = new Properties();
        this.configFile = new File("config.properties");
        if (this.configFile.exists()) {
            try (final FileInputStream inputStream = new FileInputStream(this.configFile)) {
                this.properties.load(inputStream);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public String getProperty(final String key) {
        return this.properties.getProperty(key);
    }
    
    public void setProperty(final String key, final String value) {
        this.properties.setProperty(key, value);
    }
    
    public void saveConfig() {
        try (final FileOutputStream outputStream = new FileOutputStream(this.configFile)) {
            this.properties.store(outputStream, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
