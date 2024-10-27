package detteproject.core;

import detteproject.data.entities.User;

public class UserConnected {

    public static User UserConnected;

    public static User getUserConnected() {
        return UserConnected;
    }

    public static void setUserConnected(User userConnected) {
        UserConnected = userConnected;
    }
}
