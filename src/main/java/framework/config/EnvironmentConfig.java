package framework.config;

import framework.utils.exceptions.AutomationException;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   -----------------------------------------------------------------------
   Resolves configuration with precedence: OS env var > -D system property
   > config/<env>.properties > config/common.properties. The active
   environment is selected via -Dapp.env=<dev|staging|prod> (default "dev").
   Secrets (tokens, passwords) are never read from a properties file -
   they must be supplied as an env var or -D system property.
   ----------------------------------------------------------------------- */
public final class EnvironmentConfig {

    private static final String DEFAULT_ENV = "dev";
    private static final EnvironmentConfig INSTANCE = new EnvironmentConfig();

    private final Properties properties = new Properties();

    private EnvironmentConfig() {
        loadFromClasspath("config/common.properties");
        loadFromClasspath("config/" + activeEnvironment() + ".properties");
    }

    public static EnvironmentConfig init() {
        return INSTANCE;
    }

    public static String activeEnvironment() {
        return resolveRaw("app.env", null, DEFAULT_ENV);
    }

    private void loadFromClasspath(String classpathResource) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(classpathResource)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config resource: " + classpathResource, e);
        }
    }

    public String getProperty(String key) throws AutomationException {
        String value = resolveRaw(key, properties.getProperty(key), null);
        if (value == null) {
            throw new AutomationException(MessageFormat.format(
                    "Missing required config property \"{0}\" - set it in config/{1}.properties, " +
                            "or via -D{0}, or via env var {2}.",
                    key, activeEnvironment(), toEnvVarName(key)));
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        return resolveRaw(key, properties.getProperty(key), defaultValue);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }

    public int getIntProperty(String key, int defaultValue) {
        return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
    }

    /** env var (SCREAMING_SNAKE_CASE) > -D system property (dotted) > properties file > fallback */
    private static String resolveRaw(String key, String fileValue, String fallback) {
        String fromEnv = System.getenv(toEnvVarName(key));
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv;
        }

        String fromSystemProperty = System.getProperty(key);
        if (fromSystemProperty != null && !fromSystemProperty.isEmpty()) {
            return fromSystemProperty;
        }

        if (fileValue != null && !fileValue.isEmpty()) {
            return fileValue;
        }

        return fallback;
    }

    private static String toEnvVarName(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_').replace('-', '_');
    }

}
