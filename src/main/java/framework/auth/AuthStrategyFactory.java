package framework.auth;

import framework.config.EnvironmentConfig;
import framework.utils.exceptions.AutomationException;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public final class AuthStrategyFactory {

    private AuthStrategyFactory() {
    }

    public static AuthStrategy forService(String serviceKey) throws AutomationException {
        EnvironmentConfig config = EnvironmentConfig.init();
        String authType = config.getProperty("service." + serviceKey + ".authType", "none");

        switch (authType.toLowerCase()) {
            case "none":
                return new NoAuthStrategy();

            case "apikey": {
                String headerName = config.getProperty("service." + serviceKey + ".auth.headerName", "X-Api-Key");
                String apiKey = config.getProperty("service." + serviceKey + ".auth.apiKey");
                return new ApiKeyAuthStrategy(headerName, apiKey);
            }

            case "bearer": {
                String token = config.getProperty("service." + serviceKey + ".auth.token");
                return new BearerTokenAuthStrategy(token);
            }

            case "basic": {
                String username = config.getProperty("service." + serviceKey + ".auth.username");
                String password = config.getProperty("service." + serviceKey + ".auth.password");
                return new BasicAuthStrategy(username, password);
            }

            case "oauth2": {
                String tokenUrl = config.getProperty("service." + serviceKey + ".auth.tokenUrl");
                String clientId = config.getProperty("service." + serviceKey + ".auth.clientId");
                String clientSecret = config.getProperty("service." + serviceKey + ".auth.clientSecret");
                String scope = config.getProperty("service." + serviceKey + ".auth.scope", "");
                return new OAuth2ClientCredentialsAuthStrategy(tokenUrl, clientId, clientSecret, scope);
            }

            default:
                throw new AutomationException("Unknown authType \"" + authType + "\" for service \"" + serviceKey
                        + "\". Expected one of: none, apiKey, bearer, basic, oauth2.");
        }
    }

}
