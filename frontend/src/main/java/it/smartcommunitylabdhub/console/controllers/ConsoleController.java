package it.smartcommunitylabdhub.console.controllers;

import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import it.smartcommunitylabdhub.console.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class ConsoleController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private SecurityProperties securityProperties;

    @Value("${solr.enabled}")
    private boolean enableSolr;

    public static final String CONSOLE_CONTEXT = Keys.CONSOLE_CONTEXT;

    @GetMapping(value = { "/", CONSOLE_CONTEXT })
    public ModelAndView root() {
        return new ModelAndView("redirect:" + CONSOLE_CONTEXT + "/");
    }

    // @GetMapping(value = { CONSOLE_CONTEXT, CONSOLE_CONTEXT + "/**" })
    @GetMapping(
        value = {
            CONSOLE_CONTEXT + "/",
            CONSOLE_CONTEXT + "/{path:^(?!\\S+(?:\\.[a-z0-9]{2,}))\\S+$}",
            CONSOLE_CONTEXT + "/-/**",
        }
    )
    public String console(Model model, HttpServletRequest request) {
        String requestUrl = ServletUriComponentsBuilder
            .fromRequestUri(request)
            .replacePath(request.getContextPath())
            .build()
            .toUriString();

        String applicationUrl = StringUtils.hasText(applicationProperties.getEndpoint())
            ? applicationProperties.getEndpoint()
            : requestUrl;

        //build config
        Map<String, String> config = new HashMap<>();
        config.put("REACT_APP_APPLICATION_URL", applicationUrl);
        config.put("REACT_APP_API_URL", "/api/v1");
        config.put("REACT_APP_CONTEXT_PATH", CONSOLE_CONTEXT);

        config.put("VITE_APP_NAME", applicationProperties.getDescription());
        config.put("REACT_APP_VERSION", applicationProperties.getVersion());

        if (securityProperties.isBasicAuthEnabled()) {
            config.put("REACT_APP_AUTH_URL", "/api");
            config.put("REACT_APP_LOGIN_URL", "/auth");
        }

        if (securityProperties.isOidcAuthEnabled()) {
            config.put("REACT_APP_AUTH_URL", "/api");
            config.put("REACT_APP_ISSUER_URI", securityProperties.getOidc().getIssuerUri());
            config.put("REACT_APP_CLIENT_ID", securityProperties.getOidc().getClientId());
            if (securityProperties.getOidc().getScope() != null) {
                config.put("REACT_APP_SCOPE", String.join(" ", securityProperties.getOidc().getScope()));
            }
        }

        config.put("REACT_APP_ENABLE_SOLR", String.valueOf(enableSolr));

        model.addAttribute("config", config);
        return "console.html";
    }

    @RequestMapping(value = "/api/auth", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> auth(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(auth.getPrincipal());
    }
}
