package com.bridgelabz;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {

    private static AddressBookDBService addressBookDBService;
    private PreparedStatement addressBookDataStatement;

    private AddressBookDBService() {

    }

    public static AddressBookDBService getInstance() {
        if (addressBookDBService == null)
            addressBookDBService = new AddressBookDBService();
        return addressBookDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/address_book_system?useSSL=false";
        String userName = "root";
        String password = "Pawar@1995";
        Connection connection;
        System.out.println("Connecting to database:" + jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is successful!!!" + connection);
        return connection;
    }


    public List<Person> readData() {
        String query = "SELECT * from address_book;";
        return this.getPersonDetailsFromDatabase(query);
    }

    private List<Person> getPersonDetailsFromDatabase(String query) {
        List<Person> personList = new ArrayList<Person>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            personList = this.getPersonData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personList;
    }

    private List<Person> getPersonData(ResultSet resultSet) {

        List<Person> personList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String address = resultSet.getString("address");
                String city = resultSet.getString("city");
                String state = resultSet.getString("state");
                String zip = resultSet.getString("zip");
                String mobileNumber = resultSet.getString("mobileNumber");
                String email = resultSet.getString("email");
                LocalDate entryDate = resultSet.getDate("entryDate").toLocalDate();
                personList.add(new Person(id,firstName,lastName,address,city,state,zip,mobileNumber,email,entryDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personList;

    }

    public int updateContactNumber(String firstName, String contactNumber) {
        return this.updateAddressBookDataUsingStatement(firstName, contactNumber);

    }

    private int updateAddressBookDataUsingStatement(String firstName, String mobileNumber) {
        String sql = String.format("update address_book set mobileNumber = '%s' where firstName = '%s';", mobileNumber, firstName);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Person> getaddressBookData(String firstName) {
        List<Person> personList = null;
        if (this.addressBookDataStatement == null)
            this.prepareStatementForAddressBookData();
        try {
            addressBookDataStatement.setString(1, firstName);
            ResultSet resultSet = addressBookDataStatement.executeQuery();
            personList = this.getPersonData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personList;
    }

    private void prepareStatementForAddressBookData() {
        try {
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM addressbook WHERE firstName = ?";
            addressBookDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Person> getAddressBookForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("SELECT * FROM address_book WHERE entryDate BETWEEN '%s' AND '%s';",
                Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getAddressBookDataUsingDB(sql);
    }

    private List<Person> getAddressBookDataUsingDB(String sql) {
        List<Person> addressBookList = new ArrayList<>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            addressBookList = this.getPersonData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addressBookList;
    }

    //main
    public static void main(String[] args) {

        AddressBookDBService addressBookDBService = new AddressBookDBService();
        List<Person> dataList = addressBookDBService.readData();
        addressBookDBService.updateAddressBookDataUsingStatement("abc", "9999999999");
        System.out.println(dataList);

    }
}