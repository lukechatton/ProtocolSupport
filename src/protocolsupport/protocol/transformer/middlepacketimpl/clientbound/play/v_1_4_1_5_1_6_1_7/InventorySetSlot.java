package protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_4_1_5_1_6_1_7;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.event.inventory.InventoryType;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ClientBoundPacket;
import protocolsupport.protocol.PacketDataSerializer;
import protocolsupport.protocol.transformer.middlepacket.clientbound.play.MiddleInventorySetSlot;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketData;
import protocolsupportbuildprocessor.annotations.NeedsNoArgConstructor;

@NeedsNoArgConstructor
public class InventorySetSlot extends MiddleInventorySetSlot<Collection<PacketData>> {

	@Override
	public Collection<PacketData> toData(ProtocolVersion version) throws IOException {
		if (player.getOpenInventory().getType() == InventoryType.ENCHANTING) {
			if (slot == 1) {
				return Collections.emptyList();
			}
			if (slot > 0) {
				slot--;
			}
		}
		PacketDataSerializer serializer = PacketDataSerializer.createNew(version);
		serializer.writeByte(windowId);
		serializer.writeShort(slot);
		serializer.writeItemStack(itemstack);
		return Collections.singletonList(new PacketData(ClientBoundPacket.PLAY_WINDOW_SET_SLOT_ID, serializer));
	}

}