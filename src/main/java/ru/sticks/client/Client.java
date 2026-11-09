package ru.sticks.client;

import ru.sticks.common.GameState;
import ru.sticks.common.StickGame;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        Scanner in = new Scanner(System.in);

        Registry registry = LocateRegistry.getRegistry(host, port);
        StickGame game = (StickGame) registry.lookup("StickGame");

        System.out.println("Введите имя: ");
        String name = in.nextLine().trim();
        int me = game.join(name);
        if (me == -1) {
            System.out.println("В игре уже два игрока.");
            return;
        }
        System.out.println("Вы игрок номер " + me);
        System.out.println("Формат хода: h строка столбец  - горизонтальная палочка вправо от точки");
        System.out.println("             v строка столбец  - вертикальная палочка вниз от точки");

        int lastVersion = -1;
        while(true) {
            GameState s = game.getState();

            if (s.version != lastVersion) {
                s.print();
                lastVersion = s.version;
            }
            if (s.gameOver) {
                break;
            }

            if (s.started && s.currentPlayer == me) {
                System.out.println("Ваш ход: ");
                String[] parts = in.nextLine().trim().split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Нужно ввести три значения, например: h 0 1");
                    continue;
                }
                try {
                    char type = parts[0].charAt(0);
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    String result = game.makeMove(me, type, row, col);
                    if (!result.equals("OK")) {
                        System.out.println("Ошибка: " + result);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Строка и столбец должны быть числами");
                }
            } else {
                Thread.sleep(1000);
            }
        }
    }
}
