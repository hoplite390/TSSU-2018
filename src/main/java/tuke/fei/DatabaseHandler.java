package tuke.fei;

import java.sql.*;

/**
 * Class used for insertion a selection to and from database
 */
public class DatabaseHandler {
    private static final String INSERT_INTO_ADDRESS_TABLE = "INSERT INTO ADDRESS(STREET, POSTAL_CODE, CITY, COUNTRY, RAW) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_INTO_PARTY_TABLE = "INSERT INTO PARTY(IS_ENTITY, ORGANIZATION_IDENTIFICATION_NUMBER, NAME, PUBLIC_OFFICIAL, ADDRESS_KEY) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_INTO_ORGANIZATION_COMPANION_RELATION_TABLE = "INSERT INTO ORGANIZATION_COMPANION_RELATION (ORGANIZATION_KEY, COMPANION_KEY, SHARE) VALUES (?, ?, ?)";
    private static final String INSERT_INTO_ORGANIZATION_END_USER_RELATION_TABLE = "INSERT INTO ORGANIZATION_END_USER_RELATION (ORGANIZATION_KEY, END_USER_KEY) VALUES (?, ?)";
    private static final String INSERT_INTO_INVOICE_TABLE = "INSERT INTO INVOICE (COST, SUBJECT, DATE_OF_PUBLICATION, DATE_OF_ISSUE, DUE_DATE, DATE_OF_DELIVERY, DATE_OF_PAYMENT, DATE_OF_CANCELLATION, PARTY_KEY, CITY_KEY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_FROM_PARTY_BY_IDENTIFICATION_NUMBER = "SELECT PARTY_KEY FROM PARTY WHERE ORGANIZATION_IDENTIFICATION_NUMBER = ?";

    public static int insertIntoAddressTable(Connection connection, Address address, String raw) {
        try (PreparedStatement stmnt = connection.prepareStatement(INSERT_INTO_ADDRESS_TABLE, Statement.RETURN_GENERATED_KEYS)) {
            if (address.getStreet() == null) {
                stmnt.setNull(1, Types.NVARCHAR);
            } else {
                stmnt.setString(1, address.getStreet());
            }
            if (address.getPostalCode() == null) {
                stmnt.setNull(2, Types.NVARCHAR);
            } else {
                stmnt.setString(2, address.getPostalCode());
            }
            if (address.getCity() == null) {
                stmnt.setNull(3, Types.NVARCHAR);
            } else {
                stmnt.setString(3, address.getCity());
            }
            if (address.getCountry() == null) {
                stmnt.setNull(4, Types.NVARCHAR);
            } else {
                stmnt.setString(4, address.getCountry());
            }
            stmnt.setString(5, raw);
            stmnt.executeUpdate();
            try (ResultSet generatedKeys = stmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int insertIntoPartyTable(Connection connection, boolean isEntity, Integer identificationNumber, String name, Boolean publicOfficial, Integer addressKey) {
        try (PreparedStatement stmnt = connection.prepareStatement(INSERT_INTO_PARTY_TABLE, Statement.RETURN_GENERATED_KEYS)) {
            stmnt.setBoolean(1, isEntity);
            if (identificationNumber == null) {
                stmnt.setNull(2, Types.INTEGER);
            } else {
                stmnt.setInt(2, identificationNumber);
            }
            stmnt.setString(3, name);
            if (publicOfficial == null) {
                stmnt.setNull(4, Types.BIT);
            } else {
                stmnt.setBoolean(4, publicOfficial);
            }
            if (addressKey == null) {
                stmnt.setNull(5, Types.INTEGER);
            } else {
                stmnt.setInt(5, addressKey);
            }
            stmnt.executeUpdate();
            try (ResultSet generatedKeys = stmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Integer selectFromPartyByIdentificationNumber(Connection connection, int identificationNumber) {
        try (PreparedStatement stmnt = connection.prepareStatement(SELECT_FROM_PARTY_BY_IDENTIFICATION_NUMBER)) {
            stmnt.setInt(1, identificationNumber);
            stmnt.execute();
            ResultSet rs = stmnt.getResultSet();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertIntoOrganizationCompanionRelation(Connection connection, int organizationKey, int companionKey, float share) {
        try (PreparedStatement stmnt = connection.prepareStatement(INSERT_INTO_ORGANIZATION_COMPANION_RELATION_TABLE)) {
            stmnt.setInt(1, organizationKey);
            stmnt.setInt(2, companionKey);
            stmnt.setFloat(3, share);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoOrganizationEndUserRelation(Connection connection, int organizationKey, int endUserKey) {
        try (PreparedStatement stmnt = connection.prepareStatement(INSERT_INTO_ORGANIZATION_END_USER_RELATION_TABLE)) {
            stmnt.setInt(1, organizationKey);
            stmnt.setInt(2, endUserKey);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static int insertIntoInvoiceTable(Connection connection, Invoice invoice, Integer partyKey, Integer cityKey) {
        try (PreparedStatement stmnt = connection.prepareStatement(INSERT_INTO_INVOICE_TABLE, Statement.RETURN_GENERATED_KEYS)) {
            stmnt.setFloat(1, invoice.getCost());
            if (invoice.getSubject() == null) {
                stmnt.setNull(2, Types.NVARCHAR);
            } else {
                stmnt.setString(2, invoice.getSubject());
            }
            if (invoice.getDateOfPublication() == null) {
                stmnt.setNull(3, Types.DATE);
            } else {
                stmnt.setDate(3, new java.sql.Date(invoice.getDateOfPublication().getTime()));
            }
            if (invoice.getDateOfIssue() == null) {
                stmnt.setNull(4, Types.DATE);
            } else {
                stmnt.setDate(4, new java.sql.Date(invoice.getDateOfIssue().getTime()));
            }
            if (invoice.getDueDate() == null) {
                stmnt.setNull(5, Types.DATE);
            } else {
                stmnt.setDate(5, new java.sql.Date(invoice.getDueDate().getTime()));
            }
            if (invoice.getDateOfDelivery() == null) {
                stmnt.setNull(6, Types.DATE);
            } else {
                stmnt.setDate(6, new java.sql.Date(invoice.getDateOfDelivery().getTime()));
            }
            if (invoice.getDateOfPayment() == null) {
                stmnt.setNull(7, Types.DATE);
            } else {
                stmnt.setDate(7, new java.sql.Date(invoice.getDateOfPayment().getTime()));
            }
            if (invoice.getDateOfCancellation() == null) {
                stmnt.setNull(8, Types.DATE);
            } else {
                stmnt.setDate(8, new java.sql.Date(invoice.getDateOfCancellation().getTime()));
            }
            if (partyKey == null) {
                stmnt.setNull(9, Types.INTEGER);
            } else {
                stmnt.setInt(9, partyKey);
            }
            if (cityKey == null) {
                stmnt.setNull(10, Types.INTEGER);
            } else {
                stmnt.setInt(10, cityKey);
            }
            stmnt.executeUpdate();

            try (ResultSet generatedKeys = stmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
