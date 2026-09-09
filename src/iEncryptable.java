public interface iEncryptable{
    boolean authenticate(String inputPassword);

    String encryptPassowrd(String rawPassword);
    boolean verifyPassword(String rawPassword, String storedHashedPassword);
}
