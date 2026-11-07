package ru.sticks.server;

import ru.sticks.common.GameState;
import ru.sticks.common.StickGame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class StickGameImpl extends UnicastRemoteObject implements StickGame {

    private final GameState state;
    private final int totalLines;
    private int linesDrawn = 0;

    public StickGameImpl(int size) throws RemoteException {
        super();
        state = new GameState(size);
        totalLines = 2 * size * (size + 1);
    }

    @Override
    public synchronized int join(String name) throws RemoteException {
        if (state.names[1] == null) {
            state.names[1] = name;
            state.message = name + " подключился. Ждём второго игрока...";
            state.version++;
            System.out.println("Подключился игрок 1: " + name);
            return 1;
        }
        if (state.names[2] == null) {
            state.names[2] = name;
            state.started = true;
            state.message = "Игра началась! Ходит " + state.names[1];
            state.version++;
            System.out.println("Подключился игрок 2: " + name + ". Игра началась.");
            return 2;
        }
        return -1;
    }

    @Override
    public synchronized GameState getState() throws RemoteException {
        return state.copy();
    }

    @Override
    public synchronized String makeMove(int player, char type, int row, int col) throws RemoteException {
        if (!state.started) return "Игра ещё не началась";
        if (state.gameOver) return "Игра уже окончена";
        if (player != state.currentPlayer) return "Сейчас не ваш ход";

        int n = state.size;
        type = Character.toLowerCase(type);

        if (type == 'h') {
            if (row < 0 || row > n || col < 0 || col >= n) return "Нет такой горизонтальной палочки";
            if (state.hor[row][col]) return "Эта палочка уже нарисована";
            state.hor[row][col] = true;
        } else if (type == 'v') {
            if (row < 0 || row >= n || col < 0 || col > n) return "Нет такой вертикальной палочки";
            if (state.ver[row][col]) return "Эта палочка уже нарисована";
            state.ver[row][col] = true;
        } else {
            return "Тип палочки должен быть h или v";
        }
        linesDrawn++;

        int closed = 0;
        if (type == 'h') {
            closed += tryClose(row - 1, col, player);
            closed += tryClose(row, col, player);
        } else {
            closed += tryClose(row, col - 1, player);
            closed += tryClose(row, col, player);
        }

        String me = state.names[player];
        System.out.println(me + ": " + type + " " + row + " " + col + " (закрыто клеток: " + closed + ")");


        if (linesDrawn == totalLines) {
            state.gameOver = true;
            int s1 = state.score[1], s2 = state.score[2];
            if (s1 > s2) state.message = "Игра окончена! Победил " + state.names[1];
            else if (s2 > s1) state.message = "Игра окончена! Победил " + state.names[2];
            else state.message = "Игра окончена! Ничья";
            System.out.println(state.message);
        } else if (closed > 0) {
            state.message = me + " закрыл клеток: " + closed + " и ходит ещё раз";
        } else {
            state.currentPlayer = 3 - player;
            state.message = me + " сделал ход. Ходит " + state.names[state.currentPlayer];
        }

        state.version++;
        return "OK";
    }

    private int tryClose(int r, int c, int player) {
        int n = state.size;
        if (r < 0 || r >= n || c < 0 || c >= n) return 0;
        if (state.owner[r][c] != 0) return 0;
        boolean full = state.hor[r][c] && state.hor[r + 1][c]
                && state.ver[r][c] && state.ver[r][c + 1];
        if (!full) return 0;
        state.owner[r][c] = player;
        state.score[player]++;
        return 1;
    }
}