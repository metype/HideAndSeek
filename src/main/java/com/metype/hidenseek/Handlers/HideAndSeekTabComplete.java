package com.metype.hidenseek.Handlers;

import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Utilities.CommandTree;
import com.metype.hidenseek.Utilities.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class HideAndSeekTabComplete implements TabCompleter {

    private final CommandTree hideAndSeekCommandTree = new CommandTree("hns");

    public HideAndSeekTabComplete() {
        CommandTree gameCommand = new CommandTree("game");

        CommandTree gameStartCommand = new CommandTree("start");

        CommandTree gameTimeSubcommand = new CommandTree(GameManager::GetGames);
        gameTimeSubcommand.SetArgs("30s", "1m", "5m");


        gameStartCommand.AddSubCommands(gameTimeSubcommand);
        gameCommand.AddSubCommands(gameStartCommand);

        CommandTree gameSetStartSubcommand = new CommandTree("set_start");
        gameSetStartSubcommand.SetArgsFunction(GameManager::GetGames);
        gameCommand.AddSubCommands(gameSetStartSubcommand);

        CommandTree gameStopCommand = new CommandTree("stop");
        gameStopCommand.SetArgsFunction(GameManager::GetGames);
        gameCommand.AddSubCommands(gameStopCommand);

        CommandTree gameCreateCommand = new CommandTree("create");
        CommandTree gameCreateNameCommand = new CommandTree("*");
        gameCreateNameCommand.SetArgs("\"\"");
        gameCreateCommand.AddSubCommands(gameCreateNameCommand);
        gameCommand.AddSubCommands(gameCreateCommand);

        CommandTree gameEditCommand = new CommandTree("edit");
        CommandTree gameEditNameCommand = new CommandTree(GameManager::GetGames);
        gameEditNameCommand.SetArgsFunction(Game::GetPropertyNames);
        gameEditCommand.AddSubCommands(gameEditNameCommand);
        gameCommand.AddSubCommands(gameEditCommand);

        CommandTree setBoundsCommand = new CommandTree("set_bounds");
        setBoundsCommand.SetArgsFunction(GameManager::GetGames);

        CommandTree saveCommand = new CommandTree("save");

        CommandTree reloadCommand = new CommandTree("reload");

        CommandTree helpCommand = getHelpCommandTree();

        CommandTree resetConfigCommand = new CommandTree("reset_config");
        resetConfigCommand.SetArgs("messages");

        CommandTree joinGameCommand = new CommandTree(() -> List.of(new String[]{"join", "join_game"}));
        joinGameCommand.SetArgsFunction(GameManager::GetJoinableGames);

        CommandTree leaveGameCommand = new CommandTree(() -> List.of(new String[]{"leave", "leave_game"}));

        hideAndSeekCommandTree.AddSubCommands(gameCommand,
                helpCommand,
                joinGameCommand,
                leaveGameCommand,
                reloadCommand,
                resetConfigCommand,
                saveCommand,
                setBoundsCommand
        );
    }

    private static CommandTree getHelpCommandTree() {
        CommandTree helpCommand = new CommandTree("help");

        CommandTree helpPageCommand = new CommandTree("page");
        helpPageCommand.SetArgs("1","2","3","4","5");
        CommandTree helpGameCommand = new CommandTree("game");
        helpGameCommand.SetArgs("1","2","3","4","5");
        CommandTree helpSaveCommand = new CommandTree("save");
        helpSaveCommand.SetArgs("1","2","3","4","5");
        CommandTree helpReloadCommand = new CommandTree("reload");
        helpReloadCommand.SetArgs("1","2","3","4","5");

        helpCommand.AddSubCommands(helpPageCommand, helpGameCommand, helpSaveCommand, helpReloadCommand);
        return helpCommand;
    }


    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String commandLabel, String[] args) {
        List<String> list = new ArrayList<>();
        CommandTree curCommandTree = hideAndSeekCommandTree;
        int commandIndex = 0;
        boolean foundNoMatch = false;
        for (String arg : args) {
            boolean foundMatch = false;
            for (CommandTree tree : curCommandTree.subcommands) {
                for(String potentialName : tree.commandNameLambda.get()) {
                    if (potentialName.equalsIgnoreCase(arg) || potentialName.equalsIgnoreCase("*")) {
                        curCommandTree = tree;
                        commandIndex++;
                        foundMatch = true;
                        break;
                    }
                }
            }
            if(!foundMatch) foundNoMatch = true;
        }

        // Prevents some odd behaviour, this only allows command hints to pop up when they're supposed to
        if(commandIndex != args.length-1) return new ArrayList<>();

        if(!foundNoMatch) {
            for(CommandTree tree : curCommandTree.subcommands) {
                list.addAll(tree.commandNameLambda.get());
            }
            if(curCommandTree.args != null) {
                list.addAll(curCommandTree.args.get());
            }
        } else {
            for(CommandTree tree : curCommandTree.subcommands) {
                for(String str : tree.commandNameLambda.get()) {
                    if(str.toUpperCase().startsWith(args[args.length-1].toUpperCase()))
                        list.add(str);
                }
            }
            if(curCommandTree.args != null) {
                for (String arg : curCommandTree.args.get()) {
                    if (arg.toUpperCase().startsWith(args[args.length - 1].toUpperCase()))
                        list.add(arg);
                }
            }
        }

        //Sorts the suggestions alphabetically, just a preference.
        list.sort(String::compareToIgnoreCase);

        return list;
    }
}
