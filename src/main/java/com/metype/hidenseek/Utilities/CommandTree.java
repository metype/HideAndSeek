package com.metype.hidenseek.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandTree {
    public List<CommandTree> subcommands;
    public ArgsFunction args;
    public ArgsFunction commandNameLambda;

    public CommandTree(String parentCommandName) {
        this.subcommands = new ArrayList<>();
        this.args = null;
        this.commandNameLambda = () -> Arrays.stream(new String[]{parentCommandName}).toList();
    }

    public CommandTree(ArgsFunction nameLambda) {
        this.subcommands = new ArrayList<>();
        this.args = null;
        this.commandNameLambda = nameLambda;
    }

    public void AddSubCommands(CommandTree... commands) {
        this.subcommands.addAll(List.of(commands));
    }

    public void SetArgsFunction(ArgsFunction argsFunction) {
        if(this.args != null) {
            System.out.println("Possible Error! Args function for " + String.join(",", commandNameLambda.get()) + " has been set multiple times!");
        }
        this.args = argsFunction;
    }

    public void SetArgs(String... args) {
        if(this.args != null) {
            System.out.println("Possible Error! Args function for " + String.join(",", commandNameLambda.get()) + " has been set multiple times!");
        }
        this.args = () -> Arrays.stream(args).toList();
    }
}
