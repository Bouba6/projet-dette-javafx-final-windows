module dette {
    requires javafx.controls;
    requires javafx.fxml;

    // requires org.hibernate; // Si tu utilises Hibernate
    requires java.sql; // Pour la connexion à la base de données
    requires lombok; // Pour Lombok
    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.databind; // Pour Jackson Databind
    requires com.fasterxml.jackson.dataformat.yaml; // Pour YAML
    requires org.yaml.snakeyaml;// Pour SnakeYAML
    requires java.persistence;
    requires javafx.base;

    // requires postgresql; // Pour PostgreSQL
    // requires mysql.connector; // Pour le connecteur MySQL

    opens detteproject to javafx.fxml, hibernate.entitymanager,
            java.persistence,
            org.yaml.snakeyaml,
            lombok, jbcrypt, java.sql; // Ouvre le package pour la réflexion
    opens detteproject.data.entities to org.hibernate.orm.core;
    opens detteproject.controller to javafx.fxml;

    exports detteproject.data.entities;
    exports detteproject; // Exporte ton package

}
