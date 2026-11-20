package per.chowh.bot.web.domain;

public record AdminSession(String tokenId, String userId, int role, long issuedAt, long expiresAt) {
}
