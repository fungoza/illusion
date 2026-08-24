package me.linstar.illusion.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class TransformDataCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//        dispatcher.register(Commands.literal("illusion").requires(stack -> stack.hasPermission(4))
//                .then(Commands.literal("transform_legacy_data").executes(ctx -> TransformDataCommand.execute(ctx.getSource())))
//        );
    }
}
