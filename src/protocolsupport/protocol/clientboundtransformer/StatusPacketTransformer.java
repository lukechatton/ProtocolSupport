package protocolsupport.protocol.clientboundtransformer;

import java.io.IOException;

import protocolsupport.protocol.PacketDataSerializer;
import protocolsupport.protocol.ServerConnectionChannel;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R1.ChatModifier;
import net.minecraft.server.v1_8_R1.ChatModifierSerializer;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.ChatTypeAdapterFactory;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.ServerPing;
import net.minecraft.server.v1_8_R1.ServerPingPlayerSample;
import net.minecraft.server.v1_8_R1.ServerPingPlayerSampleSerializer;
import net.minecraft.server.v1_8_R1.ServerPingSerializer;
import net.minecraft.server.v1_8_R1.ServerPingServerData;
import net.minecraft.server.v1_8_R1.ServerPingServerDataSerializer;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;

public class StatusPacketTransformer implements PacketTransformer {

    private static final Gson gson = new GsonBuilder()
    .registerTypeAdapter(ServerPingServerData.class, new ServerPingServerDataSerializer())
    .registerTypeAdapter(ServerPingPlayerSample.class, new ServerPingPlayerSampleSerializer())
    .registerTypeAdapter(ServerPing.class, new ServerPingSerializer())
    .registerTypeHierarchyAdapter(IChatBaseComponent.class, new ChatSerializer())
    .registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifierSerializer())
    .registerTypeAdapterFactory(new ChatTypeAdapterFactory())
    .create();

	@Override
	public boolean tranform(Channel channel, int packetId, Packet packet, PacketDataSerializer serializer) throws IOException {
		if (serializer.getVersion() == ServerConnectionChannel.CLIENT_1_8_PROTOCOL_VERSION) {
			return false;
		}
		if (packetId == 0x00) {
			PacketDataSerializer packetdata = new PacketDataSerializer(Unpooled.buffer(), serializer.getVersion());
			packet.b(packetdata);
			ServerPing serverPing = gson.fromJson(packetdata.readString(32767), ServerPing.class);
			serverPing.setServerInfo(new ServerPingServerData(serverPing.c().a(), ServerConnectionChannel.getVersion(channel.remoteAddress())));
			serializer.writeString(gson.toJson(serverPing));
			return true;
		}
		return false;
	}

}
