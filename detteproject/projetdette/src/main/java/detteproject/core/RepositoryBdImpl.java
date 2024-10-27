package detteproject.core;

import detteproject.State.Etat;
import detteproject.State.EtatDette;
import detteproject.State.Role;
import detteproject.State.StateDette;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.AbstractEntity;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.reflect.Field;

public abstract class RepositoryBdImpl<T> extends DataSourceImpl<T> implements Repositorie<T> {

    protected abstract String getTableName();

    protected abstract String[] column();

    protected Class<T> entityClass;

    public RepositoryBdImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // Surcharge avec connexion pour adapter à ton contexte
    public RepositoryBdImpl() {
        this.connection = this.setConnexion();
    }

    protected abstract List<String> excludedFieldsInsert();

    protected abstract List<String> excludedFieldsUpdate();

    protected abstract List<String> excludedFieldsSelect();

    protected abstract List<String> getColumnNames();

    @Override
    public boolean insert(T objet) {
        String sql = super.generateSql(objet, "INSERT", getTableName(), excludedFieldsInsert(), null, null);
        try {
            super.innit(sql);
            System.err.println(sql);
            // Utilisation de setFields pour définir les valeurs des paramètres
            super.setFields(objet, stm, excludedFieldsInsert()); // Ici, vous passez l'objet, le PreparedStatement et
            // les
            // champs exclus
            // Exécution de la requête
            int affectedRows = stm.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    // Set the ID back to the object as needed
                    if (objet instanceof User) {
                        ((User) objet).setId(generatedId);
                    } else if (objet instanceof AbstractEntity) {
                        ((AbstractEntity) objet).setId(generatedId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // Assurez-vous de fermer le PreparedStatement ici si nécessaire
        }
        return false;
    }

    public List<T> selectAll(String condition) throws InstantiationException {
        List<T> results = new ArrayList<>();
        String sql = super.generateSql(entityClass, "SELECTALL", getTableName(), excludedFieldsSelect(),
                condition,
                column());

        try {
            // Initialize statement or connection if necessary
            super.innit(sql); // Ensure this correctly initializes the statement
            System.err.println(sql);

            // Execute the select query
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                T entity;
                // Create and populate the entity from the ResultSet
                entity = createEntityFromResultSet(resultSet, excludedFieldsSelect());

                results.add(entity);
            }

            // Close ResultSet
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Ensure resources are cleaned up if necessary
            if (stm != null) {
                try {
                    stm.close(); // Close statement if you're not reusing it
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return results; // Return the list of entities
    }

    @Override
    public void update(T objet) {
        String sql = super.generateSql(objet, "UPDATE", getTableName(), excludedFieldsUpdate(), null, column());
        try {
            // if (objet instanceof Client) {
            // User user = ((Client) objet).getUser();
            // System.out.println(user);
            // }
            super.innit(sql);
            System.err.println(sql);

            // Set fields for the PreparedStatement using the update exclusion list
            super.setFields(objet, stm, excludedFieldsUpdate());

            // Explicitly set the ID as the last parameter for the WHERE clause
            if (objet instanceof AbstractEntity) {
                int idParameterIndex = stm.getParameterMetaData().getParameterCount();
                stm.setInt(idParameterIndex, ((AbstractEntity) objet).getId());
            } else {
                throw new IllegalArgumentException("The provided object does not have an ID field.");
            }

            // Execute the update
            int affectedRows = stm.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Update successful");
            } else {
                System.out.println("Update failed");
            }

        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            // Close the PreparedStatement if necessary
        }
    }

    protected int getFieldIndex(String fieldName) {
        int index = -1;
        Class<?> currentClass = entityClass;

        // Traverse through the class hierarchy to search for the field
        while (currentClass != null) {
            Field[] fields = currentClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equalsIgnoreCase(fieldName)) {
                    index = i; // Found the field, get its index
                    break;
                }
            }

            if (index != -1) {
                break; // Exit the loop if the field is found
            }

            // Move to the superclass to continue the search
            currentClass = currentClass.getSuperclass();
        }

        if (index == -1) {
            throw new IllegalArgumentException(
                    "Field '" + fieldName + "' not found in the entity class or its superclasses.");
        }

        // SQL parameter indices are 1-based, so we add 1 to the zero-based index
        return index + 1;
    }

    protected T createEntityFromResultSet(ResultSet resultSet, List<String> excludeFields) throws SQLException {
        T entity = null;

        // Instantiate the entity based on the type parameter T
        try {
            entity = entityClass.getDeclaredConstructor().newInstance(); // Create a new instance of T
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Failed to create an instance of the entity", e);
        }

        // Combine fields from the class and its superclass
        Field[] fields = Stream.concat(
                Arrays.stream(entityClass.getDeclaredFields()),
                Arrays.stream(entityClass.getSuperclass().getDeclaredFields())).toArray(Field[]::new);

        // Set the fields of the entity using the ResultSet, excluding specified fields
        for (Field field : fields) {
            if (!excludeFields.contains(field.getName())) { // Skip fields in the exclude list
                field.setAccessible(true); // Make private fields accessible

                Object value = null; // Initialize value variable
                String columnName = field.getName().toLowerCase(); // Assuming the field names match the column

                try {
                    // Retrieve the value from the ResultSet based on field type
                    if (field.getType() == String.class) {
                        value = resultSet.getString(columnName);
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        value = resultSet.getInt(columnName);
                        if (resultSet.wasNull())
                            value = null;
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        value = resultSet.getDouble(columnName);
                        if (resultSet.wasNull())
                            value = null;
                    } else if (field.getType() == long.class || field.getType() == Long.class) {
                        value = resultSet.getLong(columnName);
                        if (resultSet.wasNull())
                            value = null;
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        value = resultSet.getBoolean(columnName);
                        if (resultSet.wasNull())
                            value = null;
                    } else if (field.getType() == Timestamp.class) {
                        value = resultSet.getTimestamp(columnName);
                    } else if (field.getType() == java.sql.Date.class) {
                        value = resultSet.getDate(columnName);
                    } else if (field.getType().isEnum()) {
                        if (field.getType() == Role.class) {
                            int roleId = resultSet.getInt("roleId"); // Use roleId
                            if (!resultSet.wasNull()) {
                                if (roleId >= 0 && roleId < Role.values().length) {
                                    value = Role.values()[roleId];
                                } else {
                                    throw new IllegalArgumentException("Invalid role ID: " + roleId);
                                }
                            }
                        } else if (field.getType() == Etat.class) {
                            int etatId = resultSet.getInt("etat"); // Ensure to use the correct column name
                            if (!resultSet.wasNull()) {
                                if (etatId >= 0 && etatId < Etat.values().length) {
                                    value = Etat.values()[etatId];
                                } else {
                                    throw new IllegalArgumentException("Invalid etat ID: " + etatId);
                                }
                            }
                        } else if (field.getType() == EtatDette.class) {
                            int etatDetteId = resultSet.getInt("etatid");
                            if (!resultSet.wasNull()) {
                                if (etatDetteId >= 0 && etatDetteId < EtatDette.values().length) {
                                    value = EtatDette.values()[etatDetteId];
                                } else {
                                    throw new IllegalArgumentException("Invalid etatDette ID: " + etatDetteId);
                                }
                            }
                        } else if (field.getType() == StateDette.class) {
                            int stateDetteId = resultSet.getInt("stateid");
                            if (!resultSet.wasNull()) {
                                if (stateDetteId >= 0 && stateDetteId < StateDette.values().length) {
                                    value = StateDette.values()[stateDetteId];
                                } else {
                                    throw new IllegalArgumentException("Invalid stateDette ID: " + stateDetteId);
                                }
                            }
                        } else {
                            // Handle other enum types if necessary
                            String enumValue = resultSet.getString(columnName);
                            if (enumValue != null) {
                                value = Enum.valueOf((Class<Enum>) field.getType(), enumValue);
                            }
                        }
                    } else if (field.getType() == User.class) {
                        if (field.getName().equals("userCreate")) {
                            int userCreateId = resultSet.getInt("usercreate"); // Ensure the case matches
                            if (!resultSet.wasNull()) {
                                User userCreate = new User();
                                userCreate.setId(userCreateId);
                                value = userCreate;
                            }
                        } else if (field.getName().equals("userUpdate")) {
                            int userUpdateId = resultSet.getInt("userupdate"); // Ensure the case matches
                            if (!resultSet.wasNull()) {
                                User userUpdate = new User();
                                userUpdate.setId(userUpdateId);
                                value = userUpdate;
                            }
                        } else if (field.getName().equals("user")) {
                            int userId = resultSet.getInt("userId");
                            if (!resultSet.wasNull()) {
                                User user = new User();
                                user.setId(userId);
                                value = user;
                            } // Ensure the case matches
                        }
                    } else if (field.getType() == Client.class) {

                        if (!excludeFields.contains("client")) {
                            try {
                                int clientId = resultSet.getInt("clientid");
                                if (!resultSet.wasNull()) {
                                    Client client = new Client();
                                    client.setId(clientId);
                                    value = client;
                                }
                            } catch (SQLException e) {
                                System.err.println("Error retrieving client ID: " + e.getMessage());
                            }
                        }

                    } else if (field.getType() == LocalDateTime.class) {
                        Timestamp timestamp = resultSet.getTimestamp(columnName);
                        value = (timestamp != null) ? timestamp.toLocalDateTime() : null;
                    } else if (field.getType() == java.util.Date.class) {
                        Timestamp timestamp = resultSet.getTimestamp(columnName);
                        value = (timestamp != null) ? new java.util.Date(timestamp.getTime()) : null;
                    } else {
                        System.out.println("Unsupported type for field: " + field.getName());
                    }
                } catch (SQLException e) {
                    System.err.println(
                            "Error retrieving field " + field.getName() + " from ResultSet: " + e.getMessage());
                }

                // Check for null values before setting primitive types
                if (value == null && field.getType().isPrimitive()) {
                    throw new IllegalArgumentException(
                            "Can not set " + field.getType() + " field " + field.getName() + " to null value");
                }

                // Set the value to the entity's field
                try {
                    field.set(entity, value);
                } catch (IllegalAccessException e) {
                    throw new SQLException("Failed to set field value for field: " + field.getName(), e);
                }
            }
        }

        return entity; // Return the populated entity
    }

}