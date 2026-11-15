package ru.sticks.client;

import ru.sticks.common.GameState;
import ru.sticks.common.StickGame;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        Scanner in = new Scanner(System.in);

        Registry registry = LocateRegistry.getRegistry(host, port);
        StickGame game = (StickGame) registry.lookup("StickGame");

        System.out.print("Введите имя: ");
        String name = in.nextLine().trim();
        int me = game.join(name);
        if (me == -1) {
            System.out.println("В игре уже два игрока.");
            return;
        }
        System.out.println("Вы игрок номер " + me);
        System.out.println("Формат хода: h строка столбец  - горизонтальная палочка вправо от точки");
        System.out.println("             v строка столбец  - вертикальная палочка вниз от точки");

        AtomicBoolean running = new AtomicBoolean(true);
        Object outputLock = new Object();

        Thread inputThread = new Thread(() -> readMoves(in, game, me, running, outputLock));

        inputThread.setDaemon(true);
        inputThread.start();

        int lastVersion = -1;
        try {
            while(running.get()) {
                GameState s = game.getState();
                if (s.version != lastVersion) {
                    synchronized (outputLock) {
                        s.print();
                        if (s.started && !s.gameOver && s.currentPlayer == me) {
                            System.out.println("Ваш ход:");
                        }
                    }
                    lastVersion = s.version;
                }
                if (s.gameOver) {
                    break;
                }
                Thread.sleep(200);
            }
        } finally {
            running.set(false);
        }

    }

    private static void readMoves(Scanner in, StickGame game, int me,
                                  AtomicBoolean running, Object outputLock) {
        try {
            while (running.get() && in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (!running.get()) {
                    break;
                }
                String[] parts = line.split("\\s+");
                if (parts.length != 3 || parts[0].length() != 1) {
                    synchronized (outputLock) {
                        System.out.println("Нужно ввести три значения, например: h 0 1");
                    }
                    continue;
                }
                try {
                    char type = parts[0].charAt(0);
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    String result = game.makeMove(me, type, row, col);
                    if (!result.equals("OK")) {
                        synchronized (outputLock) {
                            System.out.println("Ошибка: " + result);
                        }
                    }
                } catch (NumberFormatException e) {
                    synchronized (outputLock) {
                        System.out.println("Строка и столбец должны быть числами");
                    }
                }
            }
        } catch (RemoteException e) {
            synchronized (outputLock) {
                System.out.println("Ошибка связи с сервером: " + e.getMessage());
            }
        } finally {
            running.set(false);
        }
    }
}
