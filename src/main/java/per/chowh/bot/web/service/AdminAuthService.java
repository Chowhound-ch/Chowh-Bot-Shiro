package per.chowh.bot.web.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import per.chowh.bot.core.permit.domain.User;
import per.chowh.bot.utils.JacksonUtil;
import per.chowh.bot.web.domain.AdminSession;
import tools.jackson.core.JacksonException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class AdminAuthService {
    public static final String SESSION_ATTRIBUTE = AdminAuthService.class.getName() + ".session";

    private final StringRedisTemplate redis;
    private final SecretKey key;
    private final JwtParser parser;
    private final Duration tokenTtl;
    private final String issuer;
    private final String sessionPrefix;

    public AdminAuthService(StringRedisTemplate redis,
                            @Value("${per.auth.jwt-secret}") String secret,
                            @Value("${per.auth.token-ttl:30m}") Duration tokenTtl,
                            @Value("${per.auth.issuer:chowh-bot-admin}") String issuer,
                            @Value("${per.auth.session-prefix:chowh:admin:session:}") String sessionPrefix) {
        this.redis = redis;
        if (secret.isBlank()) {
            throw new IllegalArgumentException("请配置 BOT_JWT_SECRET，值为至少 32 字节随机密钥的 Base64 编码");
        }
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        if (tokenTtl.compareTo(Duration.ofSeconds(1)) < 0) {
            throw new IllegalArgumentException("BOT_JWT_TOKEN_TTL 至少为 1 秒");
        }
        this.tokenTtl = tokenTtl;
        this.issuer = issuer;
        this.sessionPrefix = sessionPrefix;
        this.parser = Jwts.parser().verifyWith(key).requireIssuer(issuer).build();
    }

    public record LoginToken(String userId, String token, String tokenType, long expiresAt) {}

    public LoginToken login(User user) {
        Instant now = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        Instant expires = now.plus(tokenTtl).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        String id = UUID.randomUUID().toString();
        AdminSession session = new AdminSession(id, user.getUserId().toString(), user.getRole().getValue(),
                now.getEpochSecond(), expires.getEpochSecond());
        String token = Jwts.builder().issuer(issuer).subject(session.userId()).id(id)
                .issuedAt(Date.from(now)).expiration(Date.from(expires))
                .signWith(key, Jwts.SIG.HS256).compact();
        // 只有 Redis 成功保存会话后，才将 Token 返回给客户端。
        redis.opsForValue().set(sessionPrefix + id, JacksonUtil.objectMapper.writeValueAsString(session),
                Duration.between(Instant.now(), expires));
        return new LoginToken(session.userId(), token, "Bearer", expires.getEpochSecond());
    }

    public AdminSession authenticate(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw unauthorized();
        }
        Claims claims;
        try {
            var signed = parser.parseSignedClaims(authorization.substring(7).trim());
            claims = signed.getPayload();
            if (!"HS256".equals(signed.getHeader().getAlgorithm()) || claims.getExpiration() == null
                    || claims.getSubject() == null || claims.getId() == null || claims.getIssuedAt() == null) {
                throw unauthorized();
            }
            UUID.fromString(claims.getId());
            Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw unauthorized();
        }
        String value = redis.opsForValue().get(sessionPrefix + claims.getId());
        if (value == null) throw unauthorized();
        AdminSession session;
        try {
            session = JacksonUtil.objectMapper.readValue(value, AdminSession.class);
        } catch (JacksonException e) {
            throw unauthorized();
        }
        if (session == null || !claims.getId().equals(session.tokenId())
                || !claims.getSubject().equals(session.userId())
                || session.expiresAt() != claims.getExpiration().toInstant().getEpochSecond()
                || session.issuedAt() != claims.getIssuedAt().toInstant().getEpochSecond()
                || session.expiresAt() <= Instant.now().getEpochSecond()) {
            throw unauthorized();
        }
        return session;
    }

    public void logout(AdminSession session) {
        redis.delete(sessionPrefix + session.tokenId());
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
    }
}
