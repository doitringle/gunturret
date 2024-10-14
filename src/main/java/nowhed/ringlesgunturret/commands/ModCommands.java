package nowhed.ringlesgunturret.commands;

// getString(ctx, "string")
// word()
// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything in the CommandManager

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;

import java.util.Arrays;

public class ModCommands {


    public static void registerModCommands() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("rgt")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(ctx -> {
                        returnText(ctx,"This is a debug command for Ringle's Gun Turret");
                        return 1;
                    })

                            .then(literal("uuidBook")
                                    .executes(ctx -> {

                                        if(ctx.getSource().isExecutedByPlayer()){
                                            PlayerEntity executor = ctx.getSource().getPlayer();

                                            String text = ctx.getSource().getPlayer().getUuidAsString();

                                            String command = "give " + executor.getName().getString()
                                                    + " writable_book{pages:[\"" + text + "\"]} 1";
                                            // kinda hacky

                                            ctx.getSource().getServer().getCommandManager()
                                                    .executeWithPrefix(executor.getCommandSource(), command);


                                        } else {
                                            returnText(ctx,"this can only be run by a player");
                                            return 0;
                                        }

                                        return 1;
                                    }))

                        .then(literal("getPlayerData")
                                .executes(ctx -> getPlayerData(ctx,ctx.getSource().getPlayer()))
                            .then(argument("player name", EntityArgumentType.player())
                            .executes(ctx -> {
                                //user input a player
                                PlayerEntity executor = EntityArgumentType.getPlayer(ctx,"player name");
                                return getPlayerData(ctx, executor);
                            })))

                    .then(literal("setPlayerData")
                            .then(argument("player name", EntityArgumentType.player())
                                    .then(argument("targetSelection", StringArgumentType.word())
                                            .then(argument("blacklist", BoolArgumentType.bool())
                                                .then(argument("player list",StringArgumentType.greedyString())
                            .executes(ctx -> {
                                PlayerEntity selectedPlayer = EntityArgumentType.getPlayer(ctx,"player name");
                                String targetSelection = StringArgumentType.getString(ctx, "targetSelection");
                                boolean blacklist = BoolArgumentType.getBool(ctx,"blacklist");
                                String playerList = StringArgumentType.getString(ctx, "player list").toLowerCase().replaceAll("\\s","");

                                String[] validSelections = {"all","hostiles","onlyplayers","disable"};
                                if(!Arrays.stream(validSelections).toList().contains(targetSelection)) {
                                    returnText(ctx, "targetSelection \"" + targetSelection + "\" is not valid");
                                    returnText(ctx, "valid options: " + Arrays.toString(validSelections));
                                    return 0;
                                }
                                return setPlayerData(ctx, selectedPlayer, targetSelection, blacklist, playerList);
                            }))))))
                            .then(literal("infiniteArrows")
                                    .executes(ctx -> {
                                        returnText(ctx,"infiniteArrows currently: " + GunTurretBlockEntity.infiniteArrows);
                                        return 1;
                                    })
                                    .then(argument("all turrets have infinite ammo", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                GunTurretBlockEntity.infiniteArrows =
                                                        BoolArgumentType.getBool(ctx,"all turrets have infinite ammo");
                                                returnText(ctx,"Set to: " + GunTurretBlockEntity.infiniteArrows);
                                                return 1;
                                            })))
                            .then(literal("setPredictionMultiplier")
                                .executes(ctx -> {
                                    returnText(ctx,"prediction multiplier currently: " + GunTurretBlockEntity.predictionMultiplier);
                                    return 1;
                                })
                                .then(argument("prediction multiplier", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> {
                                            GunTurretBlockEntity.predictionMultiplier =
                                                    DoubleArgumentType.getDouble(ctx,"prediction multiplier");
                                            returnText(ctx,"Set to: " + GunTurretBlockEntity.predictionMultiplier);
                                            return 1;
                                        })))
                    );

        }));
    }

    public static int setPlayerData(CommandContext<ServerCommandSource> ctx,PlayerEntity player, String targetSelection, boolean blacklist, String playerList) {
        if(player == null) {
            returnText(ctx, "Player doesn't exist or is null");
            return 0;
        }
        MinecraftServer server = ctx.getSource().getServer();
        if(server.getWorld(World.OVERWORLD) == null) return 0;
        PlayerData playerState = StateSaver.getPlayerState(player, server.getWorld(World.OVERWORLD));
        if(playerState == null) {
            returnText(ctx, "Could not find playerdata for " + player.getName() + " to change");
            return 0;
        }
        playerState.targetSelection = targetSelection;
        playerState.blacklist = blacklist;
        playerState.playerList = playerList;

        return 1;

    }

    public static int getPlayerData(CommandContext<ServerCommandSource> ctx,PlayerEntity player) {
        if(player == null) {
            returnText(ctx, "Player doesn't exist or is null");
            return 0;
        }
        MinecraftServer server = ctx.getSource().getServer();
        if(server.getWorld(World.OVERWORLD) == null) return 0;
        PlayerData playerState = StateSaver.getPlayerState(player, server.getWorld(World.OVERWORLD));
        if(playerState == null) {
            returnText(ctx, "No player data for " + player.getName() + " found");
            return 0;
        } else {
            returnText(ctx, "targetSelection: " + playerState.targetSelection);
            returnText(ctx, "playerList: " + playerState.playerList);
            returnText(ctx, "blacklist: " + playerState.blacklist);
            return 1;
        }
    }

    public static void returnText(CommandContext<ServerCommandSource> ctx, String text) {
        ctx.getSource().sendFeedback(() -> Text.literal(text), false);
    }

    /*                            .then(literal("setPredictionIterations")
                                    .executes(ctx -> {
                                        returnText(ctx,"prediction iterations currently: " + GunTurretBlockEntity.predictionIterations);
                                        return 1;
                                    })
                                    .then(argument("prediction iterations", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        GunTurretBlockEntity.predictionIterations =
                                                IntegerArgumentType.getInteger(ctx,"prediction iterations");
                                        returnText(ctx,"Set to: " + GunTurretBlockEntity.predictionIterations);
                                        return 1;
                                    })))*/
}
