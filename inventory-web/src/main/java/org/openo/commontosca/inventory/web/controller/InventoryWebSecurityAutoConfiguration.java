/**
 * Copyright  2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.commontosca.inventory.web.controller;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.support.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class InventoryWebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InventoryWebSecurityAutoConfiguration.class);

  private SavedRequestAwareAuthenticationSuccessHandler handler;
  @Autowired(required = false)
  private AuthenticationProvider provider;

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    if (provider == null) {
      InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryAuthentication =
          auth.inMemoryAuthentication();
      inMemoryAuthentication.withUser("admin").password("admin").roles("ADMIN");
    } else {
      auth.authenticationProvider(provider);
    }
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    KeyBasedPersistenceTokenService tokenService = new KeyBasedPersistenceTokenService();
    tokenService.setServerSecret("inventory");
    tokenService.setSecureRandom(new SecureRandom());
    tokenService.setServerInteger(1000);
    TokenSecurityContextRepository securityContextRepository =
        new TokenSecurityContextRepository(tokenService);
    http.setSharedObject(SecurityContextRepository.class, securityContextRepository);
    http.authorizeRequests().antMatchers("/openoapi/inventory/v1/**").permitAll();
    http.authorizeRequests().anyRequest().authenticated();
    http.csrf().disable();
  }

  public static final class TokenSecurityContextRepository implements SecurityContextRepository {

    private static final String Inventory_TOKEN = "Inventory-token";
    private static final String Inventory_TOKEN_SECURITY_CONTEXT =
        "Inventory-token-security-context";
    private TokenService tokenService;

    /**
     * @param tokenService
     */
    public TokenSecurityContextRepository(TokenService tokenService) {
      super();
      this.tokenService = tokenService;
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
      SecurityContext securityContext = this.loadSecurityContextFromRequest(request);
      return securityContext != null;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
      SecurityContext securityContext =
          this.loadSecurityContextFromRequest(requestResponseHolder.getRequest());
      if (securityContext == null) {
        securityContext = SecurityContextHolder.createEmptyContext();
      }
      return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request,
        HttpServletResponse response) {
      if (response.isCommitted()
          || context.getAuthentication() instanceof AnonymousAuthenticationToken) {
        return;
      }
      ValueMap map = new ValueMap();
      map.put("session-id", request.getRequestedSessionId());
      Authentication authentication = context.getAuthentication();
      if (authentication != null) {
        map.put("user", authentication.getName());
        map.put("authority",
            AuthorityUtils.authorityListToSet(context.getAuthentication().getAuthorities()));
        String content = GsonUtils.toJson(map);
        Token token = this.tokenService.allocateToken(content);
        Cookie cookie = new Cookie(TokenSecurityContextRepository.Inventory_TOKEN, token.getKey());
        cookie.setPath(request.getContextPath());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
      } else {
        Cookie cookie = new Cookie(TokenSecurityContextRepository.Inventory_TOKEN, null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
      }
    }

    private SecurityContext loadSecurityContextFromRequest(HttpServletRequest request) {
      SecurityContext securityContext = (SecurityContext) request
          .getAttribute(TokenSecurityContextRepository.Inventory_TOKEN_SECURITY_CONTEXT);
      if (securityContext != null) {
        return securityContext;
      }
      Optional<Cookie> tokenCookie = Stream
          .of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0])).filter(cookie -> {
            return TokenSecurityContextRepository.Inventory_TOKEN.equals(cookie.getName());
          }).findFirst();
      try {
        if (tokenCookie.isPresent()) {
          securityContext = SecurityContextHolder.createEmptyContext();
          Token token = this.tokenService.verifyToken(tokenCookie.get().getValue());
          ValueMap map = GsonUtils.fromJson(token.getExtendedInformation(), ValueMap.class);
          String tokenSessionId = map.optString("session-id", "");
          if (request.getRequestedSessionId() == null
              || request.getRequestedSessionId().equals(tokenSessionId)) {
            String tokenUser = map.requireString("user");
            List<GrantedAuthority> tokenAuthority = AuthorityUtils
                .createAuthorityList(map.requireList("authority").toArray(new String[0]));
            securityContext.setAuthentication(
                new PreAuthenticatedAuthenticationToken(tokenUser, "", tokenAuthority));
            request.setAttribute(TokenSecurityContextRepository.Inventory_TOKEN_SECURITY_CONTEXT,
                securityContext);
            return securityContext;
          }
        }
      } catch (Exception ex) {
        InventoryWebSecurityAutoConfiguration.LOGGER.warn("Load security token failed", ex);
      }
      return null;
    }
  }

}
