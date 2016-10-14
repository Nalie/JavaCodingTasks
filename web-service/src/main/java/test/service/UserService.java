package test.service;

import test.exception.InvalidPasswordException;
import test.exception.NoSuchUserException;
import test.exception.UserIsAlreadyExistsException;
import test.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created by nyapparova on 11.10.2016.
 */
public interface UserService {

    void addUser(User user) throws IOException, SQLException, UserIsAlreadyExistsException;
    BigDecimal getUserBalance(User user) throws IOException, SQLException, NoSuchUserException, InvalidPasswordException;
}
