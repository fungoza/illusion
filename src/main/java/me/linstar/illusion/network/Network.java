package me.linstar.illusion.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class Network {
    public static final String VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);

        registrar.playBidirectional(
                BlockEntityRequestC2SPayload.TYPE,
                BlockEntityRequestC2SPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (p, c) -> {},
                        BlockEntityRequestC2SPayload::handle
                )
        );
        registrar.playBidirectional(
                BoundIllusionDataS2CPacket.TYPE,
                BoundIllusionDataS2CPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        BoundIllusionDataS2CPacket::handle,
                        (p, c) -> {}
                )
        );
        registrar.playBidirectional(
                IllusionDataS2CPacket.TYPE,
                IllusionDataS2CPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        IllusionDataS2CPacket::handle,
                        (p, c) -> {}
                )
        );

        registrar.playBidirectional(
                RemoveDataS2CPacket.TYPE,
                RemoveDataS2CPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        RemoveDataS2CPacket::handle,
                        (p, c) -> {}
                )
        );
    }
}
