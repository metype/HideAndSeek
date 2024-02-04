package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import com.metype.hidenseek.Utilities.PluginStorage;
import de.themoep.inventorygui.*;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameCommand implements CommandExecutor {

    public static final HashMap<UUID, String> playersEditingGames = new HashMap<>();

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        StringBuilder argsString = new StringBuilder();
        for(String arg : args) {
            argsString.append(arg).append(" ");
        }
        List<String> newArgs = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(argsString);
        while (m.find())
            newArgs.add(m.group(1).replace("\"", ""));

        args = newArgs.toArray(args);

        if(args.length == 0) return true;
        if(args[0].equalsIgnoreCase("start")) {
            if(!sender.hasPermission("hns.game.start")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length < 3) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return StartGame(sender, args[1], args[2]);
        }
        if(args[0].equalsIgnoreCase("stop")) {
            if(!sender.hasPermission("hns.game.stop")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length < 2) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return StopGame(sender, args[1]);
        }
        if(args[0].equalsIgnoreCase("create")) {
            if(!sender.hasPermission("hns.game.create")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length <= 2) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return CreateGame(sender, args[1], args[2]);
        }
        if(args[0].equalsIgnoreCase("delete")) {
            if(!sender.hasPermission("hns.game.delete")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length == 1) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return DeleteGame(args[1]);
        }
        if(args[0].equalsIgnoreCase("set_start")) {
            if(!sender.hasPermission("hns.game.set_start")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length == 1) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return SetStart(sender, args[1]);
        }
        if(args[0].equalsIgnoreCase("edit")) {
            if(!sender.hasPermission("hns.game.edit")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }

            if(args.length >= 2) {
                return EditGame(sender, args[1]);
            } else {
                return EditGame(sender, null);
            }
        }
        return false;
    }

    private boolean DeleteGame(String gameKey) {
        GameManager.DeleteGame(gameKey);
        return true;
    }

    private boolean StartGame(CommandSender sender, String gameKey, String timeStr) {
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        try {
            float timeVal = Float.parseFloat(timeStr.replaceAll("[^0-9.]", ""));
            String timeMult = timeStr.replaceAll("[0-9.]", "");

            char c = timeStr.charAt(0);
            if(c >= '0' && c <= '9') {
                int gameStartTime;
                if(timeMult.isEmpty()) {
                    gameStartTime = (int)timeVal;
                } else {
                    gameStartTime = switch (timeMult.charAt(0)) {
                        case 's', 'S' -> (int) timeVal;
                        case 'm', 'M' -> (int) (timeVal * 60);
                        case 'h', 'H' -> (int) (timeVal * 60 * 60);
                        default -> 0;
                    };
                }

                GameManager.StartGame(gameKey, gameStartTime);
            } else {
                throw new NumberFormatException();
            }
        } catch(NumberFormatException e) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_number", gameKey));
            return false;
        }
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.start", game.props.gameName, timeStr));
        return true;
    }

    private boolean StopGame(CommandSender sender, String gameKey) {
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        GameManager.EndGame(gameKey);
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.stop", game.props.gameName));
        return true;
    }

    private boolean CreateGame(CommandSender sender, String gameKey, String gameName) {
        if(!gameKey.replaceAll("([\0-/]|[:-@]|[\\[-`]|[{-\177])", "_").equals(gameKey)) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.command.game.create.bad_key", gameKey));
        }
        Game newGame = new Game();
        newGame.props.gameName = gameName;

        GameManager.NewGame(gameKey, newGame);
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.create", gameKey, newGame.props.gameName));
        return true;
    }

    private boolean SetStart(CommandSender sender, String gameKey) {
        if(!(sender instanceof Player player)) return false;
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        game.startGameLocation = player.getLocation();
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.set_start", game.props.gameName));
        return true;
    }

    private boolean EditGame(CommandSender sender, String gameKey) {
        PluginStorage.playersInHNSUI.add(((Player)sender).getUniqueId());
        if(gameKey == null) {
            List<String> games = GameManager.GetGames();
            InventoryGui gui = SetUpPagedGUI(1, (Player)sender, "Game List", false);
            GuiElementGroup group = new GuiElementGroup('g');
            for (String game : games) {
                Game gameOption = GameManager.GetGame(game);
                if(gameOption == null) continue;
                String name = gameOption.props.gameName;
                group.addElement(new DynamicGuiElement('e', (viewer) -> new StaticGuiElement('e', new ItemStack(gameOption.props.displayMaterial), GameCommand::OnGameToEditSelected, name.isEmpty() ? game : name, game)));
            }
            gui.addElement(group);
            gui.setCloseAction((close) -> {
                PluginStorage.playersInHNSUI.remove(close.getPlayer().getUniqueId());
                return true;
            });
            FinishPreparingPagedGUI(gui);
            gui.show((((Player) sender).getPlayer()));
        } else {
            Game game = GameManager.GetGame(gameKey);
            OpenGameEditGUI((Player)sender, game);
        }
        return true;
    }

    private static boolean OnGameToEditSelected(GuiElement.Click clickEvent) {
        InventoryClickEvent event = (InventoryClickEvent)clickEvent.getRawEvent();
        ItemStack itemOnCursor = event.getCurrentItem();
        if(itemOnCursor == null) return true;
        ItemMeta itemOnCursorMeta = itemOnCursor.getItemMeta();
        if(itemOnCursorMeta == null) return true;
        Game game = GameManager.GetGame(Objects.requireNonNull(itemOnCursorMeta.getLore()).get(0));
        if(game == null) {
            return true;
        }
        OpenGameEditGUI(clickEvent.getWhoClicked(), game);
        return true;
    }

    private static void OpenGameEditGUI(HumanEntity who, Game gameToEdit) {
        playersEditingGames.put(who.getUniqueId(), GameManager.GetKey(gameToEdit));
        ArrayList<Field> gameFields = Game.GetProperties();
        InventoryGui gui = SetUpPagedGUI(2, who, "Game Info", true, GameCommand::OnSaveGameSelected);
        GuiElementGroup group = new GuiElementGroup('g');
        for (Field field : gameFields) {
                group.addElement(GetDynamicElementForField('e', field, gameToEdit, GameCommand::OnSelectFieldToEdit));
        }
        gui.addElement(group);
        FinishPreparingPagedGUI(gui);
        gui.setCloseAction((close) -> {
            PluginStorage.playersInHNSUI.remove(close.getPlayer().getUniqueId());
            return true;
        });
        gui.show(who);
    }

    private static boolean OnSelectFieldToEdit(GuiElement.Click clickEvent) {
        InventoryClickEvent event = (InventoryClickEvent)clickEvent.getRawEvent();
        ItemStack itemOnCursor = event.getCurrentItem();
        if(itemOnCursor == null) {
            System.out.println("itemOnCursor is null");
            return true;
        }
        ItemMeta itemOnCursorMeta = itemOnCursor.getItemMeta();
        if(itemOnCursorMeta == null) {
            System.out.println("itemOnCursorMeta is null");
            return true;
        }
        String gameKey = playersEditingGames.get(clickEvent.getWhoClicked().getUniqueId());
        if(gameKey == null) {
            System.out.println("gameKey is null");
            return true;
        }
        Game editingGame = GameManager.GetGame(gameKey);
        if(editingGame == null) {
            System.out.println("editingGame is null");
            return true;
        }
        try {
            String fieldName = Objects.requireNonNull(itemOnCursorMeta.getLore()).get(0).split("=")[0];
            Field field = editingGame.props.getClass().getField(fieldName);
            Object val = field.get(editingGame.props);
            if(val instanceof Boolean boolVal) {
                field.setBoolean(editingGame.props, !boolVal);
                clickEvent.getGui().draw();
                return true;
            }
            if(val instanceof Integer) {
                CreateAndShowIntModificationGui(clickEvent.getWhoClicked(), field, fieldName.equalsIgnoreCase("minHeightBounds"));
            }
            if(val instanceof Material) {
                InventoryClickEvent rawClickEvent = ((InventoryClickEvent) clickEvent.getRawEvent());
                Material desiredNewMat = Objects.requireNonNull(rawClickEvent.getCursor()).getType();
                if(desiredNewMat.isAir()) return true;
                field.set(editingGame.props, desiredNewMat);
                clickEvent.getGui().draw();
            }
            if(val instanceof String stringVal) {
                clickEvent.getGui().close(true);
                AnvilGUI.Builder builder = new AnvilGUI.Builder();
                builder.itemLeft(new ItemStack(Material.NAME_TAG));
                builder.text(stringVal);
                builder.itemRight(new ItemStack(Material.NAME_TAG));
                builder.onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    return List.of(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> {
                                try {
                                    field.set(editingGame.props, stateSnapshot.getText());
                                } catch (IllegalAccessException ignore) {
                                }
                            })
                    );
                });
                builder.onClose(stateSnapshot -> OpenGameEditGUI(clickEvent.getWhoClicked(), editingGame));
                builder.plugin(JavaPlugin.getPlugin(com.metype.hidenseek.Main.class));
                builder.open((Player)clickEvent.getWhoClicked());
            }
        } catch(IllegalAccessException | NoSuchFieldException e) {
            System.out.println(e);
            return true;
        }
        return true;
    }

    private static void CreateAndShowIntModificationGui(HumanEntity viewer, Field field, boolean allowNegative) {
        String[] rows = new String[]{
            "         ",
            "abc d efg",
            "    h    "
        };
        Game editingGame = GameManager.GetGame(playersEditingGames.get(viewer.getUniqueId()));
        if(editingGame == null) return;
        try {
            AtomicInteger fieldVal = new AtomicInteger(field.getInt(editingGame.props));
            InventoryGui gui = new InventoryGui(JavaPlugin.getPlugin(com.metype.hidenseek.Main.class), viewer, ChatColor.DARK_GREEN + fromJavaIdentifierToDisplayableString(field.getName()), rows);
            gui.addElement(new DynamicGuiElement('a', (who) -> new StaticGuiElement('a', new ItemStack(fieldVal.get() - 100 < 0 && !allowNegative ? Material.BARRIER : Material.RED_STAINED_GLASS_PANE), click -> {
                fieldVal.addAndGet(-100);
                if(!allowNegative) fieldVal.set(Math.max(fieldVal.get(), 0));
                try {
                    field.set(editingGame.props, fieldVal.get());
                } catch (IllegalAccessException ignored) {}
                click.getGui().draw();
                return true;
            }, ChatColor.WHITE + "-100", fieldVal.get() - 100 < 0 && !allowNegative ? MessageManager.GetMessageByKey("error.value_no_negative") : fieldVal.get() - 100 + "")));
            gui.addElement(new DynamicGuiElement('b', (who) -> new StaticGuiElement('a', new ItemStack(fieldVal.get() - 10 < 0 && !allowNegative ? Material.BARRIER : Material.RED_STAINED_GLASS_PANE), click -> {
                fieldVal.addAndGet(-10);
                if(!allowNegative) fieldVal.set(Math.max(fieldVal.get(), 0));
                try {
                    field.set(editingGame.props, fieldVal.get());
                } catch (IllegalAccessException ignored) {}
                click.getGui().draw();
                return true;
            }, ChatColor.WHITE + "-10", fieldVal.get() - 10 < 0 && !allowNegative ? MessageManager.GetMessageByKey("error.value_no_negative") : fieldVal.get() - 10 + "")));
            gui.addElement(new DynamicGuiElement('c', (who) -> new StaticGuiElement('a', new ItemStack(fieldVal.get() - 1 < 0 && !allowNegative ? Material.BARRIER : Material.RED_STAINED_GLASS_PANE), click -> {
                fieldVal.addAndGet(-1);
                if(!allowNegative) fieldVal.set(Math.max(fieldVal.get(), 0));
                try {
                    field.set(editingGame.props, fieldVal.get());
                } catch (IllegalAccessException ignored) {}
                click.getGui().draw();
                return true;
            }, ChatColor.WHITE + "-1", fieldVal.get() - 1 < 0 && !allowNegative ? MessageManager.GetMessageByKey("error.value_no_negative") : fieldVal.get() - 1 + "")));
            gui.addElement(new DynamicGuiElement('e', (who) -> new StaticGuiElement('a', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                fieldVal.addAndGet(1);
                try {
                    field.set(editingGame.props, fieldVal.get());
                } catch (IllegalAccessException ignored) {}
                click.getGui().draw();
                return true;
            }, "+1", fieldVal.get() + 1 + "")));
            gui.addElement(new DynamicGuiElement('f', (who) -> new StaticGuiElement('a', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                fieldVal.addAndGet(10);
                try {
                    field.set(editingGame.props, fieldVal.get());
                } catch (IllegalAccessException ignored) {}
                click.getGui().draw();
                return true;
            }, "+10", fieldVal.get() + 10 + "")));
            gui.addElement(new DynamicGuiElement('g', (who) -> new StaticGuiElement('a', new ItemStack(Material.LIME_STAINED_GLASS_PANE), click -> {
                fieldVal.addAndGet(100);
                try {
                    field.set(editingGame.props, fieldVal.get());
                } catch (IllegalAccessException ignored) {}
                click.getGui().draw();
                return true;
            }, "+100", fieldVal.get() + 100 + "")));


            gui.addElement(GetDynamicElementForField('d', field, editingGame, (click)->true));

            gui.addElement(new StaticGuiElement('h', new ItemStack(Material.ARROW), click -> {
                click.getGui().close(false);
                return true;
            }, "Back"));

            gui.show(viewer);

        } catch (IllegalAccessException ignored) {

        }
    }

    private static DynamicGuiElement GetDynamicElementForField(char slot, Field field, Game game, GuiElement.Action action) {
        return new DynamicGuiElement(slot, (who) -> {
            try {
                Object val = field.get(game.props);
                return new StaticGuiElement('a', GetMaterialForField(field, game), action, ChatColor.WHITE + fromJavaIdentifierToDisplayableString(field.getName()), field.getName() + "=" + val);
            } catch(IllegalAccessException e) {
                return new StaticGuiElement('e', new ItemStack(Material.BARRIER), field.getName(), ChatColor.RED + "Inaccessible.");
            }
        });
    }

    private static ItemStack GetMaterialForField(Field field, Game game) throws IllegalAccessException {
        Object val = field.get(game.props);
        if(val instanceof Boolean boolVal) {
            if(boolVal) {
                return new ItemStack(Material.LIME_WOOL);
            } else {
                return new ItemStack(Material.RED_WOOL);
            }
        }
        if(val instanceof String) {
            return new ItemStack(Material.NAME_TAG);
        }
        if(val instanceof Integer) {
            return new ItemStack(Material.COMMAND_BLOCK);
        }
        if(val instanceof Material materialVal) {
            return new ItemStack(materialVal);
        }
        return new ItemStack(Material.GOLD_BLOCK);
    }

    private static boolean OnSaveGameSelected(GuiElement.Click clickEvent) {
        GameManager.SaveGames();
        return true;
    }

    @SuppressWarnings("SameParameterValue")
    private static InventoryGui SetUpPagedGUI(int rowCount, HumanEntity viewer, String title, boolean includeBackButton) {
        return SetUpPagedGUI(rowCount, viewer, title, includeBackButton, null);
    }

    private static InventoryGui SetUpPagedGUI(int rowCount, HumanEntity viewer, String title, boolean includeBackButton, GuiElement.Action action) {
        String[] rows = new String[rowCount + 2];
        for(int i=0;i < rowCount;i++) rows[i] = "ggggggggg";
        rows[rowCount] = "         ";
        var builder = new StringBuilder("p       n");
        if(includeBackButton) builder.setCharAt(4, 'b');
        if(action != null) builder.setCharAt(5, 's');
        rows[rowCount + 1] = builder.toString();
        var gui = new InventoryGui(JavaPlugin.getPlugin(com.metype.hidenseek.Main.class), viewer, title, rows);
        gui.addElement(new StaticGuiElement('b', new ItemStack(Material.ARROW), click -> {
            click.getGui().close(false);
            return true;
        }, "Back"));
        gui.addElement(new StaticGuiElement('s', new ItemStack(Material.STRUCTURE_BLOCK), action, "Save Changes"));
        return gui;
    }

    private static void FinishPreparingPagedGUI(InventoryGui gui) {
        // Previous page
        gui.addElement(new GuiPageElement('p', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));

        // Next page
        gui.addElement(new GuiPageElement('n', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));
        gui.setFiller(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
    }

    private static String[] splitByCapitalLetters(String input) {
        return input.split("(?=\\p{Upper})");
    }

    private static String fromJavaIdentifierToDisplayableString(String input) {
        StringBuilder rv = new StringBuilder();
        for (String s : splitByCapitalLetters(input)) rv.append(s).append(" ");
        rv = new StringBuilder(rv.toString().trim());
        if (Character.isLowerCase(rv.charAt(0)))
            rv = new StringBuilder(Character.toUpperCase(rv.charAt(0)) + rv.substring(1, rv.length()));
        return rv.toString();
    }
}
