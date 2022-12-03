package me.dasfaust.vhadditions;

import me.dasfaust.vhadditions.config.VHAdditionsConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.config.ModConfig;
import java.util.ArrayList;
import java.util.List;

@Mod("vhadditions")
public class VHAdditions
{
    public class VaultEnteredEvent
    {
        public long time;
        public Entity entity;
        public List<String> commands;

        public VaultEnteredEvent(long time, Entity entity, List<String> commands)
        {
            this.time = time;
            this.entity = entity;
            this.commands = commands;
        }
    }

    private List<VaultEnteredEvent> vaultEnteredEvents;
    private List<Integer> readyVaultEnteredEvents;

    public VHAdditions()
    {
        vaultEnteredEvents = new ArrayList<>();
        readyVaultEnteredEvents = new ArrayList<>();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, VHAdditionsConfig.SPEC, "vhadditions.toml");
    }

    @SubscribeEvent
    public void onEntityTravelToDim(EntityTravelToDimensionEvent event)
    {
        Entity ent = event.getEntity();
        if (ent instanceof Player)
        {
            String displayName = ent.getDisplayName().getString();
            String dimName = event.getDimension().location().toString();
            if (dimName.startsWith("the_vault:vault"))
            {
                List<String> unformatted = VHAdditionsConfig.COMMANDS_ON_VAULT_ENTER.get();
                if (unformatted.size() > 0)
                {
                    List<String> commands = new ArrayList<>();
                    for (String cmd : unformatted)
                    {
                        String formatted = cmd.replace("{display_name}", displayName);
                        formatted.replace("{dim_name}", dimName);
                        commands.add(formatted);
                    }
                    vaultEnteredEvents.add(new VaultEnteredEvent(System.currentTimeMillis(), ent, commands));
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;

        for (int i = 0; i < vaultEnteredEvents.size(); i++)
        {
            VaultEnteredEvent e = vaultEnteredEvents.get(i);
            if (System.currentTimeMillis() - e.time >= 1000)
            {
                readyVaultEnteredEvents.add(i);
            }
        }

        for (int i : readyVaultEnteredEvents)
        {
            VaultEnteredEvent e = vaultEnteredEvents.get(i);
            for (String cmd : e.commands)
            {
                e.entity.getServer().getCommands().performCommand(e.entity.getServer().createCommandSourceStack(), cmd);
            }
            vaultEnteredEvents.remove(i);
        }
        readyVaultEnteredEvents.clear();
    }
}
