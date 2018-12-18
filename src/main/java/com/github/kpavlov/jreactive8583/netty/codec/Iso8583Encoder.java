package com.github.kpavlov.jreactive8583.netty.codec;

import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.util.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

@ChannelHandler.Sharable
public class Iso8583Encoder extends MessageToByteEncoder<IsoMessage> {
    protected static final Logger logger = getLogger(Iso8583Encoder.class);

    private final int lengthHeaderLength;

    public Iso8583Encoder(int lengthHeaderLength) {
        this.lengthHeaderLength = lengthHeaderLength;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) {
        if (lengthHeaderLength == 0) {
            byte[] bytes = isoMessage.writeData();
            logger.debug("Sending Message: {}", HexCodec.hexEncode(bytes, 0, bytes.length));
            out.writeBytes(bytes);
        } else {
            final ByteBuffer byteBuffer = isoMessage.writeToBuffer(lengthHeaderLength);
            final byte[] bytes = byteBuffer.array();
            logger.debug("Sending Message: {}", HexCodec.hexEncode(bytes, 0, bytes.length));
            out.writeBytes(bytes);
        }
    }
}
