package de.invesdwin.context.integration.streams.key.password;

/**
 * Only hashes to strengthen and stretch a weak password in order to apply HKDF on it to derive multiple keys.
 * 
 * For encoding passwords for storage, use spring-security-crypto PasswordEncoder instead. It encodes the algorithm
 * parameters in the hash and allows to determine if passwords should be upgraded.
 * 
 * Bcrypt is better than Scrypt (until it is proven, which it is by now) and PBKDF2 (easily cracked by GPUs):
 * https://medium.com/@mpreziuso/password-hashing-pbkdf2-scrypt-bcrypt-1ef4bb9c19b3
 * 
 * Argon2 is better than Scrypt or Bcrypt:
 * https://medium.com/analytics-vidhya/password-hashing-pbkdf2-scrypt-bcrypt-and-argon2-e25aaf41598e
 * 
 * https://github.com/Password4j/password4j/wiki/Recommended-settings#responsiveness-3
 */
public interface IPasswordHasher {

    byte[] hash(byte[] salt, byte[] password, int length);

}
