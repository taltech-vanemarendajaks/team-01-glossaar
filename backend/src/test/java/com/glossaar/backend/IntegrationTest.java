package com.glossaar.backend;

import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.user.UserPrincipal;
import com.glossaar.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb-${random.uuid};DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public abstract class IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    public UserEntity testUser;
    public UserPrincipal testUserPrincipal;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        testUser = createTestUser();
        testUserPrincipal = createTestUserPrincipal(testUser);
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity("testuser", "test@test.com");
        return userRepository.save(user);
    }

    private UserPrincipal createTestUserPrincipal(UserEntity testUser) {
        return new UserPrincipal(testUser);
    }
}
