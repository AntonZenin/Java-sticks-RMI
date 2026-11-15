package ru.sticks.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

// список того, что клиент может попросить у сервера
// extends Remote для RMI, что методы этого интерфейса можно вызывать по сети
public interface StickGame extends Remote {

    int join(String name) throws RemoteException;

    GameState getState() throws RemoteException;

    String makeMove(int player, char type, int row, int col) throws RemoteException;
}
