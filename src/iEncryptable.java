public interface iEncryptable{
    String encryptPassowrd(String rawPassword);
    boolean verifyPassword(String rawPassword, String storedHashedPassword);
}
