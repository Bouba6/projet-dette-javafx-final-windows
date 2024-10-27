package detteproject.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DataSourceImpl<T> implements DataSource {
    Connection connection;
    private static final String url = "jdbc:postgresql://localhost:5433/gesdette2";
    private static final String user = "postgres";
    private static final String password = "root";
    protected PreparedStatement stm;

    @Override
    public Connection setConnexion() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void closeConnexion() {
        try {
            if (connection == null || connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        return stm.executeUpdate();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return stm.executeQuery();
    }

    @Override
    public void innit(String sql) {
        try {
            Connection connection = setConnexion();
            if (sql.startsWith("INSERT")) {
                stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                stm = connection.prepareStatement(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generateSql(Object entity, String action, String tableName, List<String> excludeFields,
            String condition, String[] column) {
        StringBuilder sql = new StringBuilder();
        // Obtenir tous les champs de la classe, y compris ceux hérités
        List<Field> allFields = new ArrayList<>();
        Class<?> clazz = entity.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass(); // Passer à la classe parente
        }

        // Filtrer les champs exclus
        List<Field> includedFields = allFields.stream()
                .filter(field -> !excludeFields.contains(field.getName()))
                .collect(Collectors.toList());

        switch (action.toUpperCase()) {
            case "INSERT":
                sql.append("INSERT INTO ").append(tableName).append(" (");
                String fieldNames = includedFields.stream()
                        .map(field -> {

                            String fieldName = field.getName();
                            Class<?> declaringClass = field.getDeclaringClass();

                            if (field.getName().equals("user")) {
                                return "\"userId\""; // Map user to userId
                            } else if (field.getName().equals("role")) {
                                return "\"roleId\""; // Map role to roleId
                            } else if (field.getName().equals("dette")) {
                                return "detteid"; // Map role to roleId
                            } else if (field.getName().equals("article")) {
                                return "articleid"; // Map role to roleId
                            } else if (field.getName().equals("etat")) {
                                if (declaringClass.equals(Dette.class)) {
                                    return "etatid";
                                }

                            } else if (field.getName().equals("client")) {
                                return "clientid";
                            } else if (field.getName().equals("state")) {
                                return "stateid";
                            } else {
                                return field.getName(); // Keep other fields as is
                            }
                            return fieldName;
                        })
                        .collect(Collectors.joining(", "));
                sql.append(fieldNames).append(") VALUES (");
                String placeholders = includedFields.stream()
                        .map(field -> "?")
                        .collect(Collectors.joining(", "));
                sql.append(placeholders).append(")");

                // Clause pour récupérer l'ID généré après l'insertion (PostgreSQL)
                sql.append(" RETURNING id;");
                break;

            case "UPDATE":
                sql.append("UPDATE ").append(tableName).append(" SET ");
                String updateFields = includedFields.stream()
                        .map(field -> {
                            if (field.getName().equals("user")) {
                                return "\"userId\" = ?";
                            } else if (field.getName().equals("client")) {
                                return "\"clientid\" = ?";
                            } else if (field.getName().equals("etat")) {
                                return "\"etatid\" = ?";
                            } else if (field.getName().equals("state")) {
                                return "\"stateid\" = ?";
                            } else {
                                return field.getName() + " = ?";
                            }
                        })
                        .collect(Collectors.joining(", "));
                sql.append(updateFields);

                // Ajouter la condition si elle est fournie
                if (condition != null && !condition.trim().isEmpty()) {
                    sql.append(" WHERE ").append(condition);
                } else {
                    sql.append(" WHERE id = ?"); // Assumant que 'id' est la clé primaire
                }
                break;

            case "SELECTALL":
                String selectFields = includedFields.stream()
                        .filter(field -> Arrays.asList(column).contains(field.getName()))
                        .map(field -> "\"" + field.getName().toLowerCase() + "\"") // Ensure proper quoting and case
                        .collect(Collectors.joining(", "));

                // Use selected fields or * if none match
                if (selectFields.isEmpty()) {
                    sql.append("SELECT * ");
                } else {
                    sql.append("SELECT ").append(selectFields).append(" ");
                }

                sql.append("FROM ").append(tableName);

                // Add the condition if provided
                if (condition != null && !condition.trim().isEmpty()) {
                    sql.append(" WHERE ").append(condition);
                }
                break;

            default:
                throw new IllegalArgumentException("Action non prise en charge : " + action);
        }

        return sql.toString();
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass(); // Remonter la hiérarchie des classes
        }

        return fields;
    }

    @Override
    public void setFields(Object entity, PreparedStatement preparedStatement, List<String> excludeFields)
            throws SQLException, IllegalAccessException, NoSuchFieldException {

        List<Field> includedFields = getAllFields(entity.getClass()).stream()
                .filter(field -> !excludeFields.contains(field.getName()))
                .collect(Collectors.toList());

        int parameterIndex = 1; // Index des paramètres pour PreparedStatement

        for (Field field : includedFields) {
            field.setAccessible(true); // Permettre l'accès aux champs privés

            Object value = field.get(entity);
            if (value != null) {
                if (field.getType() == String.class) {
                    preparedStatement.setString(parameterIndex, (String) value);
                } else if (field.getType() == int.class || field.getType() == Integer.class) {
                    preparedStatement.setInt(parameterIndex, (Integer) value);
                } else if (field.getType() == double.class || field.getType() == Double.class) {
                    preparedStatement.setDouble(parameterIndex, (Double) value);
                } else if (field.getType() == long.class || field.getType() == Long.class) {
                    preparedStatement.setLong(parameterIndex, (Long) value);
                } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                    preparedStatement.setBoolean(parameterIndex, (Boolean) value);
                } else if (field.getType() == Timestamp.class) {
                    preparedStatement.setTimestamp(parameterIndex, (Timestamp) value);
                } else if (field.getType() == java.sql.Date.class) {
                    preparedStatement.setDate(parameterIndex, (java.sql.Date) value);

                } else if (field.getType().isEnum()) {
                    // Handle enums (role, etat)
                    Enum<?> enumValue = (Enum<?>) value;
                    preparedStatement.setInt(parameterIndex, enumValue.ordinal()); // Store the ordinal value
                    // Alternatively, you could store the enum name if preferred
                    // preparedStatement.setString(parameterIndex, enumValue.name());
                } else if (field.getType() == User.class) {
                    // Supposons que vous ayez un champ utilisateur avec un identifiant

                    if (value != null) {
                        preparedStatement.setInt(parameterIndex, ((User) value).getId());
                    }
                } else if (field.getType() == Client.class) {
                    // Supposons que vous ayez un champ utilisateur avec un identifiant
                    if (value != null) {
                        preparedStatement.setInt(parameterIndex, ((Client) value).getId());
                    }
                } else if (field.getType() == LocalDateTime.class) {
                    preparedStatement.setTimestamp(parameterIndex, Timestamp.valueOf((LocalDateTime) value));
                } else if (field.getType() == LocalDate.class) {
                    preparedStatement.setDate(parameterIndex, Date.valueOf((LocalDate) value));
                } else if (field.getType() == Dette.class) {
                    preparedStatement.setInt(parameterIndex, ((Dette) value).getId());
                } else if (field.getType() == Article.class) {
                    preparedStatement.setInt(parameterIndex, ((Article) value).getId());
                }

                else if (field.getType() == java.util.Date.class) {
                    preparedStatement.setTimestamp(parameterIndex, new Timestamp(((java.util.Date) value).getTime()));
                } else {
                    System.out.println("Type non pris en charge pour le champ : " + field.getName());
                }
            }

            parameterIndex++;
        }
    }

    public void populateEntityFromResultSet(T entity, ResultSet resultSet, List<String> excludeFields)
            throws SQLException, IllegalAccessException, NoSuchFieldException {

        List<Field> includedFields = getAllFields(entity.getClass()).stream()
                .filter(field -> !excludeFields.contains(field.getName()))
                .collect(Collectors.toList());

        for (Field field : includedFields) {
            field.setAccessible(true); // Allow access to private fields

            // Get the value from the ResultSet
            Object value = null;
            String columnName = "\"" + field.getName() + "\"";

            if (field.getType() == String.class) {
                value = resultSet.getString(columnName);
            } else if (field.getType() == int.class || field.getType() == Integer.class) {
                value = resultSet.getInt(field.getName());
            } else if (field.getType() == double.class || field.getType() == Double.class) {
                value = resultSet.getDouble(field.getName());
            } else if (field.getType() == long.class || field.getType() == Long.class) {
                value = resultSet.getLong(field.getName());
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                value = resultSet.getBoolean(field.getName());
            } else if (field.getType() == Timestamp.class) {
                value = resultSet.getTimestamp(field.getName());
            } else if (field.getType() == java.sql.Date.class) {
                value = resultSet.getDate(field.getName());
            } else if (field.getType().isEnum()) {
                // Handle enums
                String enumValue = resultSet.getString(field.getName());
                if (enumValue != null) {
                    value = Enum.valueOf((Class<Enum>) field.getType(), enumValue);
                }
            } else if (field.getType() == User.class) {
                int userId = resultSet.getInt(field.getName());
                if (!resultSet.wasNull()) {
                    User user = new User(); // Assuming you have a User constructor
                    user.setId(userId);
                    value = user;
                }
            } else if (field.getType() == Client.class) {
                int clientId = resultSet.getInt(field.getName());
                if (!resultSet.wasNull()) {
                    Client client = new Client(); // Assuming you have a Client constructor
                    client.setId(clientId);
                    value = client;
                }
            } else if (field.getType() == LocalDateTime.class) {
                value = resultSet.getTimestamp(field.getName()).toLocalDateTime();
            } else if (field.getType() == java.util.Date.class) {
                value = new Date(resultSet.getTimestamp(field.getName()).getTime());
            } else {
                System.out.println("Unsupported type for field: " + field.getName());
            }

            field.set(entity, value); // Set the value to the entity's field
        }
    }

}
