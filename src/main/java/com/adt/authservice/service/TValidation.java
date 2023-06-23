//package com.adt.authservice.service;
//
//import org.slf4j.Logger;import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import com.adt.authservice.cache.LoggedOutJwtTokenCache;
//import com.adt.authservice.exception.InvalidTokenRequestException;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.SignatureException;
//import io.jsonwebtoken.UnsupportedJwtException;
//
//public class TValidation {
//	
//	private static final Logger logger = LOGGER.getLogger(TValidation.class);
//    private final String jwtSecret;
//    private final LoggedOutJwtTokenCache loggedOutTokenCache;
//
//    @Autowired
//    public TValidation(@Value("${app.jwt.secret}") String jwtSecret, LoggedOutJwtTokenCache loggedOutTokenCache) {
//        this.jwtSecret = jwtSecret;
//        this.loggedOutTokenCache = loggedOutTokenCache;
//    }
//	
//	
//	public boolean validateToken(String authToken) {
//        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
//
//        } catch (SignatureException ex) {
//            LOGGER.error("Invalid JWT signature");
//            throw new InvalidTokenRequestException("JWT", authToken, "Incorrect signature");
//
//        } catch (MalformedJwtException ex) {
//            LOGGER.error("Invalid JWT token");
//            throw new InvalidTokenRequestException("JWT", authToken, "Malformed jwt token");
//
//        } catch (ExpiredJwtException ex) {
//            LOGGER.error("Expired JWT token");
//            throw new InvalidTokenRequestException("JWT", authToken, "Token expired. Refresh required");
//
//        } catch (UnsupportedJwtException ex) {
//            LOGGER.error("Unsupported JWT token");
//            throw new InvalidTokenRequestException("JWT", authToken, "Unsupported JWT token");
//
//        } catch (IllegalArgumentException ex) {
//            LOGGER.error("JWT claims string is empty.");
//            throw new InvalidTokenRequestException("JWT", authToken, "Illegal argument token");
//        }
//    //    validateTokenIsNotForALoggedOutDevice(authToken);
//        return true;
//    }
//	
//	public long getTokenExpiryFromJWT(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getExpiration().getTime();
//    }
//	
////	 private void validateTokenIsNotForALoggedOutDevice(String authToken) {
////	        OnUserLogoutSuccessEvent previouslyLoggedOutEvent = loggedOutTokenCache.getLogoutEventForToken(authToken);
////	        if (previouslyLoggedOutEvent != null) {
////	            String userEmail = previouslyLoggedOutEvent.getUserEmail();
////	            Date logoutEventDate = previouslyLoggedOutEvent.getEventTime();
////	            String errorMessage = String.format("Token corresponds to an already logged out user [%s] at [%s]. Please login again", userEmail, logoutEventDate);
////	            throw new InvalidTokenRequestException("JWT", authToken, errorMessage);
////	        }
////	    }
//
//}
