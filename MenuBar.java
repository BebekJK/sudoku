import javax.swing.*;

public class MenuBar {
    JMenuBar menubar = new JMenuBar();
    JMenu fileMenu, gameMenu, helpMenu;
    JMenuItem newGameItem, restartGameItem, exitGameItem;
    JMenuItem toggleThemeItem, toggleSoundItem, getHintItem;
    JMenuItem instructionsItem, aboutItem;

    public MenuBar() {
        fileMenu = new JMenu("File");
        gameMenu = new JMenu("Game");
        helpMenu = new JMenu("Help");

        // File Menu : new game, restart game, exit game
        newGameItem = new JMenuItem("New Game");
        restartGameItem = new JMenuItem("Reset");
        fileMenu.add(newGameItem);
        fileMenu.add(restartGameItem);
        
        // Game Menu : toggle theme, toggle music, get hint
        toggleThemeItem = new JMenuItem("Change Theme");
        toggleSoundItem = new JMenuItem("Disable Sound");
        getHintItem = new JMenuItem("Get Hint");
        gameMenu.add(toggleThemeItem);
        gameMenu.add(toggleSoundItem);
        gameMenu.add(getHintItem);

        // Help Menu : instructions, about
        instructionsItem = new JMenuItem("Instructions");
        aboutItem = new JMenuItem("About");
        helpMenu.add(instructionsItem);
        helpMenu.add(aboutItem);

        menubar.add(fileMenu);
        menubar.add(gameMenu);
        menubar.add(helpMenu);
    }
}
