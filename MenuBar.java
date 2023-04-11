

import javax.swing.*;


public class MenuBar{
    JMenuBar menubar = new JMenuBar();

    JMenuItem newGameItem, restartGameItem, exitGameItem;
    JMenuItem toggleThemeItem;
    JMenuItem instructionsItem, aboutItem;

    public MenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");
  
  
        newGameItem = new JMenuItem("New Game");
        restartGameItem = new JMenuItem("Reset");
        exitGameItem = new JMenuItem("Exit");
        fileMenu.add(newGameItem);
        fileMenu.add(restartGameItem);
        fileMenu.add(exitGameItem);
  
        toggleThemeItem = new JMenuItem("Change Theme");
        editMenu.add(toggleThemeItem);
  
        instructionsItem = new JMenuItem("Instructions");
        aboutItem = new JMenuItem("About");
        helpMenu.add(instructionsItem);
        helpMenu.add(aboutItem);
  
        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(helpMenu);
    }
}
