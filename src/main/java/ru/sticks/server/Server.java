package ru.sticks.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static Registry registry;
    private static StickGameImpl game;

    public static void main(String[] args) throws Exception {
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        int port =  args.length > 1 ? Integer.parseInt(args[1]) : 1099;

        // создание объекта игры
        game = new StickGameImpl(size);

        // запуск RMI
        registry = LocateRegistry.createRegistry(port);

        // регистрация объекта
        registry.rebind("StickGame", game);

        System.out.println("Сервер запущен. Поле " + size + "x" + size + ", порт " + port);
        System.out.println("Ожидание игроков...");
    }
}
