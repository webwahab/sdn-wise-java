/*
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.packet;

import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.CONFIG;
import com.github.sdnwiselab.sdnwise.util.*;

/**
 * This class models a Configuration packet. This packet is sent to a node to
 * read/write a parameter or to get/set/remove a rule, a funtion, or a node
 * address alias.
 *
 * @author Sebastiano Milardo
 */
public class ConfigPacket extends NetworkPacket {

    public boolean isWrite() {
        int value = (getPayloadAt((byte) 0) & 0xFF) >> 7;

        return (value == CNF_WRITE);
    }

    // Configuration Properties
    public enum ConfigProperty {
        RESET(0, 0),
        MY_NET(1, 1),
        MY_ADDRESS(2, 2),
        PACKET_TTL(3, 1),
        RSSI_MIN(4, 1),
        BEACON_PERIOD(5, 2),
        REPORT_PERIOD(6, 2),
        RULE_TTL(7, 1),
        ADD_ALIAS(8, 2),
        REM_ALIAS(9, 1),
        GET_ALIAS(10, 1),
        ADD_RULE(11, -1),
        REM_RULE(12, 1),
        GET_RULE(13, 1),
        ADD_FUNCTION(14, -1),
        REM_FUNCTION(15, 1),
        GET_FUNCTION(16, 1);

        private final byte value;
        public final int size;

        private final static ConfigProperty[] configPropertyValues = ConfigProperty.values();

        public static ConfigProperty fromByte(byte value) {
            return configPropertyValues[value];
        }

        private ConfigProperty(int value, int size) {
            this.value = (byte) value;
            this.size = size;
        }
    }

    private final static byte CNF_WRITE = 1;

    /**
     * This constructor initialize a config packet starting from a byte array.
     *
     * @param data the byte array representing the config packet
     */
    public ConfigPacket(byte[] data) {
        super(data);
    }

    /**
     * This constructor initialize a config packet starting from a int array.
     *
     * @param data the int array representing the config packet, all int are
     * casted to byte
     */
    public ConfigPacket(int[] data) {
        super(data);
    }

    /**
     * This constructor initialize a config packet starting from a
     * NetworkPacket.
     *
     * @param data the NetworkPacket representing the beacon packet
     */
    public ConfigPacket(NetworkPacket data) {
        super(data.toByteArray());
    }

    /**
     * This constructor initialize a config packet. The type of the packet is
     * set to {@code CONFIG} and the read/write bit is set to {@code READ}.
     *
     * @param net Network ID of the packet
     * @param src source address of the packet
     * @param dst destination address of the packet
     * @param read the name of the property to read
     */
    public ConfigPacket(int net, NodeAddress src, NodeAddress dst, ConfigProperty read) {
        super(net, src, dst);
        setConfigId(read).setTyp(CONFIG);
    }

    /**
     * This constructor initialize a config packet. The type of the packet is
     * set to {@code CONFIG} and the read/write bit is set to {@code WRITE}.
     *
     * @param net the Network ID of the node
     * @param src source address
     * @param dst destination address
     * @param write the name of the property to write
     * @param value the value to be written
     */
    public ConfigPacket(int net, NodeAddress src, NodeAddress dst, ConfigProperty write, byte[] value) {
        super(net, src, dst);
        this.setConfigId(write).setWrite().setParams(value, write.size)
                .setTyp(CONFIG);
    }

    public final ConfigProperty getConfigId() {
        return ConfigProperty.fromByte((byte) (getPayloadAt((byte) 0) & 0x7F));
    }

    private ConfigPacket setWrite() {
        setPayloadAt((byte) ((getPayloadAt(0)) | (CNF_WRITE << 7)), 0);
        return this;
    }

    private ConfigPacket setConfigId(ConfigProperty id) {
        setPayloadAt(id.value, 0);
        return this;
    }

    public ConfigPacket setParams(byte[] bytes, int size) {
        if (size != -1) {
            for (int i = 0; i < size; i++) {
                setPayloadAt(bytes[i], i + 1);
            }
        } else {
            for (int i = 0; i < bytes.length; i++) {
                setPayloadAt(bytes[i], i + 1);
            }
        }
        return this;
    }

    public byte[] getParams() {
        return getPayloadFromTo(1, getPayloadSize());
    }
}
