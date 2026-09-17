
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class UserTest {

    /*
     * User is abstract, so we create a small concrete
     * class inside the test so we can create a User object.
     */
    static class TestUser extends User {

        public TestUser(String userID,
                        String encryptedPassword,
                        String name,
                        Role role,
                        int failedLoginAttempts,
                        LocalDateTime lockoutDatetime,
                        String cpr) {

            super(userID,
                    encryptedPassword,
                    name,
                    role,
                    failedLoginAttempts,
                    lockoutDatetime,
                    cpr);
        }

        @Override
        public boolean verifyPassword(String rawPassword, String storedHashedPassword) {
            return false;
        }

        @Override
        public boolean checkLockedStatus() {
            return false;
        }
    }


    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------

    @Test
    public void testUserConstructor() {

        LocalDateTime lockTime =
                LocalDateTime.now().plusMinutes(1);

        TestUser user = new TestUser(
                "00001",
                "hashedPassword",
                "Ahmed",
                Role.Customer,
                2,
                lockTime,
                "123456789"
        );

        assertEquals("00001", user.getUserID());
        assertEquals("hashedPassword",
                user.getEncryptedPassword());
        assertEquals("Ahmed", user.getName());
        assertEquals(Role.Customer, user.getRole());
        assertEquals(2, user.getFailedLoginAttempts());
        assertEquals(lockTime, user.getLockoutDatetime());
        assertEquals("123456789", user.getCPR());
    }


    // ---------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------

    @Test
    public void testSetters() {

        TestUser user = new TestUser(
                "00001",
                "password",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        user.setUserID("00002");
        user.setEncryptedPassword("newPassword");
        user.setName("Mohammed");
        user.setRole(Role.Banker);
        user.setFailedLoginAttempts(2);

        LocalDateTime lockTime =
                LocalDateTime.now().plusMinutes(1);

        user.setLockoutDatetime(lockTime);
        user.setCPR("987654321");

        assertEquals("00002", user.getUserID());
        assertEquals("newPassword",
                user.getEncryptedPassword());
        assertEquals("Mohammed", user.getName());
        assertEquals(Role.Banker, user.getRole());
        assertEquals(2, user.getFailedLoginAttempts());
        assertEquals(lockTime, user.getLockoutDatetime());
        assertEquals("987654321", user.getCPR());
    }


    // ---------------------------------------------------------
    // Password Encryption
    // ---------------------------------------------------------

    @Test
    public void testEncryptPassword() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        String encrypted =
                user.encryptPassowrd("password");

        assertNotNull(encrypted);

        // SHA-256 produces 64 hexadecimal characters
        assertEquals(64, encrypted.length());

        // Same password should produce same hash
        assertEquals(
                encrypted,
                user.encryptPassowrd("password")
        );

        // Different password should produce a different hash
        assertNotEquals(
                encrypted,
                user.encryptPassowrd("differentPassword")
        );
    }


    // ---------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------

    @Test
    public void testAuthenticateCorrectPassword() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        String hashedPassword =
                user.encryptPassowrd("password");

        user.setEncryptedPassword(hashedPassword);

        assertTrue(
                user.authenticate("password")
        );
    }


    @Test
    public void testAuthenticateWrongPassword() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        String hashedPassword =
                user.encryptPassowrd("password");

        user.setEncryptedPassword(hashedPassword);

        assertFalse(
                user.authenticate("wrongPassword")
        );
    }


    // ---------------------------------------------------------
    // CPR Validation
    // ---------------------------------------------------------

    @Test
    public void testValidCPR() {

        assertTrue(User.isValidCPR("123456789"));
        assertTrue(User.isValidCPR("000000001"));
        assertTrue(User.isValidCPR("999999999"));
    }


    @Test
    public void testInvalidCPR() {

        assertFalse(User.isValidCPR(null));
        assertFalse(User.isValidCPR(""));
        assertFalse(User.isValidCPR("12345678"));
        assertFalse(User.isValidCPR("1234567890"));
        assertFalse(User.isValidCPR("12345678A"));
        assertFalse(User.isValidCPR("123-456-789"));
        assertFalse(User.isValidCPR("abcdefghi"));
    }


    // ---------------------------------------------------------
    // Failed Login Attempts
    // ---------------------------------------------------------

    @Test
    public void testIncrementFailedAttempts() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        assertEquals(0, user.getFailedLoginAttempts());

        user.incrementFailedAttempts();

        assertEquals(1, user.getFailedLoginAttempts());

        user.incrementFailedAttempts();

        assertEquals(2, user.getFailedLoginAttempts());
    }


    @Test
    public void testUserLocksAfterThreeFailedAttempts() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        user.incrementFailedAttempts();
        user.incrementFailedAttempts();
        user.incrementFailedAttempts();

        assertEquals(3,
                user.getFailedLoginAttempts());

        assertNotNull(
                user.getLockoutDatetime()
        );

        assertTrue(
                user.isCurrentlyLocked()
        );
    }


    // ---------------------------------------------------------
    // Locking
    // ---------------------------------------------------------

    @Test
    public void testUserIsNotLockedInitially() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        assertFalse(
                user.isCurrentlyLocked()
        );
    }


    @Test
    public void testUserIsLockedWhenLockoutIsInFuture() {

        LocalDateTime future =
                LocalDateTime.now().plusMinutes(5);

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                3,
                future,
                "123456789"
        );

        assertTrue(
                user.isCurrentlyLocked()
        );
    }


    @Test
    public void testUserIsNotLockedWhenLockoutHasExpired() {

        LocalDateTime past =
                LocalDateTime.now().minusMinutes(5);

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                3,
                past,
                "123456789"
        );

        assertFalse(
                user.isCurrentlyLocked()
        );
    }


    // ---------------------------------------------------------
    // Reset Failed Attempts
    // ---------------------------------------------------------

    @Test
    public void testResetFailedAttempts() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                3,
                LocalDateTime.now().plusMinutes(1),
                "123456789"
        );

        assertTrue(
                user.isCurrentlyLocked()
        );

        user.resetFailedAttempts();

        assertEquals(
                0,
                user.getFailedLoginAttempts()
        );

        assertNull(
                user.getLockoutDatetime()
        );

        assertFalse(
                user.isCurrentlyLocked()
        );
    }


    // ---------------------------------------------------------
    // CPR Getter and Setter
    // ---------------------------------------------------------

    @Test
    public void testCPRGetterAndSetter() {

        TestUser user = new TestUser(
                "00001",
                "",
                "Ahmed",
                Role.Customer,
                0,
                null,
                "123456789"
        );

        assertEquals(
                "123456789",
                user.getCPR()
        );

        user.setCPR("987654321");

        assertEquals(
                "987654321",
                user.getCPR()
        );
    }
}
