package services;

import resources.CONFIG;
import services.database.ConnectionPool;

public class OnStartUp {
    public OnStartUp() {
        new CoolDownManager();
        new ConnectionPool();
        CONFIG.reload();
    }
}
