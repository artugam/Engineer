package com.artur.engineer.engine.managers;

import com.artur.engineer.engine.exceptions.ApiException;
import com.artur.engineer.engine.readers.CoursesReader;
import com.artur.engineer.engine.readers.RoleReader;
import com.artur.engineer.engine.readers.UserReader;
import com.artur.engineer.entities.Course;
import com.artur.engineer.entities.Role;
import com.artur.engineer.entities.User;
import com.artur.engineer.payload.ApiResponse;
import com.artur.engineer.payload.user.UserCreate;
import com.artur.engineer.payload.user.UserCreateWithPassword;
import com.artur.engineer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.synchronoss.cloud.nio.multipart.Multipart;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Artur Pilch <artur.pilch12@gmail.com>
 */
@Component("UserManager")
public class UserManager {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleReader roleReader;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoursesReader coursesReader;

    @Autowired
    private UserReader userReader;

    public User createOrUpdate(User user, String firstName, String lastName, String email, String password, Collection<Role> roles, boolean enabled) throws ApiException {

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        if (!password.equals("")) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setRoles(roles);
        user.setEnabled(enabled);

        return userRepository.save(user);
    }

    public User create(UserCreateWithPassword userCreate) throws ApiException {
        return this.createOrUpdate(new User(), userCreate.getFirstName(), userCreate.getLastName(), userCreate.getEmail(), userCreate.getPassword(), roleReader.getUserRoles(userCreate.getRole()), true);
    }

    public User edit(Long id, UserCreate userCreate) throws ApiException {
        User user = userReader.get(id);
        return this.createOrUpdate(user, userCreate.getFirstName(), userCreate.getLastName(), userCreate.getEmail(), "", roleReader.getUserRoles(userCreate.getRole()), true);
    }

    @Transactional
    public void remove(Long id) {
        userRepository.deleteById(id);
    }

    public User block(Long id) {
        return this.setUserStatus(id, false);
    }

    public User unblock(Long id) {
        return this.setUserStatus(id, true);
    }

    public User setUserStatus(Long id, boolean status) {
        User user = userReader.get(id);
        user.setEnabled(status);
        userRepository.save(user);

        return user;
    }

    public ApiResponse addUsersFromCsv(MultipartFile file, Long courseId) throws IOException, ApiException {
        BufferedReader br;
        List<String[]> result = new ArrayList<>();
        try {

            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                result.add(line.split(","));
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        List<User> currentUsersList = userRepository.findAll();
        Map<String, User> usersIndexMap = new HashMap<>();
        for (User u : currentUsersList) {
            if(null != u.getUniqueNumber() && !u.getUniqueNumber().equals("")) {
                usersIndexMap.put(u.getUniqueNumber(), u);
            }
        }

        Map<String, User> usersEmailMap = new HashMap<>();
        for (User u : currentUsersList) {
            if(null != u.getEmail() && !u.getEmail().equals("")) {
                usersEmailMap.put(u.getUniqueNumber(), u);
            }
        }

        Course course = coursesReader.get(courseId);

        List<User> usersList = new ArrayList();
        for (String[] names : result) {
            String firstName = names[0];
            String lastName = names[1];
            String index = names[2];
            String email = names[3];

            if (null != usersIndexMap.get(index)) {
                throw new ApiException("Użytkownik o indeksie " + index + " istnieje!");
            }

            if (null != usersEmailMap.get(email)) {
                throw new ApiException("Użytkownik o emailu " + email + " istnieje!");
            }


            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUniqueNumber(index);
            user.setEmail(email);
            user.setPassword("notProvided");
            user.setRoles(roleReader.getUserRoleAsCollection());
            user.setEnabled(true);
            user.addCourse(course);
            usersList.add(user);
        }
        userRepository.saveAll(usersList);

        return new ApiResponse(true, "Studenci zostali dodani do bazy");
    }
}
