package ru.sticks.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StickGame extends Remote {

    int join(String name) throws RemoteException;

    GameState getState() throws RemoteException;

    String makeMove(int player, char type, int row, int col) throws RemoteException;
}
