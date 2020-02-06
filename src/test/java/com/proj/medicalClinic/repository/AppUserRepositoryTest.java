package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Patient;
import com.proj.medicalClinic.model.RoleType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AppUserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AppUserRepository appUserRepository;

    private static final String EMAIL = "neki@gmail.com";

    private static final String PASSWORD = "sifra123";

    private static final String NAME = "Ime";

    private static final String LASTNAME = "Prezime";

    private static final RoleType ROLE = RoleType.PATIENT;

    private static final String ADDRESS = "Adresa";

    private static final String CITY = "Grad";

    private static final String STATE = "Opstina";

    private static final String MOBILE = "123456789";

    @After
    public void cleanUp(){
        appUserRepository.deleteAll();
    }

    //FIND BY EMAIL
    @Test
    public void shouldReturnAppUserWhenFindingExistingAppUserByEmail(){
        Optional<AppUser> foundAppUser = appUserRepository.findByEmail("Miljana@gmail.com");
        System.out.println("\n\n\n\n\n"+foundAppUser.get().getEmail());

        assertTrue("App user does exist", foundAppUser.isPresent());
        assertEquals("AppUser contains correct email", foundAppUser.get().getEmail(), "Miljana@gmail.com");
    }

    @Test
    public void shouldReturnEmptyOptionalWhenFindingNonExistingUserByEmail(){
        Optional<AppUser> foundAppUser = appUserRepository.findByEmail("neki@gmail.com");

        assertFalse("App user does not exist", foundAppUser.isPresent());
    }


    //FIND BY USER ROLE
    @Test
    public void shouldReturnListOfPatientsWhenFindingByPatientRole(){
        Optional<List<AppUser>> foundAppUsers = appUserRepository.findByUserRole(RoleType.PATIENT);
        if(!(foundAppUsers.get().isEmpty())) {
            assertTrue("At least one patient does exist", foundAppUsers.isPresent());
            for (AppUser appUser : foundAppUsers.get()) {
                assertEquals("AppUser has role patient", appUser.getUserRole(), RoleType.PATIENT);
            }
        }
        else{
            assertFalse("AppUser with role patient does not exist", foundAppUsers.isPresent());
        }
    }
}
