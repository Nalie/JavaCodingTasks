package test.service;

import test.exception.InvalidPasswordException;
import test.exception.NoSuchUserException;
import test.exception.UserIsAlreadyExistsException;
import test.model.User;
import test.util.DBUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nyapparova on 11.10.2016.
 */
public class UserServiceImpl implements UserService {

    @Override
    public void addUser(User user) throws IOException, SQLException, UserIsAlreadyExistsException {
        String sql = "select 1 from User1 where login = ?";
        ResultSet rs = null;
        try (Connection con = DBUtil.getDataSource().getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, user.getLogin());
            rs = st.executeQuery();
            while (rs.next()) {
                throw new UserIsAlreadyExistsException();
            }
            sql = "insert into User1 (login, password, balance) values (?, ?, ?)";
            try (PreparedStatement insSt = con.prepareStatement(sql)) {
                insSt.setString(1, user.getLogin());
                insSt.setString(2, user.getPass());
                insSt.setBigDecimal(3, new BigDecimal("0"));
                insSt.executeUpdate();
            }
        } finally {
            if (rs != null)
                rs.close();
        }
    }

    @Override
    public BigDecimal getUserBalance(User user) throws IOException, SQLException, NoSuchUserException, InvalidPasswordException {
        String sql = "select * from User1 where login = ?";
        ResultSet rs = null;
        try (Connection con = DBUtil.getDataSource().getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, user.getLogin());
            rs = st.executeQuery();
            while (rs.next()) {
                if (user.getPass().equals(rs.getString("password")))
                    return rs.getBigDecimal("balance");
                else
                    throw new InvalidPasswordException();
            }
            throw new NoSuchUserException();
        } finally {
            if (rs != null)
                rs.close();
        }
    }
}
