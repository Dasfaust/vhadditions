package me.dasfaust.vhadditions.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class VHAdditionsConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<String>> COMMANDS_ON_VAULT_ENTER;

    static
    {
        BUILDER.push("Vault Hunter Additions");

        COMMANDS_ON_VAULT_ENTER = BUILDER.comment(" Commands to run when a player enters a vault dimension\n Can contain {display_name} for the player's display name, and {dim_name} for the dimension name")
            .define("commands_on_vault_enter", Arrays.asList("/gamerule vaultCasualMode true", "/gamerule vaultAllowWaypoints true"));

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
